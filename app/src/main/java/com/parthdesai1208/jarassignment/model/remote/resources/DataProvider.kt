package com.parthdesai1208.jarassignment.model.remote.resources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.parthdesai1208.jarassignment.orFalse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * This function takes the function as an argument with the receiver type of DataResource
 * and @return LiveData<Resource<R>> type object
 * [DataResource] class have the functions for get and store data
 */
fun <R, F> CoroutineScope.dataApi(
        data: R? = null,
        from: DataResource<R, F>.() -> Unit
): LiveData<Resource<R, F>> {
    val dataResource = DataResource<R, F>()
    from.invoke(dataResource)
    launch(Dispatchers.IO) {
        dataResource.init(data)
    }
    return dataResource.result
}


/**
 * This class have the functions to get data from the
 * Network and local db and insert the data in local db
 */
class DataResource<R, F> {
    internal val result = MutableLiveData<Resource<R, F>>()

    private var initialData: R? = null
    private var funcFromNetwork: (suspend () -> Result<R, F>)? = null
    private var funcFromDb: (suspend () -> R)? = null
    private var funcSaveDataInDb: (suspend (data: R) -> Unit)? = null

    /**
     * start executing functions
     */
    suspend fun init(data: R?) {
        initialData = data
        when {
            funcFromDb != null && funcFromNetwork != null -> {
                // 1. Load data from db. Initial data not available
                withContext(Dispatchers.Main) {
                    result.value = Resource.loading(initialData)
                }
                val liveDataFromDb = funcFromDb?.invoke()
                processNetworkRequest(liveDataFromDb)
            }
            funcFromDb == null && funcFromNetwork != null -> {
                processNetworkRequest(initialData)
            }
            funcFromDb != null && funcFromNetwork == null -> {
                withContext(Dispatchers.Main) {
                    result.value = Resource.loading(initialData)
                }
                val liveDataFromDb = funcFromDb?.invoke()
                withContext(Dispatchers.Main) {
                    liveDataFromDb?.let { result.value = Resource.success(it) }
                }
            }
        }
    }

    /**
     * process the network request
     * @param liveDataFromDb local db fresh data
     */
    private suspend fun processNetworkRequest(data: R?) {
        withContext(Dispatchers.Main) {
            result.value = (Resource.loading(data))
        }

        val liveDataFromNetwork = funcFromNetwork?.invoke()  // init network request
        withContext(Dispatchers.Main) {
            result.value = liveDataFromNetwork?.mapToResource()
        }

        if (liveDataFromNetwork?.isSuccess.orFalse()) result.value?.data?.let {
            funcSaveDataInDb?.invoke(it)
        }
    }


    /**
     * this function takes the argument as a
     * function where the network request call happen
     */
    fun fromNetwork(func: suspend () -> Result<R, F>) {
        this.funcFromNetwork = func
    }

    /**
     * @return [Resource]
     * this is the extension function of repoListResource, it maps the repoListResource to
     * their appropriate resource
     */
    private fun Result<R, F>.mapToResource(): Resource<R, F> {
        return when (this) {
            is Success -> Resource.success(data)
            is Failure -> Resource.error(initialData, throwable, error)
        }
    }
}
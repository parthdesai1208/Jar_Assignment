package com.parthdesai1208.jarassignment.model.remote.resources

import com.parthdesai1208.jarassignment.enqueueDeferredResponse
import retrofit2.Call

/**
 * This extension method enqueues the call using the coroutine and
 * return the Result instance with Success or Failure
 */
suspend fun <T : Any> Call<T>.getResult(): Result<T, APIError> {
    return try {
        val response = this.enqueueDeferredResponse().await()
        if (response.isSuccessful) {
            if (response.code() == 201) {
                val apiError = APIError("", "201", 201, null)
                Failure(null, apiError)
            } else {
                Success(response.body())
            }
        } else {
            //parse error from API
            when (response.code()) {
                401 -> {
                    val apiError = APIErrorUtils.parseError<T, APIError>(response)
                    apiError?.status_code = 401
                    Failure(null, apiError)
                }
                500 -> {
                    val apiError = APIErrorUtils.parseError<T, APIError>(response)
                    apiError?.status_code = 500
                    Failure(null, apiError)
                }
                else -> {
                    val apiError = APIErrorUtils.parseError<T, APIError>(response)
                    Failure(null, apiError)
                }
            }
        }
    } catch (throwable: Throwable) {

        Failure(throwable, APIError(null, "500", 500, null))
    }
}







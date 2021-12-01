package com.parthdesai1208.jarassignment.model

import androidx.lifecycle.LiveData
import com.parthdesai1208.jarassignment.Keys
import com.parthdesai1208.jarassignment.model.remote.ApiClient
import com.parthdesai1208.jarassignment.model.remote.resources.*
import kotlinx.coroutines.CoroutineScope

object AppRepository {
    fun CoroutineScope.getImage(page: Int): LiveData<Resource<List<UnSplashModel>, APIError>> =
        dataApi {
            // get data from network
            fromNetwork {
                ApiClient.service.getImageList(Keys.apiKey(),page).getResult()
            }
        }
}
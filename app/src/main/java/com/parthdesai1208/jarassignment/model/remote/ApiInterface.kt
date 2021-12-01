package com.parthdesai1208.jarassignment.model.remote

import com.parthdesai1208.jarassignment.Keys
import com.parthdesai1208.jarassignment.model.UnSplashModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {


    @GET("photos/")
    fun getImageList(@Query("client_id") client_id : String, @Query("page") page : Int) : Call<List<UnSplashModel>>
}
package com.parthdesai1208.jarassignment.model.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    lateinit var retrofit: Retrofit


    val service: ApiInterface by lazy {
        val builder = Retrofit.Builder()
            .baseUrl(Helper.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
        val client = OkHttpClient.Builder()
        client.connectTimeout(60, TimeUnit.SECONDS)
        client.readTimeout(60, TimeUnit.SECONDS)
        client.writeTimeout(60, TimeUnit.SECONDS)


        /**
         * Interceptor will intercept the request before it send to the server
         * Hereby it will add Authorization and BarerToken in header for security reason
         * Every time it will generate new random string and new encrypted token
         * */
        val interceptor = Interceptor {
            var request = it.request()
            /*try {
                val newBuilder = request.newBuilder()
                val randomStr = randomString(53)
                val token = Base64.encodeToString(
                    getDeviceBearerToken(randomStr).toByteArray(),
                    Base64.NO_WRAP
                )
                //add Authorization in header of the request
                newBuilder.addHeader("Authorization", randomStr)
                //add BarerToken in header of the request
                newBuilder.addHeader("BarerToken", token)
                request = newBuilder.build()
            } catch (e: Exception) {
            }*/
            val response = it.proceed(request)
            response
        }
        client.addInterceptor(interceptor)
        /**
         * End of interceptor code
         * */


        /*if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(loggingInterceptor)
            client.addInterceptor(ChuckerInterceptor.Builder(App.getApplicationContext()).build())
        }*/

        builder.client(client.build())
        retrofit = builder.build()
        retrofit.create(ApiInterface::class.java)
    }

}
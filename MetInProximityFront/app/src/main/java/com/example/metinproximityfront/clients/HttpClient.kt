package com.example.metinproximityfront.clients

import com.example.metinproximityfront.api.ApiInterface
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import retrofit2.Retrofit

object HttpClient {

    val BASE_URL = "https://localhost:7238/api"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            //.addConverterFactory(GsonConverterFactory.create())  // Used to convert JSON response to Kotlin objects
            .build()
    }

    val apiClient: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }

}
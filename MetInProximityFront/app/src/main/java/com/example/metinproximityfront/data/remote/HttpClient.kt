package com.example.metinproximityfront.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpClient {

    val BASE_URL = "https://localhost:7238/api"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Used to convert JSON response to Kotlin objects
            .build()
    }

}
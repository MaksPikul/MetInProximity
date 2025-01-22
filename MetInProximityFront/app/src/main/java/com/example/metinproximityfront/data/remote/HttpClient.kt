package com.example.metinproximityfront.data.remote

import retrofit2.Retrofit

object HttpClient {

    val BASE_URL = "https://localhost:7238/api"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            //.addConverterFactory(GsonConverterFactory.create())  // Used to convert JSON response to Kotlin objects
            .build()
    }

}
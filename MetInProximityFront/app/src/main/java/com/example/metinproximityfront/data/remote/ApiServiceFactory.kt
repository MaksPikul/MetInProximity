package com.example.metinproximityfront.data.remote

import retrofit2.Retrofit

//import com.example.metinproximityfront.data.remote.HttpClient.retrofit


inline fun <reified T> ApiServiceFactory(retrofit : Retrofit): T {
    return retrofit.create(T::class.java)
}
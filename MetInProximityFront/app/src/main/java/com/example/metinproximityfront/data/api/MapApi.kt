package com.example.metinproximityfront.data.api

import android.graphics.Bitmap
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

interface MapApi {

    @GET("map")
    suspend fun GetMapTilesApi(
        @Query("long") long: Double,
        @Query("lat") lat: Double,
        @Header("Authorization") authHeader: String
    ) : Response<Bitmap>



}
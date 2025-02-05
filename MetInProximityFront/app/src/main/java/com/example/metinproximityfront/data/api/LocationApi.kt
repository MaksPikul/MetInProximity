package com.example.metinproximityfront.data.api


import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

interface LocationApi{

    @PUT("location")
    suspend fun PutUserLocation(
        @Query("long") long: Double,
        @Query("lat") lat: Double,
        @Header("Authorization") authHeader: String
    ) : Response<Unit>


}
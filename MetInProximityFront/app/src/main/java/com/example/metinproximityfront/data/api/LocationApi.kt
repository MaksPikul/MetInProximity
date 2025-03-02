package com.example.metinproximityfront.data.api


import com.example.metinproximityfront.data.entities.location.LocResObj
import com.example.metinproximityfront.data.entities.location.LocationObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

interface LocationApi{

    @PUT("location")
    suspend fun PutUserLocation(
        // THIS NEEDS CHANGING TO A BODY
        @Body locObj : LocationObject,
        @Header("Authorization") authHeader: String
    ) : Response<LocResObj>

}
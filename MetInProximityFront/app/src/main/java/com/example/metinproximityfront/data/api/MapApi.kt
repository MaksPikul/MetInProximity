package com.example.metinproximityfront.data.api

import com.example.metinproximityfront.data.entities.account.StringRes
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Query

interface MapApi {
    @GET("location/map")
    suspend fun getMapApi(
        @Query("lon") lon : Double,
        @Query("lat") lat : Double,
        @Header("Authorization") accessToken: String
    ): Response<StringRes>
}
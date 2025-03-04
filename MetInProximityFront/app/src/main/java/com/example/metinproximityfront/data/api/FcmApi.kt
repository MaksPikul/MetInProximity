package com.example.metinproximityfront.data.api

import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Query

interface FcmApi {
    @PATCH("user/fcm/token")
    suspend fun UpdateUserFcm(
        @Query("token") token : String,
        @Header("Authorization") accessToken: String
    ): Response<Unit>
}
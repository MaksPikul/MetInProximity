package com.example.metinproximityfront.data.api

import com.example.metinproximityfront.data.entities.account.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RefreshTokenApi {

    @POST("account/refresh")
    suspend fun RefreshAccessToken(
        @Query("refreshToken") refreshToken: String
    ) : Response<AuthResponse>

}
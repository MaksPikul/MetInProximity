package com.example.metinproximityfront.data.api

import com.example.metinproximityfront.data.entities.account.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshTokenApi {

    @POST("account/refresh")
    suspend fun RefreshAccessToken(
        @Body() refreshToken: String
    ) : Response<AuthResponse>

}
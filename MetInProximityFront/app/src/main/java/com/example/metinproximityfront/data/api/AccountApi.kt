package com.example.metinproximityfront.data.api

import com.example.metinproximityfront.data.entities.account.AuthRequest
import com.example.metinproximityfront.data.entities.account.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AccountApi  {

    @POST("account/oauth/{provider}")
    suspend fun Authenticate(
        @Path("provider") provider: String, // Whether to handle google, microsoft, etc
        @Body() authRequest: AuthRequest
    ): Response<AuthResponse>
}
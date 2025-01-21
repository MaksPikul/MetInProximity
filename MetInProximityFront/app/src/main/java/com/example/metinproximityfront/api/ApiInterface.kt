package com.example.metinproximityfront.api

import com.example.metinproximityfront.api.entities.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @POST("account/oauth/{provider}")
    suspend fun Authenticate(
        @Path("provider") provider: String, // Wether to handle google, microsoft, etc
        @Query("code") code: String // code provided by provider
    ): Response<AuthResponse>



}
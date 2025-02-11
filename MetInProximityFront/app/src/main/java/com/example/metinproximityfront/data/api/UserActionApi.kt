package com.example.metinproximityfront.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface UserActionApi {

    @GET("user")
    suspend fun GetOpenForPrivateUsers(
        @Query("longitude") longitude : Double,
        @Query("latitude") latitude : Double,
        @Header("Authorization") accessToken: String
    ) : Response<Unit> //THIS NEEDS TO CHANGE

    @PATCH("user/visibility")
    suspend fun UpdateOpenForPrivate(
    ): Response<String>

    @PATCH("user/visibility")
    suspend fun UpdateUserFcm(
        @Query("token") token : String,
        @Header("Authorization") accessToken: String
    ): Response<Unit>


}
package com.example.metinproximityfront.data.api

import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.entities.users.ChatUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface UserApi {

    @GET("user")
    suspend fun GetPrivateUserApi(
        @Body() locObj : LocationObject,
        @Header("Authorization") accessToken: String
    ) : Response<List<ChatUser>> //THIS NEEDS TO CHANGE

    @PATCH("user/visibility")
    suspend fun ChangeVisibilityApi(
        @Header("Authorization") accessToken: String
    ): Response<String>

}
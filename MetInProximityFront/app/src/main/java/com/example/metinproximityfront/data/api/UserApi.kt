package com.example.metinproximityfront.data.api

import com.example.metinproximityfront.data.entities.account.StringRes
import com.example.metinproximityfront.data.entities.users.ChatUser
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Query

interface UserApi {

    @GET("user/private")
    suspend fun GetPrivateUserApi(
        @Query("lon") lon : Double,
        @Query("lat") lat : Double,
        @Header("Authorization") accessToken: String
    ) : Response<List<ChatUser>>

    @PATCH("user/visibility")
    suspend fun ChangeVisibilityApi(
        @Header("Authorization") accessToken: String
    ): Response<StringRes>

}
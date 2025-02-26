package com.example.metinproximityfront.data.api

import com.example.metinproximityfront.data.entities.message.MsgReqObject
import com.example.metinproximityfront.data.entities.message.MsgResObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface MessageApi {

    @POST("message/public")
    suspend fun SendPublicMessage(
        @Body() msgObj: MsgReqObject,
        @Header("Authorization") authHeader: String
    ) : Response<MsgResObject>

    @POST("message/private")
    suspend fun SendPrivateMessage(
        @Body() msgObj: MsgReqObject,
        @Header("Authorization") authHeader: String
    ) : Response<MsgResObject>
}
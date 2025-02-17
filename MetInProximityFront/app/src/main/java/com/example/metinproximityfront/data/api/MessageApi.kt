package com.example.metinproximityfront.data.api

import com.example.metinproximityfront.data.entities.message.MsgReqObject
import com.example.metinproximityfront.data.entities.message.MsgResObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

interface MessageApi {

    @PUT("message/public")
    suspend fun SendPublicMessage(
        // THIS NEEDS CHANGING TO A BODY
        @Body() msgObj: MsgReqObject,
        @Header("Authorization") authHeader: String
    ) : Response<MsgResObject>

    @PUT("message/private")
    suspend fun SendPrivateMessage(
        @Body() msgObj: MsgReqObject,
        @Header("Authorization") authHeader: String
    ) : Response<MsgResObject>
}
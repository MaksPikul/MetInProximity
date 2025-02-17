package com.example.metinproximityfront.data.repositories

import com.example.metinproximityfront.data.api.ApiTokenWrapper
import com.example.metinproximityfront.data.api.MessageApi
import com.example.metinproximityfront.data.entities.message.MsgReqObject
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageRepository(
    private val apiTokenWrapper: ApiTokenWrapper
) {

    private val messageApi: MessageApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    suspend fun SendMessage(
        msgObj : MsgReqObject
        // onSuccess : ()-> Unit
    ) : MsgResObject? {
        return try {
            apiTokenWrapper.callApiWithToken { token: String ->
                messageApi.SendPublicMessage(msgObj, token) // Extract response body
            }
        } catch (e: Exception) {
            // TODO : Throw Error? Idk man
            null
        }
    }


}
package com.example.metinproximityfront.services.message

import com.example.metinproximityfront.services.preference.SharedStoreService
import com.google.firebase.messaging.RemoteMessage

class MsgStoreService(
    private val sharedStore : SharedStoreService
)
{
    fun storeMessage(message : RemoteMessage) : String{

        val data = message.data

        val userId = data.get("UserId") // owner of message
        val isPublic = data.get("isPublic")
        val recipientId = data.get("RecipientId")

        var key = "public-$userId"
        if (isPublic == "false"){
            key = "private-$userId-$recipientId"
        }

        sharedStore.saveIntoPref(key, data.toString())

        return key
    }

    //fun mapRemoteToLocal(remoteMessage : RemoteMessage) :MessageObject {

    //}
}
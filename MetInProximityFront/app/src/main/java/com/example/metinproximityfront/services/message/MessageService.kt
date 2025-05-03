package com.example.metinproximityfront.services.message

import android.util.Log
import com.example.metinproximityfront.services.location.LocationServiceBinder
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.entities.account.User
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.repositories.MessageRepository
import com.example.metinproximityfront.factories.MessageFactory
import com.example.metinproximityfront.services.preference.IStoreService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageService(
    private val sharedStore: IStoreService,
    private val msgRepo: MessageRepository? = null,
    private val msgLocBinder : LocationServiceBinder,
){
    private val _messages = MutableStateFlow<List<MsgResObject>>(emptyList()) // StateFlow for UI
    val messages: StateFlow<List<MsgResObject>> = _messages // Expose immutable StateFlow

    fun storeMessage(msg: MsgResObject, otherId: String? = null): String {

        var key = Constants.PUBLIC_CHAT_KEY(User.userData.value.userId)
        if (!msg.isPublic) {
            key = Constants.PRIVATE_CHAT_KEY(User.userData.value.userId, otherId)
        }
        val json = Gson().toJson(_messages.value + msg)
        sharedStore.saveIntoPref(key, json)

        return key
    }

    fun retrieveMessages(
        latestMsg: MsgResObject? = null,
        passedKey: String? = null
    ): List<MsgResObject> {
        var key = passedKey

        if (key == null) {
            key = if (latestMsg?.isPublic != true && latestMsg?.recipientId != null) {
                Constants.PRIVATE_CHAT_KEY(latestMsg.recipientId, latestMsg.userId)
            } else {
                User.userData.value?.let { Constants.PUBLIC_CHAT_KEY(it.userId) }
            }
        }
        val json = key?.let { sharedStore.getFromPref(it) }
        if (json.isNullOrEmpty()) {
            // Handle the case where json is null or empty
            _messages.value = emptyList()
            return emptyList()
        }

        val type = object : TypeToken<List<MsgResObject>>() {}.type
        val messagesList: List<MsgResObject> = Gson().fromJson(json, type)

        _messages.value = messagesList
        return messagesList
    }

    fun  sendMessage(
        textToSend: String,
        chatUser: ChatUser? = null
    ) : String? {
        return try {
            CoroutineScope(Dispatchers.IO).launch {

                val locObj: LocationObject = msgLocBinder.getCurrentLocation()

                val msgObj = MessageFactory.CreateMsg(
                    textToSend,
                    locObj.lon,
                    locObj.lat,
                )

                val result: MsgResObject?
                if (chatUser == null) {
                    result = msgRepo?.SendPublicMessageRepo(msgObj)
                } else {
                    Log.e("Recipient Id", chatUser.id)
                    msgObj.msgRecipientId = chatUser.id
                    result = msgRepo?.SendPrivateMessageRepo(msgObj)
                }

                result?.let { msg ->
                    Log.i(
                    "MessageSerivce",
                    "Message sent!" + msg.body
                    )

                    if (msg.recipientId != null) {
                        var privateKey = storeMessage(msg, msg.recipientId)
                        retrieveMessages(msg, privateKey)

                    }
                    else {
                        var publicKey = storeMessage(msg)
                        retrieveMessages(msg, publicKey)
                    }

                }
            }
            null
        } catch (ex: Throwable) {
            "ERROR"
        }
    }
}
package com.example.metinproximityfront.services.message

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.example.metinproximityfront.services.location.LocationServiceBinder
import com.example.metinproximityfront.config.Constants
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
import java.util.Date

class MessageService(
    private val sharedStore: IStoreService,
    private val msgRepo: MessageRepository? = null,
    private val msgLocBinder : LocationServiceBinder,
)
{
    // Ik this class be using the observer method someone
    // Does it without the list of observers, signalR updater, message service observer
    private val _messages = MutableStateFlow<List<MsgResObject>>(emptyList()) // StateFlow for UI
    val messages: StateFlow<List<MsgResObject>> = _messages // Expose immutable StateFlow

    fun storeMessage(msg: MsgResObject): String {

        var key = Constants.PUBLIC_CHAT_KEY/*-${msg.UserId}"*/
        if (!msg.isPublic) {
            key = Constants.PRIVATE_CHAT_KEY(msg.userId, msg.recipientId)
        }
        val json = Gson().toJson(_messages.value + msg)
        sharedStore.saveIntoPref(key, json)

        return key
    }

    fun retrieveMessages(
        latestMsg: MsgResObject? = null,
        passedkey: String? = null
    ): List<MsgResObject> {
        var key = passedkey

        if (key == null) {
            key = if (latestMsg?.isPublic != true && latestMsg?.recipientId != null) {
                Constants.PRIVATE_CHAT_KEY(latestMsg.userId, latestMsg.recipientId)
            } else {
                Constants.PUBLIC_CHAT_KEY
            }
        }
        val json = sharedStore.getFromPref(key)
        Log.i("Retrieve", json.toString())
        if (json.isNullOrEmpty()) {
            // Handle the case where json is null or empty
            _messages.value = emptyList() // You can return empty list as a fallback
            return emptyList()
        }

        val type = object : TypeToken<List<MsgResObject>>() {}.type
        val messagesList: List<MsgResObject> = Gson().fromJson(json, type)

        _messages.value = messagesList
        return messagesList
    }

    fun sendMessage(
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

                val dummyMessage = MsgResObject(
                    body = "Hello, this is a test message!",
                    userId = "User123",
                    isPublic = true,
                    recipientId = null,
                    timestamp = Date()
                )
                storeMessage(dummyMessage)
                retrieveMessages(dummyMessage)
                /*
                val result: MsgResObject?
                if (chatUser == null) {
                    result = msgRepo?.SendPublicMessageRepo(msgObj)
                } else {
                    msgObj.recipientId = chatUser.Id
                    result = msgRepo?.SendPrivateMessageRepo(msgObj)
                }

                result?.let { msg ->
                    Log.i("SendMessage", msg.body)
                    storeMessage(msg)

                    retrieveMessages(msg)
                }
                 */
            }
            null
        } catch (ex: Throwable) {
            "ERROR"
        }
    }
}
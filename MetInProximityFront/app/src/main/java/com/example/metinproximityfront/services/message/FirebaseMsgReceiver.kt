package com.example.metinproximityfront.services.message

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.RefreshTokenApi
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import com.example.metinproximityfront.data.repositories.FcmRepository
import com.example.metinproximityfront.services.location.LocationService
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import com.example.metinproximityfront.services.preference.SharedStoreService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class FirebaseMsgReceiver : FirebaseMessagingService() {

    private lateinit var publicStore : SharedStoreService

    private lateinit var privateStore : EncryptedStoreService

    private lateinit var fcmRepo : FcmRepository

    private val refreshTokenApi: RefreshTokenApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    override fun onCreate() {
        super.onCreate()

        publicStore = SharedStoreService(
            this.applicationContext,
            Constants.MsgSharedStoreServiceFileName
        )
        privateStore = EncryptedStoreService(
            this.applicationContext
        )
        fcmRepo = FcmRepository(
            ApiTokenWrapper(privateStore, refreshTokenApi)
        )
    }

    override fun onNewToken(fcmToken: String) {
        super.onNewToken(fcmToken)
        CoroutineScope(Dispatchers.IO).launch {
            fcmRepo.UpdateUserFcm(fcmToken)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.i("s", "s")
        message.data.forEach{Log.i("E", it.toString())}

        val msgObj : MsgResObject = mapRemoteToMsgRes(message)

        val key : String = this.storeMessage(msgObj)

        val intent = Intent(this, /* TODO THIS HAS TO CHANGE */LocationService::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        this.createAndRunNotification()
    }

    // Hate that i have to repeat code,
    // But idk how else to do this :/
    // This would be simpler
    private fun storeMessage(msg: MsgResObject): String {

        var key = Constants.PUBLIC_CHAT_KEY/*-${msg.UserId}"*/
        if (!msg.isPublic) {
            key = Constants.PRIVATE_CHAT_KEY(msg.userId, msg.recipientId)
        }

        val messages = retrieveMessages(msg)

        val json = Gson().toJson(messages + msg)
        publicStore.saveIntoPref(key, json)

        return key
    }

    private fun retrieveMessages(
        msg: MsgResObject,
    ): List<MsgResObject> {

        val key = if (msg.isPublic != true && msg.recipientId != null) {
            Constants.PRIVATE_CHAT_KEY(msg.userId, msg.recipientId)
        } else {
            Constants.PUBLIC_CHAT_KEY
        }

        val json = publicStore.getFromPref(key)

        val type = object : TypeToken<List<MsgResObject>>() {}.type
        val messagesList: List<MsgResObject> = Gson().fromJson(json, type)

        return messagesList
    }

    private fun mapRemoteToMsgRes(
        message : RemoteMessage
    ) : MsgResObject{

        return MsgResObject(
            body = message.data.get("Body") ?: "",
            userId = message.data.get("UserId") ?: "", // owner of message
            isPublic = message.data.get("isPublic").toBoolean(),
            recipientId = message.data.get("RecipientId"),
            timestamp = Date() // Parse Date From String
        )
    }

    // TODO : Make Into a Service
    /*
        https://developer.android.com/develop/ui/views/notifications/build-notification
     */
    @SuppressLint("MissingPermission")
    private fun createAndRunNotification(
    ){
        var channelId = "firebase_channel"

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Message Received")
            .setContentText("Check who sent you a message!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(1, notification)
    }
}


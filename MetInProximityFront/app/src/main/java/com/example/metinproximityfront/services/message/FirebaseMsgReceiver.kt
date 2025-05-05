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
import com.example.metinproximityfront.app.MainActivity
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.RefreshTokenApi
import com.example.metinproximityfront.data.entities.account.User
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
import kotlin.random.Random

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

        Log.i("Push Notif data", "")
        message.data.forEach{
            Log.i( it.key.toString() , it.value.toString() )
        }

        val msgObj : MsgResObject = mapRemoteToMsgRes(message)

        val key : String = this.storeMessage(msgObj)

        val notifIntent = CreateIntent(key)

        this.createAndRunNotification(msgObj)
    }

    // Hate that i have to repeat code,
    // But idk how else to do this :/
    // This would be simpler
    private fun storeMessage(msg: MsgResObject): String {

        var key = Constants.PUBLIC_CHAT_KEY(User.userData.value.userId)/*-${msg.UserId}"*/
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
            Constants.PUBLIC_CHAT_KEY(User.userData.value.userId)
        }

        val json = key?.let { publicStore.getFromPref(it) }
        if (json.isNullOrEmpty()) {
            // Handle the case where json is null or empty
            return emptyList()
        }

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

    private fun CreateIntent(key: String) : PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = Random.nextInt().toString()
            putExtra("destination", key) // Pass route name to MainActivity
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return pendingIntent
    }

    @SuppressLint("MissingPermission")
    private fun createAndRunNotification(
        msgObj : MsgResObject
    ){
        var channelId = "firebase_channel"

        var contentTitle = if (msgObj.isPublic) {
            "You Received a Public Message!"
        } else {
            "You Received a Private Message!"
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(contentTitle)
            .setContentText("See what they sent!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(1, notification)
    }
}


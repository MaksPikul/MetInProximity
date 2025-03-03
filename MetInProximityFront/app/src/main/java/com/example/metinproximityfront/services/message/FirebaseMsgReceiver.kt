package com.example.metinproximityfront.services.message

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
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


    private val publicStore = SharedStoreService(
        this,
        Constants.MsgSharedStoreServiceFileName
    )
    private val privateStore = EncryptedStoreService(
        this
    )
    private val fcmRepo = FcmRepository(
        ApiTokenWrapper(privateStore)
    )

    override fun onNewToken(fcmToken: String) {
        super.onNewToken(fcmToken)
        CoroutineScope(Dispatchers.IO).launch {
            fcmRepo.UpdateUserFcm(fcmToken)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val msgObj : MsgResObject = mapRemoteToMsgRes(message)

        val key : String = this.storeMessage(msgObj)

        // TODO: Use Key to create route for intent based on public or private
        val intent = Intent(this, /* TODO THIS HAS TO CHANGE */LocationService::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

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
    private fun createAndRunNotification(

    ){
        var channelId = "10"

        // Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MetInProximity"
            val descriptionText = "Push Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // tap action
        val intent = Intent(this, /* TODO THIS HAS TO CHANGE */LocationService::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // notification
        var builder = NotificationCompat.Builder(this, channelId)
            //.setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("New message")
            //.setContentText(newMsg)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }
}


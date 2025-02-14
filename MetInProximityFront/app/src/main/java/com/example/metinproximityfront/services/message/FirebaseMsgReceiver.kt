package com.example.metinproximityfront.services.message

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.metinproximityfront.R
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.ApiTokenWrapper
import com.example.metinproximityfront.data.api.UserActionApi
import com.example.metinproximityfront.data.repositories.UserActionRepository
import com.example.metinproximityfront.services.locaction.LocationService
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import com.example.metinproximityfront.services.preference.SharedStoreService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMsgReceiver : FirebaseMessagingService() {

    override fun onNewToken(fcmToken: String) {
        super.onNewToken(fcmToken)

        // These two services initiated twice
        val storeService = EncryptedStoreService(applicationContext)

        val repo = UserActionRepository(
            ApiTokenWrapper(storeService)
        )
        // ----------------------------------

        repo.UpdateUserFcm(fcmToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val msgStore = MsgStoreService( SharedStoreService(
            applicationContext,
            Constants.MsgSharedStoreServiceFileName
        ) )

        val key : String = msgStore.storeMessage(message)

        // TODO: Use Key to create route for intent based on public or private

        this.createAndRunNotification()
    }

    // TODO : Make Into a Service
    /*
        https://developer.android.com/develop/ui/views/notifications/build-notification
     */
    private fun createAndRunNotification(

    ){
        var channelId = "10"

        // Build Channel
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

        // Build tap action
        val intent = Intent(this, /* TODO THIS HAS TO CHANGE */LocationService::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Build notification
        var builder = NotificationCompat.Builder(this, channelId)
            //.setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("New message")
            //.setContentText(newMsg)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Run Notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }
}


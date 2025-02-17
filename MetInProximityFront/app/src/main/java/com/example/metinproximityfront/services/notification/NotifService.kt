package com.example.metinproximityfront.services.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.content.Context
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.metinproximityfront.services.locaction.LocationService
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import java.nio.channels.Channel
/*
class NotifService(
    private val context : Context
) {

    fun CreateNotfication(
        channelName : String,
        channelDesc : String,
        channelId : String,

        contentTitle: String,
        contentText : String? = null,
        icon : String,

        actionTitle : String? = null,
        intent : PendingIntent? = null

    ) : Notification {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelName
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

        var builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(icon)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .addAction(0, actionTitle, intent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return builder.build()

    }

    fun RunNotification(
        notification : Notification
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)

    }

    fun locationIntent() : PendingIntent {
        val intent = Intent(context, /* TODO THIS HAS TO CHANGE */LocationService::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun FirebaseIntent() : PendingIntent{
        val stopIntent = Intent(context, LocationService::class.java)
        stopIntent.setAction( "STOP_SERVICE")

        return PendingIntent.getService(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
/*
    val notification = notifService.CreateNotfication(
        "MetInProximity",
        "Device open to receive messages for MetInProximity",
        "4523",
        "MetInProximity",
        "Listening for Messages",
        "icon placeholder",
        "Stop",
        intent
    )*/
}
 */
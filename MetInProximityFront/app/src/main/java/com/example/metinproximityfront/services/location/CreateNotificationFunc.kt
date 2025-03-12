package com.example.metinproximityfront.services.location

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

fun CreateNotification(
    context: Context
): Notification {
    //https://stackoverflow.com/questions/20857120/what-is-the-proper-way-to-stop-a-service-running-as-foreground

    val stopIntent : Intent = Intent(context, LocationService::class.java)
    stopIntent.setAction( "STOP_SERVICE") /*Constants.ACTION.STOPFOREGROUND_ACTION*/
    context.startService(stopIntent);

    val pendingStopIntent = PendingIntent.getService(context, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

    val channelId = "4523"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "MetInProximity"
        var description = "Device open to receive messages for MetInProximity"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, name, importance)
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val notification: Notification = NotificationCompat
        .Builder(context, channelId)
        .setContentTitle("MetInProximity")
        .setContentText("Listening for Messages")
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
        .addAction(0, "Stop", pendingStopIntent)
        .build()

    return notification
}
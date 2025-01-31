package com.example.metinproximityfront.services.loc


import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.metinproximityfront.app.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class LocationService : Service() {
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_LOW_POWER, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateDistanceMeters(100f)
            .build();

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                p0?.lastLocation?.let { location ->
                    // TODO : SEND LOC TO Azure Event Hub -> AZURE COSMOS DB
                }

            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                , "Location Service", NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, )
            //.setContentTitle("MetInProximity")
            //.setContentText("Listening for Messages")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .addAction(0, "Stop", )
            .build()

        startForeground(NOTIFICATION_ID, notification)


        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopSelf()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null // No binding is needed for this service
    }
}
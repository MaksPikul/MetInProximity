package com.example.metinproximityfront.services.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.repositories.LocationRepo
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService: Service() {

    private lateinit var locationClient: LocationClient
    private lateinit var locationRepo : LocationRepo

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()


        locationRepo = LocationRepo(
            ApiTokenWrapper(
                EncryptedStoreService(applicationContext)
            )
        )

        locationClient = LocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext),
        )
    }

    // This Changed, Looks Cleaner
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action) {
            Constants.START_LOC_SERVICE -> start()
            Constants.STOP_LOC_SERVICE -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }

    private fun start() {

        locationClient
            .getLocationUpdates()
            .catch { e -> e.printStackTrace() }
            .onEach{ location ->
                locationRepo.UpdateUserLocation(location)
            }
            .launchIn(serviceScope)

        val notification = this.CreateNotification() // Create Notification Service?

        startForeground(4523, notification)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // No binding is needed for this service
    }

    suspend fun GetCurrentLocation() : LocationObject {
        return locationClient.GetCurrentLocation()
    }

    private fun CreateNotification(): Notification {
        //https://stackoverflow.com/questions/20857120/what-is-the-proper-way-to-stop-a-service-running-as-foreground

        val stopIntent : Intent = Intent(this, LocationService::class.java)
        stopIntent.setAction( "STOP_SERVICE") /*Constants.ACTION.STOPFOREGROUND_ACTION*/
        startService(stopIntent);

        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val channelId = "4523"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MetInProximity"
            var description = "Device open to receive messages for MetInProximity"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, name, importance)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat
            .Builder(this, channelId)
            .setContentTitle("MetInProximity")
            .setContentText("Listening for Messages")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .addAction(0, "Stop", pendingStopIntent)
            .build()

        return notification
    }
}
package com.example.metinproximityfront.services.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Base64
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.RefreshTokenApi
import com.example.metinproximityfront.data.entities.location.LocResObj
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import com.example.metinproximityfront.data.repositories.LocationRepo
import com.example.metinproximityfront.interfaces.LocObserver
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService: Service() {

    lateinit var locationClient: LocationClient
    val locObsMan = LocObserverManager() // theres gonna be like one observer in this list

    private lateinit var locationRepo : LocationRepo

    private val binder = LocationServiceBinder(this)

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        locationRepo = LocationRepo(
            ApiTokenWrapper(
                EncryptedStoreService(applicationContext),
                ApiServiceFactory(publicRetrofit)
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
        locationClient.getLocationUpdates()
            .catch { e -> e.printStackTrace() }
            .onEach{ location ->

                val result : LocResObj? = locationRepo.UpdateUserLocationRepo(location)


                result?.mapImage?.let { mapImage ->

                    locObsMan.notifyObservers(mapImage)
                }
            }.launchIn(serviceScope)

        val notification = CreateNotification(this) // Method in file CreateNotificationFunc

        startForeground(4523, notification)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocationServiceBinder(private val service: LocationService) : Binder() {
        fun getService(): LocationService = service
    }
}
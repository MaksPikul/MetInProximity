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
import androidx.core.app.NotificationCompat
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.entities.location.LocResObj
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
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

    private lateinit var locationClient: LocationClient
    private lateinit var locationRepo : LocationRepo

    private val binder = LocationServiceBinder(this)

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // theres gonna be like one in this list
    private val locObsMan = LocObserverManager()

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

                // logic to check if i need to fetch more map data or not

                val result : LocResObj? = locationRepo.UpdateUserLocationRepo(location)

                result?.mapImage?.let { mapImage ->
                    val mapImageBase64: String? = mapImage
                    val decodedString: ByteArray = Base64.decode(mapImageBase64, Base64.DEFAULT)
                    val mapBitmap : Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                    locObsMan.notifyObservers(mapBitmap)
                }
            }
            .launchIn(serviceScope)

        val notification = this.CreateNotification() // Create Notification Service?

        startForeground(4523, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun registerObserver(observer: LocObserver) {
        locObsMan.registerObserver(observer)
    }

    fun unregisterObserver(observer: LocObserver) {
        locObsMan.unregisterObserver(observer)
    }

    suspend fun GetCurrentLocation() : LocationObject {
        return locationClient.GetCurrentLocation()
    }

    inner class LocationServiceBinder(private val service: LocationService) : Binder() {
        fun getService(): LocationService = service
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
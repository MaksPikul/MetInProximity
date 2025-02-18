package com.example.metinproximityfront.services.locaction


import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.repositories.LocationRepo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class LocationService(
    //private val appContext: Context,
    //private val locationRepo : LocationRepo
) : Service() {



    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //private val notifService = NotifService(this)

    override fun onCreate() {
        super.onCreate()
        Log.e("LocService", "Service Created")

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                p0?.lastLocation?.let { location ->
                    //locationRepo.UpdateUserLocation(location)
                    Log.i("Location", location.longitude.toString())
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val notification = this.CreateNotification() // Create Notification Service?
         // Create a permission checking Wrapper?

        startForeground(4523, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals("STOP_SERVICE")){
            stopSelf()
        }

        this.StartLocationRequests()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopSelf()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // Clean the permissions
    @SuppressLint("MissingPermission")
    suspend fun GetCurrentLocation(): LocationObject {
        return suspendCoroutine { continuation ->

            if (/*
                ContextCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED

                &&
                ContextCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED
                */
                true
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(LocationObject(location.longitude, location.latitude))
                    } else {
                        continuation.resumeWithException(Exception("Error Getting Current Location"))
                    }
                }
            } else {
                continuation.resumeWithException(Exception("Missing location permissions"))
            }
        }
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

    private fun StartLocationRequests() {
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_LOW_POWER, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateDistanceMeters(100f)
            .build();

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION, // TODO : CHECK for moar permissions?
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        else {
            // tODO : Request | stop app
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // No binding is needed for this service

    }
}
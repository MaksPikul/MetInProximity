package com.example.metinproximityfront.services.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


class LocationClient(
    private val context: Context,
    private val fusedClient: FusedLocationProviderClient,
) {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE_LOCATION
    )

    /*
        https://developer.android.com/kotlin/flow
     */
    fun getLocationUpdates(): Flow<Location> {
        return callbackFlow {
            if(
                permissions.all {
                    ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
                }
            ) {
                throw Exception("Missing Permissions") // TODO Clean exceptions, Should i Make Custom Exceptions for each exception?
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if(!isGpsEnabled && !isNetworkEnabled) {
                throw Exception("GPS is disabled")
            }

            val locationRequest = LocationRequest
                .Builder(0)
                .setWaitForAccurateLocation(false)
                .setPriority(Priority.PRIORITY_LOW_POWER)
                .setMinUpdateDistanceMeters(100f)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    p0.lastLocation?.let { location ->
                        Log.i("Location", location.longitude.toString())
                        launch { send(location) }
                    }
                }
            }

            fusedClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                fusedClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    /*
        Need to fix binding, so that i can use it in message service, until then, commented out

    suspend fun GetCurrentLocation(): LocationObject {
        return suspendCoroutine { continuation ->
            if (
                permissions.all {
                    ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
                }
            ){
                continuation.resumeWithException(Exception("Error Getting Permissions"))
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if(!isGpsEnabled && !isNetworkEnabled) {
                continuation.resumeWithException(Exception("GPS is disabled"))
            }

            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(
                        LocationObject(location.longitude, location.latitude)
                    )
                } else {
                    continuation.resumeWithException(Exception("Error Getting Current Location"))
                }
            }
        }
    }
    */

}
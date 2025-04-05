package com.example.metinproximityfront.services.location

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.interfaces.LocObserver


/*
    https://developer.android.com/develop/background-work/services/bound-services
 */
class LocationServiceBinder (private val context: Context) {

    private var locationService: LocationService? = null
    private var isBound = false

    private var onBoundCallback: (() -> Unit)? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocationServiceBinder
            locationService = binder.getService()
            isBound = true
            onBoundCallback?.invoke()
            Log.i("MsgLocBinder", "did the binding")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            isBound = false
        }
    }

    fun bindLocationService(onBound: () -> Unit) {
        val intent = Intent(context, LocationService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        this.onBoundCallback = onBound
    }

    // These two are only used by binder, hence why they're public, less stuff in location service
    suspend fun getCurrentLocation(): LocationObject {
        return locationService?.locationClient!!.GetCurrentLocation()
    }

    fun registerObserver(locObserver: LocObserver?) {
        locationService?.locObsMan?.registerObserver(locObserver)
    }
}
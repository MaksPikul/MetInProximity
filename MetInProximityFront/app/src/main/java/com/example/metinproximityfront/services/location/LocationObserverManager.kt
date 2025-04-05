package com.example.metinproximityfront.services.location

import android.graphics.Bitmap
import android.util.Log
import com.example.metinproximityfront.interfaces.LocObserver



class LocObserverManager {
    private val observers: MutableList<LocObserver?> = ArrayList()

    fun registerObserver(observer: LocObserver?) {
        observers.add(observer)
    }

    fun notifyObservers(mapBitmap: String) {
        observers.forEach {
            Log.e("Observer", "Logs")
            it?.updateLocation(mapBitmap)
        }
    }
}
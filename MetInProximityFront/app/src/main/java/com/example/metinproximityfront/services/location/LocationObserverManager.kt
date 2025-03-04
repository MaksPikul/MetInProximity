package com.example.metinproximityfront.services.location

import android.graphics.Bitmap
import com.example.metinproximityfront.interfaces.LocObserver



class LocObserverManager {
    private val observers: MutableList<LocObserver> = ArrayList()

    fun registerObserver(observer: LocObserver) {
        observers.add(observer)
    }

    fun unregisterObserver(observer: LocObserver) {
        observers.remove(observer)
    }

    fun notifyObservers(mapBitmap: Bitmap) {
        observers.forEach {
            it.updateLocation(mapBitmap)
        }
    }
}
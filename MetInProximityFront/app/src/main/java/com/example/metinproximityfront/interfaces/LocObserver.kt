package com.example.metinproximityfront.interfaces

import android.graphics.Bitmap

interface LocObserver {
    fun updateLocation(mapBitMap: Bitmap)
}
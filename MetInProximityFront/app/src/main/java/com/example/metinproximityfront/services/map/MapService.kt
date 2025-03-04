package com.example.metinproximityfront.services.map

import android.graphics.Bitmap
import com.example.metinproximityfront.services.location.LocationServiceBinder
import com.example.metinproximityfront.interfaces.LocObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapService : LocObserver
{
    private var _map = MutableStateFlow<Bitmap?>(null) // StateFlow for UI
    val map: StateFlow<Bitmap?> = _map // Expose immutable StateFlow



    override fun updateLocation(mapBitMap: Bitmap){
        _map.value = mapBitMap
    }
}
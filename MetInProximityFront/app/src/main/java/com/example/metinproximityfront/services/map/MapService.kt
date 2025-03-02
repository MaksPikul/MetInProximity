package com.example.metinproximityfront.services.map

import android.graphics.Bitmap
import android.util.Log
import com.android.volley.VolleyError
import com.example.metinproximityfront.binders.LocationBinder
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.repositories.MapRepository
import com.example.metinproximityfront.interfaces.LocObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapService (
    private val locBinder : LocationBinder
) : LocObserver
{
    private var _map = MutableStateFlow<Bitmap?>(null) // StateFlow for UI
    val map: StateFlow<Bitmap?> = _map // Expose immutable StateFlow

    override fun updateLocation(mapBitMap: Bitmap){
        _map.value = mapBitMap
    }
}
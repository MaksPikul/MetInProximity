package com.example.metinproximityfront.services.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.repositories.MapRepository
import com.example.metinproximityfront.services.location.LocationServiceBinder
import com.example.metinproximityfront.interfaces.LocObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapService(
    private val mapRepo: MapRepository,
    private val msgLocBinder : LocationServiceBinder,
): LocObserver
{
    private var _map = MutableStateFlow<Bitmap?>(null) // StateFlow for UI
    val map: StateFlow<Bitmap?> = _map // Expose immutable StateFlow

    fun getMap(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val locObj: LocationObject = msgLocBinder.getCurrentLocation()

                val result: String? = mapRepo.getMapRepo(
                    locObj.lon,
                    locObj.lat
                )

                result?.let { mapImage ->
                    updateLocation(mapImage)
                }
            }
            catch (ex : ApiTokenWrapper.AuthException){
                Log.e("Auth", ex.message.toString())
            }
            catch (ex : Throwable) {
                // Show UI error
                Log.e("MapService", ex.message.toString())
            }
        }
    }

    override fun updateLocation(mapStringBase64: String){
        val mapImageBase64: String = mapStringBase64
        val decodedString: ByteArray = Base64.decode(mapImageBase64, Base64.DEFAULT)
        val mapBitmap : Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

        _map.value = mapBitmap
    }
}
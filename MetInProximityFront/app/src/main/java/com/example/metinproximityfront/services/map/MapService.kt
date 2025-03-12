package com.example.metinproximityfront.services.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.metinproximityfront.data.api.MapApi
import com.example.metinproximityfront.data.entities.error.AuthException
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import com.example.metinproximityfront.services.location.LocationServiceBinder
import com.example.metinproximityfront.interfaces.LocObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapService(
    private val msgLocBinder : LocationServiceBinder,
    private val navController: NavHostController,
    private val apiTokenWrapper: ApiTokenWrapper
): LocObserver
{
    private var _map = MutableStateFlow<Bitmap?>(null) // StateFlow for UI
    val map: StateFlow<Bitmap?> = _map // Expose immutable StateFlow

    private val mapApi: MapApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    fun getMap(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val locObj: LocationObject = msgLocBinder.getCurrentLocation()

                val result: String? = apiTokenWrapper.callApiWithToken { accessToken: String ->
                    mapApi.getMapApi(
                        locObj.lon,
                        locObj.lat,
                        accessToken
                    )
                }

                result?.let { mapImage ->
                    updateLocation(mapImage)
                }
            }
            catch (ex : AuthException){
                Log.e("Auth", ex.message.toString())
                navController.navigate("Login")
            }
            catch (ex : Exception) {
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
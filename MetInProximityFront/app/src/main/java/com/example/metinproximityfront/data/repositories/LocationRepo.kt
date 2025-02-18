package com.example.metinproximityfront.data.repositories

import android.location.Location
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.api.LocationApi
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationRepo (
    private val apiTokenWrapper: ApiTokenWrapper
) {

    private val locationApi: LocationApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    fun UpdateUserLocation(
        loc : Location
    ) {
        // This doesnt Update ui, Just sends data to server so that it can be used for requests
        CoroutineScope(Dispatchers.IO).launch {
            apiTokenWrapper.callApiWithToken { token: String ->
                locationApi.PutUserLocation(loc.longitude, loc.latitude, token)
            }
        }
    }
}



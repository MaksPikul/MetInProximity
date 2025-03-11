package com.example.metinproximityfront.data.repositories

import android.util.Log
import com.example.metinproximityfront.data.api.MapApi
import com.example.metinproximityfront.data.api.MessageApi
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit

class MapRepository(
    private val apiTokenWrapper: ApiTokenWrapper
) {
    private val mapApi: MapApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    suspend fun getMapRepo(
        lon : Double,
        lat : Double
    ) : String? {
        return apiTokenWrapper.callApiWithToken { accessToken: String ->
            mapApi.getMapApi(
                lon,
                lat,
                accessToken
            )
        }
    }

}
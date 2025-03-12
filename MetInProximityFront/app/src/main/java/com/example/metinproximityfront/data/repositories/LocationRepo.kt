package com.example.metinproximityfront.data.repositories

import android.location.Location
import android.util.Log
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.api.LocationApi
import com.example.metinproximityfront.data.entities.error.AuthException
import com.example.metinproximityfront.data.entities.location.LocResObj
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit

class LocationRepo (
    private val apiTokenWrapper: ApiTokenWrapper
) {

    private val locationApi: LocationApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    suspend fun UpdateUserLocationRepo(
        loc : Location
    ) : LocResObj? {
        // This doesnt Update ui, Just sends data to server so that it can be used for requests
        return try {
            apiTokenWrapper.callApiWithToken { token: String ->
                locationApi.PutUserLocation(
                    LocationObject(loc.longitude, loc.latitude),
                    token
                )
            }
        }catch (ex : AuthException){
            Log.e("Auth", ex.message.toString())
            null
        }
        catch (ex : Throwable) {
            Log.e("Location", ex.message.toString())
            null
        }
    }
}



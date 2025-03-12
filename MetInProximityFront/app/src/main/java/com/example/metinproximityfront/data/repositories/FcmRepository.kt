package com.example.metinproximityfront.data.repositories

import android.util.Log
import com.example.metinproximityfront.data.api.FcmApi
import com.example.metinproximityfront.data.entities.error.AuthException
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit

class FcmRepository(
    private val apiTokenWrapper: ApiTokenWrapper
) {

    private val fcmApi: FcmApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    suspend fun UpdateUserFcm(fcmToken : String) {
        try {
            apiTokenWrapper.callApiWithToken { accessToken: String ->
                fcmApi.UpdateUserFcm(
                    fcmToken,
                    accessToken
                )
            }
        }catch (ex : AuthException){
            Log.e("Auth", ex.message.toString())
        }
        catch (ex : Throwable) {
            Log.e("Location", ex.message.toString())
        }
    }

}
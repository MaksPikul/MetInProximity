package com.example.metinproximityfront.data.repositories

import com.example.metinproximityfront.data.api.AccountApi
import com.example.metinproximityfront.data.api.ApiTokenWrapper
import com.example.metinproximityfront.data.api.UserActionApi
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserActionRepository(
    private val apiTokenWrapper: ApiTokenWrapper<String>
) {

    private val userActionApi: UserActionApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    suspend fun GetUsersOpenForPrivate() {

    }

    suspend fun UpdateOpenForPrivate() {

    }

    fun UpdateUserFcm(
        fcmToken : String
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                apiTokenWrapper.callApiWithToken { accessToken: String ->
                    userActionApi.UpdateUserFcm(fcmToken, accessToken);
                }
            }
        }
        catch (ex : Throwable) {
            // TODO : DO sumthing with error, Try again?
        }
    }

}
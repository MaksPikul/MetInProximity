package com.example.metinproximityfront.data.repositories

import android.util.Log
import androidx.navigation.NavController
import com.example.metinproximityfront.data.api.UserActionApi
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit

class UserActionRepo (
    private val apiTokenWrapper: ApiTokenWrapper,
    private val navController: NavController
) {

    private val userActionApi: UserActionApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    suspend fun changeVisibilityRepo() : String? {
        return try {
            apiTokenWrapper.callApiWithToken { token: String ->
                userActionApi.ChangeVisibilityApi(token)// Extract response body
            }
        } catch (e : ApiTokenWrapper.AuthException){
            Log.e("Auth Error", e.message.toString())
            navController.navigate("Login")
            null
        }
        catch (e: Exception) {
            Log.e("Api Call Error", e.message.toString())
            null
        }
    }

    suspend fun getPrivateUsersRepo(
        locObj : LocationObject
    ) : List<ChatUser>? {
        return try {
            apiTokenWrapper.callApiWithToken { token: String ->
                userActionApi.GetPrivateUserApi(locObj, token)// Extract response body
            }
        } catch (e : ApiTokenWrapper.AuthException){
            Log.e("Auth Error", e.message.toString())
            navController.navigate("Login")
            null
        }
        catch (e: Exception) {
            Log.e("Api Call Error", e.message.toString())
            null
        }
    }
}

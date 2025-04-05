package com.example.metinproximityfront.data.repositories

import android.util.Log
import androidx.navigation.NavHostController
import com.example.metinproximityfront.data.api.UserApi
import com.example.metinproximityfront.data.entities.account.StringRes
import com.example.metinproximityfront.data.entities.error.AuthException
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit

class UserRepo (
    private val apiTokenWrapper: ApiTokenWrapper,
    private val mainNavController: NavHostController
) {

    private val userActionApi: UserApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    suspend fun changeVisibilityRepo(
    ) : StringRes? {
        return try {
            apiTokenWrapper.callApiWithToken { token: String ->
                userActionApi.ChangeVisibilityApi(
                    token
                )
            }

        } catch (e : AuthException){
            Log.e("Auth Error", e.message.toString())
            mainNavController.navigate("Login")
            null
        }
        catch (e: Exception) {
            Log.e("Api Call Error", e.message.toString())
            null
        }
    }

    suspend fun getPrivateUsersRepo(
        locObj : LocationObject,
    ) : List<ChatUser>? {
        return try {
            apiTokenWrapper.callApiWithToken { token: String ->
                userActionApi.GetPrivateUserApi(
                    locObj.lon,
                    locObj.lat,
                    token
                )
            }
        } catch (e : AuthException){
            Log.e("Auth Error", e.message.toString())
            mainNavController.navigate("Login")
            null
        }
        catch (e: Exception) {
            Log.e("Api Call Error", e.message.toString())
            null
        }
    }
}

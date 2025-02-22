package com.example.metinproximityfront.data.remote

import android.util.Log
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.RefreshTokenApi
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import com.example.metinproximityfront.services.preference.IStoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class ApiTokenWrapper(
    private val encryptedStoreService: IStoreService
) {

    private val refreshTokenApi: RefreshTokenApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    class AuthException(message: String) : Exception(message)

    suspend fun <T> callApiWithToken(
        apiCall: suspend (accessToken: String) -> Response<T>
    ): T {
        var accessToken = encryptedStoreService.getFromPref(Constants.ACCESS_TOKEN_KEY)
        Log.i("ApiTokenWrapper", "Access Token : $accessToken")

        if (accessToken.isNullOrBlank()) {
            // Todo : Implement UI changes or sumthing
            throw
            AuthException("Access token is missing. Redirect to login.")
        }

        return withContext(Dispatchers.IO) {
            val response = try {
                apiCall(getAuthHeader(accessToken!!))

            } catch (e: Throwable) {
                encryptedStoreService.removeFromPref(Constants.ACCESS_TOKEN_KEY)
                if (e is HttpException && e.code() != 401) {
                    throw e
                }

                accessToken = try {
                    RefreshAccessToken()
                } catch (refreshError: Throwable) {
                    encryptedStoreService.removeFromPref(Constants.REFRESH_TOKEN_KEY)
                    throw AuthException("Token refresh failed: $refreshError")
                }

                apiCall(getAuthHeader(accessToken!!))
            }

            if (response.isSuccessful) {
                response.body() ?: throw AuthException("API response body is null")
            } else {
                throw HttpException(response)
            }
        }
    }

    private fun getAuthHeader(accessToken: String) : String{
        return "Bearer $accessToken"
    }

    // This method only gets used here,
    // used to be in authService,
    // decoupled this whole class by moving this here
    // Now i should be able to use this in firebaseMsgReceiver, Only need to pass encrypted store
    private suspend fun RefreshAccessToken(): String {
        try {
            val refreshToken = encryptedStoreService.getFromPref(Constants.REFRESH_TOKEN_KEY)

            if (refreshToken != null) {
                val response = refreshTokenApi.RefreshAccessToken(refreshToken)

                if (response.isSuccessful && response.body() != null) {
                    val tokenResponse = response.body()!!
                    return tokenResponse.accessToken
                } else {
                    throw Exception("Failed to refresh access token")
                }
            }
            else{
                throw Exception("Missing Refresh Token")
            }
        } catch (e: Exception) {
            throw Exception("Error refreshing access token: ${e.message}")
        }
    }


}
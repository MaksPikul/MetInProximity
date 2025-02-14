package com.example.metinproximityfront.data.api

import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import com.example.metinproximityfront.services.preference.IStoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class ApiTokenWrapper<T> (
    private val encryptedStoreService: IStoreService
) {

    private val refreshTokenApi: RefreshTokenApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    suspend fun <T> callApiWithToken(
        apiCall: suspend (accessToken: String) -> Response<T>
    ): T {
        var accessToken = encryptedStoreService.getFromPref(Constants.ACCESS_TOKEN_KEY)

        if (accessToken.isNullOrBlank()) {
            // Todo : Implement UI changes or sumthing
            throw Exception("Access token is missing. Redirect to login.")
        }

        return withContext(Dispatchers.IO) {
            val response = try {
                apiCall(accessToken!!)
            } catch (e: Throwable) {
                if (e is HttpException && e.code() != 401) {
                    throw e
                }

                accessToken = try {
                    RefreshAccessToken()
                } catch (refreshError: Throwable) {
                    throw Exception("Token refresh failed so redirect to login.", refreshError)
                }

                apiCall(accessToken!!)
            }

            if (response.isSuccessful) {
                response.body() ?: throw Exception("API response body is null")
            } else {
                throw HttpException(response)
            }
        }
    }

    // This method only gets used here,
    // used to be in authService,
    // decoupled this whole class by moving this here
    // Now i should be able to use this in firebaseMsgReceiver, Only need to pass encrypted store
    suspend fun RefreshAccessToken(): String {
        try {
            val refreshToken = encryptedStoreService.getFromPref(Constants.REFRESH_TOKEN_KEY)

            if (refreshToken != null) {
                val response = refreshTokenApi.RefreshAccessToken(refreshToken)

                if (response.isSuccessful && response.body() != null) {
                    val tokenResponse = response.body()!!
                    return tokenResponse.access_token
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
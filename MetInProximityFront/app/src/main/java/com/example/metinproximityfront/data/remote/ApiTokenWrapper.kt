package com.example.metinproximityfront.data.remote

import android.util.Log
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.RefreshTokenApi
import com.example.metinproximityfront.data.entities.account.AuthResponse
import com.example.metinproximityfront.data.entities.error.AuthException
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import com.example.metinproximityfront.services.preference.IStoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit

class ApiTokenWrapper(
    private val encryptedStoreService: IStoreService,
    private val refreshTokenApi: RefreshTokenApi // needs to be here for testing
) {

    suspend fun <T> callApiWithToken(
        apiCall: suspend (accessToken: String) -> Response<T>
    ): T {
        var accessToken = encryptedStoreService.getFromPref(Constants.ACCESS_TOKEN_KEY)

        if (accessToken.isNullOrBlank()) {
            throw AuthException("Access token is missing. Redirect to login.")
        }

        return withContext(Dispatchers.IO) {
            val response = try {
                apiCall(getAuthHeader(accessToken!!))

            } catch (e: Throwable) {
                if (e is HttpException && e.code() != 401) {
                    throw e
                }

                accessToken = try {
                    refreshAccessToken()
                }
                catch (e: Exception) {
                    throw AuthException("Error : ${e.message}")
                }

                apiCall(getAuthHeader(accessToken!!))
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
    private suspend fun refreshAccessToken(): String {

        val refreshToken = encryptedStoreService.getFromPref(Constants.REFRESH_TOKEN_KEY)

        if (refreshToken == null) {
            throw AuthException("Missing Refresh Token")
        }
        val response : Response<AuthResponse> = refreshTokenApi.RefreshAccessToken(refreshToken)
        return handleRefreshResponse(response)
    }

    // any failure to refresh will cause log out (AuthError)
    private fun handleRefreshResponse(response: Response<AuthResponse>) : String{

        if (response.isSuccessful && response.body() != null) {

            val tokenResponse = response.body()!!

            encryptedStoreService.saveIntoPref(Constants.ACCESS_TOKEN_KEY, tokenResponse.accessToken)
            return tokenResponse.accessToken
        } else {

            encryptedStoreService.removeFromPref(Constants.ACCESS_TOKEN_KEY)
            encryptedStoreService.removeFromPref(Constants.REFRESH_TOKEN_KEY)
            throw AuthException("Failed to refresh access token")
        }
    }

    private fun getAuthHeader(accessToken: String) : String{
        return "Bearer $accessToken"
    }
}
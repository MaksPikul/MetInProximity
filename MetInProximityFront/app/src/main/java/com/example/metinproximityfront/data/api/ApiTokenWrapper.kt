package com.example.metinproximityfront.data.api

import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class ApiTokenWrapper<T> (
    private val authService: AuthService,
    private val encryptedStoreService: EncryptedStoreService
) {

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
                    authService?.RefreshAndReturnToken()
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

}
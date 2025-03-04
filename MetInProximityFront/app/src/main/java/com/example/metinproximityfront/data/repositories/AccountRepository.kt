package com.example.metinproximityfront.data.repositories

import com.example.metinproximityfront.data.api.AccountApi
import com.example.metinproximityfront.data.entities.account.AuthRequest
import com.example.metinproximityfront.data.entities.account.AuthResponse
import com.example.metinproximityfront.data.entities.account.AuthResult
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import retrofit2.Response

class AccountRepository {

    // TODO : THIS NEEDS TO BE PUT SOMEWHERE, THERE ARE CURRENTLY INITIALISATIONE ERRORS

    // AccountApi instance, lazily initialized
    private val accountApi: AccountApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    suspend fun Authenticate(
        provider: String,
        authRequest: AuthRequest
    ): AuthResult {
        return try {
            val response : Response<AuthResponse> = accountApi.Authenticate(provider, authRequest)

            if (response.isSuccessful) {
                val tokens = response.body()

                AuthResult.success(
                    tokens?.accessToken ?: "",
                    tokens?.refreshToken ?: ""
                )

            } else {
                AuthResult.error(
                    "code: ${response.code()}",
                    "Message: ${response.errorBody()?.string()}"
                )
            }
        } catch (e: Exception) {
            AuthResult.error("400", "Network error: ${e.message}")
        }
    }
}
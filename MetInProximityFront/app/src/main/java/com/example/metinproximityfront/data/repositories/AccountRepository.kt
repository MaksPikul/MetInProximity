package com.example.metinproximityfront.data.repositories

import com.example.metinproximityfront.data.api.AccountApi
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
        code : String
    ): AuthResult {
        return try {
            val response : Response<AuthResponse> = accountApi.Authenticate(provider, code)

            if (response.isSuccessful) {
                val tokens = response.body()

                AuthResult.success(
                    tokens?.access_token ?: "",
                    tokens?.refresh_token ?: ""
                )

            } else {
                AuthResult.error("Authentication failed: ${response.code()}")
            }
        } catch (e: Exception) {
            AuthResult.error("Network error: ${e.message}")
        }
    }

    suspend fun Logout() {
        try {
            accountApi.Logout()
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun RefreshAccessToken(refreshToken: String): String {
        try {

            val response = accountApi.RefreshAccessToken(refreshToken)

            if (response.isSuccessful && response.body() != null) {
                val tokenResponse = response.body()!!
                return tokenResponse.access_token
            } else {
                throw Exception("Failed to refresh access token")
            }
        } catch (e: Exception) {
            throw Exception("Error refreshing access token: ${e.message}")
        }
    }


    /*
    fun Ping(){

    }
     */

}
package com.example.metinproximityfront.data.repositories

import com.example.metinproximityfront.data.api.AccountApi
import com.example.metinproximityfront.data.entities.account.AuthResponse
import com.example.metinproximityfront.data.entities.account.AuthResult
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.HttpClient.retrofit
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AccountRepository {

    // TODO : THIS NEEDS TO BE PUT SOMEWHERE, THERE ARE CURRENTLY INITIALISATIONE ERRORS

    val BASE_URL = "https://10.0.2.2:7238/api"


    // AccountApi instance, lazily initialized
    private val accountApi: AccountApi by lazy {
        ApiServiceFactory(retrofit)
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

    /*
    suspend fun RefreshAccessToken(
        refreshToken : String
    ) {
        val response : Response<AuthResponse> = accountApi.RefreshAccessToken(refreshToken)
    }



    fun Ping(){

    }
     */
    suspend fun Logout() {
        try {
            accountApi.Logout()
        } catch (e: Exception) {
            throw e
        }
    }


    // TODO: private fun Co-Routine Wrapper?
}
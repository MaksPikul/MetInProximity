package com.example.metinproximityfront.api

import com.example.metinproximityfront.api.entities.AuthResponse
import retrofit2.Response

class AccountRepo (
    private val apiClient: ApiInterface
){

    suspend fun Authenticate(
        provider: String,
        code : String
    ): Response<AuthResponse> {
        return try {
            val response = apiClient.Authenticate(provider, code)

            if (response.isSuccessful) {
                // Process the successful response
                Resource.Success(response.body())
            } else {
                // Handle API errors (e.g., 404, 500, etc.)
                Resource.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            // Handle network errors (e.g., no internet, timeout)
            Resource.Error("Network Error: ${e.message}")
        }
    }
    }

    fun Logout(){

    }

    fun Ping(){

    }


    // TODO: private fun Co-Routine Wrapper?
}
package com.example.metinproximityfront.data.entities.account

data class AuthResult(
    val isSuccessful: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val error: String? = null,
    val message: String? = null
) {
    companion object {
        fun success(accessToken: String, refreshToken: String) = AuthResult(
            isSuccessful = true,
            accessToken = accessToken,
            refreshToken = refreshToken
        )

        fun error(error: String, message: String) = AuthResult(
            isSuccessful = false,
            error = error,
            message = message
        )
    }
}

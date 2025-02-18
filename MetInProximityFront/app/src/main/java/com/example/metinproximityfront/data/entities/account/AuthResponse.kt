package com.example.metinproximityfront.data.entities.account

data class AuthResponse(
    val accessToken : String,
    val refreshToken : String?
)

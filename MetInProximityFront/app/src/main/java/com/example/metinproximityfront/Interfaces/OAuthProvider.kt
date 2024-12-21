package com.example.metinproximityfront.Interfaces

interface OAuthProvider {
    val name: String
    val oauthUrl: String
    val redirectUri : String
    val clientId : String
    fun getOAuthUrl(): String
}


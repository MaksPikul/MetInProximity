package com.example.metinproximityfront.config.OAuth

interface OAuthConfig{
    val name: String
    val clientId: String
    val redirectUri: String
    val tokenUri : String
    val oauthUrl : String
}
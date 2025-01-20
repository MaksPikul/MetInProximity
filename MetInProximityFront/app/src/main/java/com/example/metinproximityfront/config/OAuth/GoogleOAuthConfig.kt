package com.example.metinproximityfront.config.OAuth

class GoogleOAuthConfig: OAuthConfig{

    override val name = "google"
    override val clientId = "150643742648-401ps6vje504ln6pru630jsrfhg3tt90.apps.googleusercontent.com"
    override val redirectUri = "com.example.metinproximityfront:/oauth2callback"
    override val tokenUri = "https://oauth2.googleapis.com/token"
    override val oauthUrl = "https://accounts.google.com/o/oauth2/v2/auth"

}
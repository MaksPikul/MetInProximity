package com.example.metinproximityfront.config.oauth

import android.net.Uri

class GoogleOAuthConfig: OAuthConfig{

    override val name = "google"
    override val clientId = "150643742648-401ps6vje504ln6pru630jsrfhg3tt90.apps.googleusercontent.com"
    override val redirectUri = Uri.parse("com.example.metinproximityfront:/oauth2callback")
    override val tokenUri = Uri.parse("https://oauth2.googleapis.com/token")
    override val oauthUrl = Uri.parse("https://accounts.google.com/o/oauth2/v2/auth")

}
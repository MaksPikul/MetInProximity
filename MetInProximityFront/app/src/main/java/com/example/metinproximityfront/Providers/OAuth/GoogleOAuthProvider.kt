package com.example.metinproximityfront.Providers.OAuth

import com.example.metinproximityfront.Interfaces.OAuthProvider

class GoogleOAuthProvider : OAuthProvider {
    override val name = "Google"
    override val oauthUrl = "https://accounts.google.com/o/oauth2/v2/auth"
    override val redirectUri = "https://localhost:7238/api/account/oauth/google"
    override val clientId = ""

    override fun getOAuthUrl(): String {
        // You can customize the URL with parameters like client ID, scopes, etc.
        return oauthUrl +
                "?client_id=$clientId" +
                "&redirect_uri=$redirectUri" +
                "&response_type=code" +
                "&response_mode=query" +
                "&scope=openid%20profile%20email" +
                "&access_type=offline" +
                "&prompt=consent"
    }
}
package com.example.metinproximityfront.Providers.OAuth

import com.example.metinproximityfront.Interfaces.OAuthProvider

class MicrosoftOAuthProvider : OAuthProvider {
    override val name = "Microsoft"
    override val oauthUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize"
    override val redirectUri = "https://localhost:7238/api/account/oauth/microsoft"
    override val clientId = ""

    override fun getOAuthUrl(): String {
        // You can customize the URL with parameters like client ID, scopes, etc.
        return oauthUrl +
                "?client_id=$clientId" +
                "&redirect_uri=$redirectUri" +
                "&response_type=code" +
                "&response_mode=query" +
                "&scope=openid%20profile%20email"
    }

}
package com.example.metinproximityfront.config.oauth

import android.net.Uri

class MicrosoftOAuthConfig : OAuthConfig{

    override val name = "microsoft"
    override val clientId = "d166e25b-6612-4fb0-bf6a-d3ba9d6c393c"
    override val redirectUri = Uri.parse("com.example.metinproximityfront:/oauth2callback")
    override val tokenUri = Uri.parse("https://login.microsoftonline.com/common/oauth2/v2.0/token")
    override val oauthUrl = Uri.parse("https://graph.microsoft.com/v1.0/me")

}
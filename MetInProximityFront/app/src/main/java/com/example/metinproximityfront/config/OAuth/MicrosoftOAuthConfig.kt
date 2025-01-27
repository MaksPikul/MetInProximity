package com.example.metinproximityfront.config.OAuth

import android.net.Uri

class MicrosoftOAuthConfig : OAuthConfig{

    override val name = "microsoft"
    override val clientId = ""
    override val redirectUri = Uri.parse("com.example.metinproximityfront:/oauth2callback")
    override val tokenUri = Uri.parse("")
    override val oauthUrl = Uri.parse("")

}
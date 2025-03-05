package com.example.metinproximityfront.config.oauth

import android.net.Uri

class MicrosoftOAuthConfig : OAuthConfig{

    override val name = "microsoft"
    override val clientId = "05563ea0-87fd-473a-b618-fc212d0c3b89"
    override val redirectUri = Uri.parse("msauth://com.example.metinproximityfront/1YFTn7Qa8LnCCXvPGHvKCg3YvXI%3D")
    override val tokenUri = Uri.parse("https://login.microsoftonline.com/common/oauth2/v2.0/token")
    override val oauthUrl = Uri.parse("https://login.microsoftonline.com/common/oauth2/v2.0/authorize")

}
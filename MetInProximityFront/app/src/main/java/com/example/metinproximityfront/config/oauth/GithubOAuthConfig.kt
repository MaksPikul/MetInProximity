package com.example.metinproximityfront.config.oauth

import android.net.Uri

class GithubOAuthConfig() : OAuthConfig {
    override val name : String = "github"
    override val clientId: String = "Ov23liOLIPozohssiHeg"
    override val redirectUri: Uri = Uri.parse("com.example.metinproximityfront://github")
    override val tokenUri: Uri = Uri.parse("https://github.com/login/oauth/access_token")
    override val oauthUrl: Uri = Uri.parse("https://github.com/login/oauth/authorize")

}
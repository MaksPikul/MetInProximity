package com.example.metinproximityfront.config.oauth

import android.net.Uri

interface OAuthConfig{
    val name: String
    val clientId: String
    val redirectUri: Uri
    val tokenUri : Uri
    val oauthUrl : Uri
}
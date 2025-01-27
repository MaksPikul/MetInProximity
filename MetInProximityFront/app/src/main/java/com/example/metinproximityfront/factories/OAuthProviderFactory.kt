package com.example.metinproximityfront.factories

import com.example.metinproximityfront.config.OAuth.GoogleOAuthConfig
import com.example.metinproximityfront.config.OAuth.MicrosoftOAuthConfig
import com.example.metinproximityfront.config.OAuth.OAuthConfig

class OAuthProviderFactory {

    private val providers: List<OAuthConfig>

    init {
        providers = listOf(
            GoogleOAuthConfig(),
            MicrosoftOAuthConfig()
        )
    }

    fun getProviders(): List<OAuthConfig> {
        return providers
    }

}
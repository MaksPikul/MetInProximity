package com.example.metinproximityfront.factories

import com.example.metinproximityfront.config.oauth.GithubOAuthConfig
import com.example.metinproximityfront.config.oauth.GoogleOAuthConfig
import com.example.metinproximityfront.config.oauth.MicrosoftOAuthConfig
import com.example.metinproximityfront.config.oauth.OAuthConfig

object OAuthProviderFactory {

    private val providers: List<OAuthConfig>

    init {
        providers = listOf(
            GoogleOAuthConfig(),
            //GithubOAuthConfig(),
            MicrosoftOAuthConfig()
        )
    }

    fun getProviders(): List<OAuthConfig> {
        return providers
    }

}
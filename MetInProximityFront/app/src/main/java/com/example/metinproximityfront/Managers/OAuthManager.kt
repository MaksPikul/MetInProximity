package com.example.metinproximityfront.Managers

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.example.metinproximityfront.Interfaces.OAuthProvider
import com.example.metinproximityfront.Providers.OAuth.GoogleOAuthProvider
import com.example.metinproximityfront.Providers.OAuth.MicrosoftOAuthProvider

class OAuthManager/*(private val context: Context)*/ {
    private val providers: List<OAuthProvider> = listOf(
        GoogleOAuthProvider(),
        MicrosoftOAuthProvider()
    )

    // Start the OAuth flow for the selected provider
    fun startOAuthFlow(provider: OAuthProvider){
        val oAuthUrl = provider.getOAuthUrl()

        val uri = Uri.parse(oAuthUrl)
        val customTabsIntent = CustomTabsIntent.Builder().build()

        //customTabsIntent.launchUrl(context, uri)
    }

    fun getProviders(): List<OAuthProvider> = providers
}


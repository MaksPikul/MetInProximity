package com.example.metinproximityfront.services.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.metinproximityfront.config.OAuth.OAuthConfig
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher

class AuthService(
    private val appContext: Context
) : IAuthService
{
    private var loginAuthService : AuthorizationService? = null

    override fun StartLogin(
        provider: OAuthConfig,
        launchAction: (i: Intent) -> Unit
    ){
        val authService = initLoginAuthService()
        this.loginAuthService = authService

        val oauthUrl = Uri.parse(provider.oauthUrl)
        val redirectUri = Uri.parse(provider.redirectUri)
        val tokenUri = Uri.parse(provider.tokenUri)
        val clientId = provider.clientId

        val config = AuthorizationServiceConfiguration(oauthUrl, tokenUri)

        val request = AuthorizationRequest
            .Builder(config, clientId, ResponseTypeValues.CODE, redirectUri)
            .setScopes("profile email")
            .build()

        val authIntent = authService.getAuthorizationRequestIntent(request)

        launchAction(authIntent)
    }

    override fun FinishLogin(responseIntent: Intent){
        val authorizationResponse : AuthorizationResponse? = AuthorizationResponse.fromIntent(responseIntent)
        val error = AuthorizationException.fromIntent(responseIntent)

        this.loginAuthService?.dispose()
        this.loginAuthService = null
    }


    private fun initLoginAuthService(): AuthorizationService {
        val appAuthConfiguration = AppAuthConfiguration.Builder()
            .setBrowserMatcher(
                BrowserAllowList(
                    VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                    VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
                )
            ).build()

        return AuthorizationService(
            appContext,
            appAuthConfiguration)
    }



}
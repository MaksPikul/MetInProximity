package com.example.metinproximityfront.services.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.example.metinproximityfront.api.AccountRepo
import com.example.metinproximityfront.api.entities.AuthResponse
import com.example.metinproximityfront.config.OAuth.OAuthConfig
import com.example.metinproximityfront.services.preference.IPrefStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import java.security.MessageDigest
import java.security.SecureRandom

class AuthService(
    private val appContext: Context,
    private val prefStore : IPrefStoreService,
    private val accountRepo: AccountRepo
) : IAuthService
{
    private var loginAuthService : AuthorizationService? = null

    override fun StartLogin(
        provider: OAuthConfig,
        launchAction: (i: Intent) -> Unit
    ){
        val authService = initLoginAuthService()
        this.loginAuthService = authService

        val config = AuthorizationServiceConfiguration(provider.oauthUrl,provider.tokenUri)

        val codePair = createVerifierAndChallenge()

        val request = AuthorizationRequest
            .Builder(config, provider.clientId, ResponseTypeValues.CODE, provider.redirectUri)
            .setScopes("profile email")
            .setCodeVerifier(
                codePair.first,
                codePair.second,
                "S256")
            .build()

        val authIntent = authService.getAuthorizationRequestIntent(request)
        authIntent.putExtra("provider", provider.name)

        launchAction(authIntent)
    }

    override fun FinishLogin(
        responseIntent: Intent,
        successRedirect : ()-> Unit
    ){
        val authResponse : AuthorizationResponse? = AuthorizationResponse.fromIntent(responseIntent)
        val error = AuthorizationException.fromIntent(responseIntent)

        if (error != null){
            // TODO : THROW OR HANDLE ERRO
        }
        if (authResponse == null){
            return
        }
        // TODO : Validate code verifier

        val provider : String = responseIntent.getStringExtra("provider").toString()

        this.loginAuthService?.dispose()
        this.loginAuthService = null

        CoroutineScope(Dispatchers.IO).launch {
            val loginResult  = accountRepo.Authenticate(provider, authResponse.authorizationCode)

            withContext(Dispatchers.Main) {
                if (loginResult.isSuccessful && loginResult.body() != null) {

                    storeTokens(
                        loginResult.body()!!.access_token,
                        loginResult.body()!!.refresh_token
                    )
                    successRedirect()
                } else {
                    // Handle error (e.g., show a toast or update UI state)
                }
            }
        }
    }

    override fun Logout() {
        (TODO("NOT IMPLEMENTED YET"))
    }

    override fun IsLoggedIn (): Boolean {
        return this.prefStore.getFromPref("auth_token") != ""
    }

    private fun createVerifierAndChallenge (): Pair<String, String> {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(64)
        secureRandom.nextBytes(bytes)

        val encoding = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
        val codeVerifier = Base64.encodeToString(bytes, encoding)

        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(codeVerifier.toByteArray())
        val codeChallenge = Base64.encodeToString(hash, encoding)

        return Pair(codeVerifier, codeChallenge)
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

    private fun storeTokens (
        accessToken : String,
        refreshToken : String
    ){
        this.prefStore.saveIntoPref("Access_Token", accessToken)
        this.prefStore.saveIntoPref("Refresh_Token", refreshToken)
    }



}
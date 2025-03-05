package com.example.metinproximityfront.services.auth

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.config.oauth.OAuthConfig
import com.example.metinproximityfront.data.entities.account.AuthRequest
import com.example.metinproximityfront.data.entities.account.AuthResult
import com.example.metinproximityfront.data.entities.account.User
import com.example.metinproximityfront.data.repositories.AccountRepository
import com.example.metinproximityfront.services.preference.IStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationService.TokenResponseCallback
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import java.security.MessageDigest
import java.security.SecureRandom


/*
    I used this library for OAuth
    https://github.com/openid/AppAuth-Android
 */
class AuthService(
    private val appContext: Context,
    private val prefStore : IStoreService,
    private val accountRepo: AccountRepository
) : IAuthService
{

    override var curProvider: String? = null

    private var loginAuthService : AuthorizationService? = null

    override fun StartLogin(
        provider: OAuthConfig,
        launchAction: (i: Intent) -> Unit
    ) {
        val authService = initLoginAuthService()
        this.loginAuthService = authService

        val config = AuthorizationServiceConfiguration(provider.oauthUrl, provider.tokenUri)
        val codePair = createVerifierAndChallenge()

        val request = createAuthorizationRequest(config, provider, codePair)

        Log.d("verifier", codePair.first)

        val authIntent = authService.getAuthorizationRequestIntent(request)
        this.curProvider = provider.name

        launchAction(authIntent)
    }

    override fun FinishLogin(
        responseIntent: Intent,
        fcmToken: String,
        onSuccessfulLogin: () -> Unit,
        onFailedLogin: (errorMsg: String?, errorCode: String?) -> Unit
    ) {
        val authResponse: AuthorizationResponse? = AuthorizationResponse.fromIntent(responseIntent)
        val error = AuthorizationException.fromIntent(responseIntent)

        when {
            error != null -> onFailedLogin(error.errorDescription, error.code.toString())
            authResponse == null -> onFailedLogin("Authentication Result is Missing", "300")
            else -> startTokenRequest(
                        authResponse,
                        fcmToken,
                        onSuccessfulLogin,
                        onFailedLogin
                    )
        }

        loginAuthService?.dispose()
        loginAuthService = null
    }

    override fun Logout(
        successRedirect: () -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                //accountRepo.Logout()
                removeTokens()
                withContext(Dispatchers.Main) {
                    User.delete()
                    successRedirect()
                }
            } catch (e: Exception) {
                // Handle logout error
            }
        }
    }

    override fun IsLoggedIn() : Boolean {
        val accessTokenKey = Constants.ACCESS_TOKEN_KEY

        val isEmpty = this.prefStore.getFromPref(accessTokenKey) != ""
        //val isExpired = this
        return isEmpty
    }

    /*
        Start Login Helper Functions
    */

    private fun createAuthorizationRequest(config: AuthorizationServiceConfiguration, provider: OAuthConfig, codePair: Pair<String, String>): AuthorizationRequest {

        val scopes = when (provider.name) {
            "google" -> "profile email"
            "microsoft" -> "openId profile email"
            "github" -> "user"
            else -> ""
        }

        return AuthorizationRequest.Builder(config, provider.clientId, ResponseTypeValues.CODE, provider.redirectUri)
            .setScopes(scopes)
            .setCodeVerifier(codePair.first, codePair.second, "S256")
            .build()
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

    /*
        Finish Login Helper Functions
    */
    private fun startTokenRequest(
        authResponse: AuthorizationResponse,
        fcmToken : String,
        onSuccessfulLogin: () -> Unit,
        onFailedLogin: (errorMsg: String?, errorCode: String?) -> Unit
    ) {
        authResponse.createTokenExchangeRequest()?.let {
            loginAuthService?.performTokenRequest(it, object : TokenResponseCallback {
                override fun onTokenRequestCompleted(resp: TokenResponse?, ex: AuthorizationException?) {
                    if (resp != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            authenticateWithWebServer(
                                resp,
                                fcmToken,
                                onSuccessfulLogin,
                                onFailedLogin
                            )
                        }
                    } else {
                        onFailedLogin(ex?.localizedMessage ?: "Unknown error", ex?.code.toString())
                    }
                }
            })
        }
    }



    private suspend fun authenticateWithWebServer(
        resp: TokenResponse,
        fcmToken : String,
        onSuccessfulLogin: () -> Unit,
        onFailedLogin: (errorMsg: String?, errorCode: String?) -> Unit
    ) {
        val authResult: AuthResult = accountRepo.Authenticate(
            provider = curProvider.toString(),
            authRequest = AuthRequest(
                resp.idToken.toString(),
                fcmToken
            )
        )

        withContext(Dispatchers.Main) {
            handleAuthResult(
                authResult,
                onSuccessfulLogin,
                onFailedLogin
            )
        }
    }

    private fun handleAuthResult(
        authResult: AuthResult,
        onSuccessfulLogin: () -> Unit,
        onFailedLogin: (errorMsg: String?, errorCode: String?) -> Unit
    ) {

        Log.i("Access", authResult.accessToken.toString())
        Log.i("Refresh", authResult.refreshToken.toString())

        if (authResult.isSuccessful) {
            storeTokens(authResult)
            onSuccessfulLogin()
        } else {
            onFailedLogin(authResult.error, "400")
        }
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
            appAuthConfiguration
        )
    }

    private fun storeTokens (
        authResult: AuthResult
    ){
        this.prefStore.saveIntoPref(Constants.ACCESS_TOKEN_KEY, authResult.accessToken.toString())
        this.prefStore.saveIntoPref(Constants.REFRESH_TOKEN_KEY, authResult.refreshToken.toString())
    }

    private fun removeTokens() {
        this.prefStore.removeFromPref(Constants.ACCESS_TOKEN_KEY)
        this.prefStore.removeFromPref(Constants.REFRESH_TOKEN_KEY)
    }

}
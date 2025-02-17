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
import net.openid.appauth.NoClientAuthentication
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


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
    ){
        // Creating client which handles OAuth (AppAuth Dependency)
        val authService = initLoginAuthService()
        this.loginAuthService = authService

        // Using provider class to configure service with Authentication server URL and
        // Resource Server URL which holds User ID tokens
        // tokenUri configured here, but not used, called in server backend
        val config = AuthorizationServiceConfiguration(provider.oauthUrl,provider.tokenUri)

        // Create a pair class which holds Verifier and Challenge for extra security
        // Following PKCE OAuth Flow) - maybe not -
        val codePair = createVerifierAndChallenge()

        // Request intent is built
        val request = AuthorizationRequest
            .Builder(config, provider.clientId, ResponseTypeValues.CODE, provider.redirectUri)
            .setScopes("profile email")
            .setCodeVerifier(
                codePair.first,
                codePair.second,
                "S256")
            .build()
        Log.d("verifier", codePair.first)

        // Provider added to intent
        // helps handle the correct third party auth provider when finishing login
        val authIntent = authService.getAuthorizationRequestIntent(request)
        this.curProvider = provider.name

        // Intent is launched which opens up chrome
        launchAction(authIntent)
    }

    override fun FinishLogin(
        responseIntent: Intent,
        onSuccessfulLogin : ()-> Unit,
        fcmToken : String
    ){
        // After redirect back to Mobile App login screen,
        // Response Object and Code extracted
        val authResponse : AuthorizationResponse? = AuthorizationResponse.fromIntent(responseIntent)
        val error = AuthorizationException.fromIntent(responseIntent)

        // Checking for errors or missing response object
        if (error != null){
            Toast.makeText(appContext, error.errorDescription, Toast.LENGTH_LONG).show()
            error.errorDescription?.let { Log.e("OAuth Error", it) }
        }
        if (authResponse == null){
            Toast.makeText(appContext, "Authentication Result is Missing", Toast.LENGTH_LONG).show()
            Log.e("OAuth Error","Missing Result")
        }
        val provider = curProvider.toString()
        Log.i("provider", provider)

        authResponse?.createTokenExchangeRequest()?.let {
            loginAuthService?.performTokenRequest(
                it,
                object : TokenResponseCallback {
                    override fun onTokenRequestCompleted(resp: TokenResponse?, ex: AuthorizationException?) {
                        if (resp != null) {

                            Log.i("IdToken", resp.idToken.toString())
                            Log.e("provider", provider)


                            CoroutineScope(Dispatchers.IO).launch {

                                val authResult: AuthResult =
                                    accountRepo.Authenticate(
                                        provider,
                                        AuthRequest(
                                            resp.idToken.toString()
                                        )
                                    )

                                withContext(Dispatchers.Main) {
                                    if (authResult.isSuccessful) {

                                        // Saving tokens into Encrypted Shared Preferences
                                        storeTokens(
                                            authResult.accessToken.toString(),
                                            authResult.refreshToken.toString()
                                        )
                                        //storeUser(/* object that comes with login */)
                                        // Function passed from MainActivity which redirects user to home view
                                        onSuccessfulLogin()
                                    } else {
                                        // Shows error on Login View
                                        Toast.makeText(
                                            appContext,
                                            authResult.error,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        authResult.message?.let { Log.e("API Error", it) }
                                    }
                                }
                            }

                        } else {
                            // Authorization failed, check 'ex' for more details
                            Log.e("OAuth Error", ex?.localizedMessage ?: "Unknown error")
                        }
                    }
                }
            )
        }

        this.loginAuthService?.dispose()
        this.loginAuthService = null
        curProvider = null

    }

    override fun Logout(
        successRedirect: () -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                accountRepo.Logout()
                removeTokens()
                withContext(Dispatchers.Main) {
                    successRedirect()
                }
            } catch (e: Exception) {
                // Handle logout error
            }
        }
    }
    /*
        this is fine even if the token is expired, because refresh token would revalidate access
        client will make api request with expired token,
        client revalidates with refresh,
        client still logged in,
        we could check here if the refresh token is expired, to save on resources, a design decision for later :D
    */
    override fun IsLoggedIn() : Boolean {

        return this.prefStore.getFromPref(Constants.ACCESS_TOKEN_KEY) != ""
    }

    /*
    override fun GetUser() : User {
        val jwt = this.prefStore.getFromPref(Constants.ACCESS_TOKEN_KEY)
    }
    */

    /*
        Helper Functions
    */


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

    private fun storeUser(
        accessToken: String
    ){

    }

    private fun storeTokens (
        accessToken : String,
        refreshToken : String
    ){
        this.prefStore.saveIntoPref(Constants.ACCESS_TOKEN_KEY, accessToken)
        this.prefStore.saveIntoPref(Constants.REFRESH_TOKEN_KEY, refreshToken)
    }

    private fun removeTokens() {
        this.prefStore.removeFromPref(Constants.ACCESS_TOKEN_KEY)
        this.prefStore.removeFromPref(Constants.REFRESH_TOKEN_KEY)
    }

}
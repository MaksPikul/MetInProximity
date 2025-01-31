package com.example.metinproximityfront.services.auth

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.widget.Toast
import com.example.metinproximityfront.data.repositories.AccountRepository
import com.example.metinproximityfront.data.entities.account.AuthResult
import com.example.metinproximityfront.config.OAuth.OAuthConfig
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
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import java.security.MessageDigest
import java.security.SecureRandom

class AuthService(
    private val appContext: Context,
    private val prefStore : IStoreService,
    private val accountRepo: AccountRepository
) : IAuthService
{
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
        // Following PKCE OAuth Flow)
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

        // Provider added to intent
        // helps handle the correct third party auth provider when finishing login
        val authIntent = authService.getAuthorizationRequestIntent(request)
        authIntent.putExtra("provider", provider.name)

        // Intent is launched which opens up chrome
        launchAction(authIntent)
    }

    override fun FinishLogin(
        responseIntent: Intent,
        successRedirect : ()-> Unit
    ){
        // After redirect back to Mobile App login screen,
        // Response Object and Code extracted
        val authResponse : AuthorizationResponse? = AuthorizationResponse.fromIntent(responseIntent)
        val error = AuthorizationException.fromIntent(responseIntent)

        // Checking for errors or missing response object
        if (error != null){
            Toast.makeText(appContext, error.errorDescription, Toast.LENGTH_SHORT).show()
        }
        if (authResponse == null){
            Toast.makeText(appContext, "Authentication Result is Missing", Toast.LENGTH_SHORT).show()
        }

        // Getting provider from intent
        val provider : String = responseIntent.getStringExtra("provider").toString()

        // Class created for OAuth disposed because it's not required after login
        this.loginAuthService?.dispose()
        this.loginAuthService = null

        // Wraps the API request in a Co-routine for async actions
        CoroutineScope(Dispatchers.IO).launch {
            // Account repo calls API, and returns an AuthResult Object
            val authResult : AuthResult = accountRepo.Authenticate(provider, authResponse?.authorizationCode.toString())

            withContext(Dispatchers.Main) {
                if (authResult.isSuccessful) {

                    // Saving tokens into Encrypted Shared Preferences
                    storeTokens(
                        authResult.accessToken.toString(),
                        authResult.refreshToken.toString()
                    )
                    // Function passed from MainActivity which redirects user to home view
                    successRedirect()
                } else {
                    // Shows error on Login View
                    Toast.makeText(appContext, authResult.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
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
    override fun IsLoggedIn (): Boolean {

        return this.prefStore.getFromPref("Access_Token") != ""
    }


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

    private fun storeTokens (
        accessToken : String,
        refreshToken : String
    ){
        this.prefStore.saveIntoPref("Access_Token", accessToken)
        this.prefStore.saveIntoPref("Refresh_Token", refreshToken)
    }

    private fun removeTokens() {
        this.prefStore.removeFromPref("Access_Token")
        this.prefStore.removeFromPref("Refresh_Token")
    }

}
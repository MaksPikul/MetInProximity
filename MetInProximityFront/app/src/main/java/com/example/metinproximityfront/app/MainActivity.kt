package com.example.metinproximityfront.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.metinproximityfront.ui.theme.MetInProximityFrontTheme
import com.example.metinproximityfront.views.Home.HomeView
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.security.MessageDigest
import java.security.SecureRandom


class MainActivity : ComponentActivity() {
    private lateinit var service: AuthorizationService
    private lateinit var navHostController: NavHostController

    private lateinit var model: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAuthService()
        //TODO: (this.application as Application).setMainActivity(this)

        val model: MainActivityViewModel by viewModels()
        this.model = model

        // TODO: this.createViews()

        //TODO: this.model.initialize(this::onLoaded)

        enableEdgeToEdge()
        setContent {
            MetInProximityFrontTheme {
                navHostController = rememberNavController()
                NavHost(navController = navHostController, startDestination = "Home") {
                    composable("Home") { login() }
                    composable("Chat") { HomeView() }

                }



            }
        }
    }




    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                handleAuthorizationResponse(result.data!!)
                navHostController.navigate("Chat")
            }
        }
    }

    private fun handleAuthorizationResponse(intent: Intent){
        val authorizationResponse : AuthorizationResponse? = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)

        //TODO: authState

        //TODO: send REQ to my server
    }



    private fun goToHome(i : Intent){
        startActivity(i)
        finish()
    }

    fun attemptAuthorization(){
        val secureRandom = SecureRandom()
        val bytes = ByteArray(64)
        secureRandom.nextBytes(bytes)

        val encoding = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
        val codeVerifier = Base64.encodeToString(bytes, encoding)

        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(codeVerifier.toByteArray())
        val codeChallenge = Base64.encodeToString(hash, encoding)

        val name = "Google"
        val oauthUrl = Uri.parse("https://accounts.google.com/o/oauth2/v2/auth")
        val redirectUri = Uri.parse("com.example.metinproximityfront:/oauth2callback") //https://localhost:7238/api/account/oauth/google"
        val tokenUri = Uri.parse("https://oauth2.googleapis.com/token")
        val clientId = "150643742648-401ps6vje504ln6pru630jsrfhg3tt90.apps.googleusercontent.com"

        val config = AuthorizationServiceConfiguration(oauthUrl, tokenUri)


        val request = AuthorizationRequest
            .Builder(config, clientId, ResponseTypeValues.CODE, redirectUri)
            .setScopes("profile email")
            .build()
            /*
            .setCodeVerifier(codeVerifier,
                codeChallenge,
                "S256")

             */

        val intent = service.getAuthorizationRequestIntent(request)


        launcher.launch(intent)

    }

    private fun initAuthService() {
        val appAuthConfiguration = AppAuthConfiguration.Builder()
            .setBrowserMatcher(
                BrowserAllowList(
                    VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                    VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
                )
            ).build()

        this.service = AuthorizationService(
            this,
            appAuthConfiguration)
    }


    private fun saveIntoSharedPref(value: String) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Initialize/open an instance of EncryptedSharedPreferences on below line.
        val sharedPreferences = EncryptedSharedPreferences.create(
            // passing a file name to share a preferences
            "preferences",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        // on below line we are storing data in shared preferences file.
        sharedPreferences.edit().putString("auth_token", value).apply()
    }




    private fun handleAuthServerCall(json : String, url : String): String {
        var client : OkHttpClient = OkHttpClient()
        val JSON: MediaType = "application/json".toMediaType()


            var body : RequestBody  = json.toRequestBody(JSON);
            var request : Request  = Request.Builder()
                .url(url)
                .post(body)
                .build();

            var response : Response = client
                .newCall(request).execute()

            return response.body?.string() ?: ""
    }





    @Composable
    fun login(){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {attemptAuthorization()}) { }
        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MetInProximityFrontTheme {
        Greeting("Android")
    }
}
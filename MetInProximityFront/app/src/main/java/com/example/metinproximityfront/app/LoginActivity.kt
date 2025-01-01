package com.example.metinproximityfront.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metinproximityfront.ui.theme.MetInProximityFrontTheme
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

class LoginActivity : ComponentActivity() {
    private lateinit var service: AuthorizationService

    override fun onCreate(savedInstanceState: Bundle?) {

        service = AuthorizationService(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetInProximityFrontTheme {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {goToHome()/*googleAuth()*/}) { }
                }

            }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK) {
            //val ex = AuthorizationException.fromIntent(it.data!!)
            //val result = AuthorizationResponse.fromIntent(it.data!!)

            // get token, call my backend, receive jwt, save Jwt + user info?

                goToHome()
        }
    }

    private fun goToHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun googleAuth() {
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

        val intent = service.getAuthorizationRequestIntent(request)
        launcher.launch(intent)
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
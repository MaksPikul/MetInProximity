package com.example.metinproximityfront.app

import android.content.Intent
import android.os.Bundle
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
import com.example.metinproximityfront.views.Home.LoginView
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response


class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private lateinit var model: MainActivityViewModel

    private val LoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        run {
            // redirect here too, depending on success
            this.model.FinishLogin(result.data!!)
        }
    }

    //TODO: LogoutLauncher


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: (this.application as Application).setMainActivity(this)

        val model: MainActivityViewModel by viewModels()
        this.model = model

        this.createViews()

        //TODO: this.model.initialize(this::onLoaded)

        enableEdgeToEdge()

    }

    private fun createViews(){
        setContent {
            MetInProximityFrontTheme {
                navHostController = rememberNavController()

                NavHost(navController = navHostController, startDestination = "Home") {
                    composable("Home") {
                        LoginView(
                            providers = model.GetOAuthProviders(),
                            StartLogin = { launcher, provider -> model.StartLogin(provider, launcher) },
                            LoginLauncher ={ intent -> LoginLauncher.launch(intent) }

                        )
                    }

                    composable("Chat") { HomeView() }
                }
            }
        }
    }


}



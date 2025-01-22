package com.example.metinproximityfront.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metinproximityfront.ui.theme.MetInProximityFrontTheme
import com.example.metinproximityfront.views.Home.HomeView
import com.example.metinproximityfront.views.Home.LoginView


class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private lateinit var model: MainActivityViewModel

    private val LoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        run {
            // redirect here too, depending on success
            val successRedirect = {
                this.navHostController.navigate("Home")
            }
            this.model.authService.FinishLogin(result.data!!, successRedirect)
        }
    }

    //TODO: LogoutLauncher, probably not necessary


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // (this.application as Application).setMainActivity(this)

        val model: MainActivityViewModel by viewModels()
        this.model = model

        this.createViews()

        //TODO: this.model.initialize(this::onLoaded)

        enableEdgeToEdge()

    }

    private fun createViews(){
        setContent {
            MetInProximityFrontTheme {
                val nhc = rememberNavController()
                this.navHostController = nhc

                NavHost(
                    navController = nhc,
                    startDestination = "Home"/*if (this.model.authService.IsLoggedIn()) "home" else "login"*/
                ) {
                    // TODO: Blank Composable? for when loading
                    composable("Login") {

                        LoginView(
                            providers = model.GetOAuthProviders(),
                            // This looks hella complicated, but its very nice
                            // pass 1 parameter here, pass a second parameter in login view
                            StartLogin = { provider ->
                                model.authService.StartLogin(
                                    provider,
                                    {LoginLauncher.launch(intent)}
                                ) },
                        )


                    }

                    composable("Home") { HomeView(/* this.model.authenticator.logout(this.navHostController.navigate("login")) */) }



                }
            }
        }
    }

}




package com.example.metinproximityfront.app

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
import com.example.metinproximityfront.views.Login.LoginView


class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController

    private lateinit var mainVM: MainActivityViewModel
    //private lateinit var homeVM : HomeViewModel


    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        run {
            // redirect here too, depending on success
            val onSuccessfulLogin = {
                this.mainVM.startLocationService()
                this.navHostController.navigate("Home")
            }
            // This should return errors if any and update UI in Login page, custom toast that lasts long and is large??
            mainVM.authService.FinishLogin(result.data!!, onSuccessfulLogin)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // (this.application as Application).setMainActivity(this)

        val mainModel: MainActivityViewModel by viewModels()
        this.mainVM = mainModel

        //val homeModel: HomeViewModel by viewModels()
        //this.homeVM = homeModel

        this.createViews()

        //TODO: this.model.initialize(this::onLoaded)

        enableEdgeToEdge()
    }

    override fun onPause() {
        super.onPause()

        // todo: if (!userWantsBackground location and messages)
        //this.mainVM.stopLocationService()
    }

    override fun onResume() {
        super.onResume()
        if (this.mainVM.authService.IsLoggedIn()) {
            this.mainVM.startLocationService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //this.mainVM.stopLocationService()
    }

    private fun createViews(){
        setContent {
            MetInProximityFrontTheme {
                val nhc = rememberNavController()
                this.navHostController = nhc

                NavHost(
                    navController = nhc,
                    startDestination = if (!this.mainVM.authService.IsLoggedIn()) "Login" else "Home"
                ) {
                    // TODO: Blank Composable? for when loading
                    composable("Login") {

                        LoginView(
                            providers = mainVM.oAuthProviderFactory.getProviders(),
                            // This looks hella complicated, but its very nice
                            // pass 1 parameter here, pass a second parameter in login view
                            StartLogin = { provider ->
                                mainVM.authService.StartLogin(provider) { intent ->
                                    loginLauncher.launch(intent)
                                }
                            }
                        )
                    }

                    composable("Home") { HomeView(
                        //homeVM,
                        {mainVM.authService.Logout({navHostController.navigate("Login")})}
                    ) }
                }
            }
        }
    }

}




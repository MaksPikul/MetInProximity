package com.example.metinproximityfront.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metinproximityfront.ui.theme.MetInProximityFrontTheme
import com.example.metinproximityfront.views.Home.HomeView
import com.example.metinproximityfront.views.Home.HomeViewModel
import com.example.metinproximityfront.views.Login.LoginView


class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController

    private lateinit var mainVM: MainActivityViewModel
    private lateinit var homeVM : HomeViewModel


    private val LoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        run {
            // redirect here too, depending on success
            val successRedirect = {
                this.navHostController.navigate("Home")
            }
            // This should return errors if any and update UI in Login page, custom toast that lasts long and is large??
            this.mainVM.authService.FinishLogin(result.data!!, successRedirect)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // (this.application as Application).setMainActivity(this)




        val mainModel: MainActivityViewModel by viewModels()
        this.mainVM = mainModel

        val homeModel: HomeViewModel by viewModels()
        this.homeVM = homeModel

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
                    startDestination = if (this.mainVM.authService.IsLoggedIn()) "Login" else "Home"
                ) {
                    // TODO: Blank Composable? for when loading
                    composable("Login") {

                        LoginView(
                            providers = mainVM.oAuthProviderFactory.getProviders(),
                            // This looks hella complicated, but its very nice
                            // pass 1 parameter here, pass a second parameter in login view
                            StartLogin = { provider ->
                                mainVM.authService.StartLogin(
                                    provider,
                                    {LoginLauncher.launch(intent)}
                                ) },
                        )
                    }

                    composable("Home") { HomeView(
                        homeVM,
                        {mainVM.authService.Logout({navHostController.navigate("Login")})}
                    ) }
                }
            }
        }
    }

}




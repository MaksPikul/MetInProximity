package com.example.metinproximityfront.app

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : ComponentActivity() {

    private lateinit var mainVm : MainActivityViewModel

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        run {

            // redirect here too, depending on success
            val onSuccessfulLogin = {
                this.InitAndMoveToHome()
            }
            val fcmToken = "" //task.result
            // This should return errors if any and update UI in Login page, custom toast that lasts long and is large??
            mainVm.authService.FinishLogin(result.data!!, onSuccessfulLogin, fcmToken)

            /*
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM token failed :(", task.exception)
                    return@addOnCompleteListener
                }

                val fcmToken = task.result
                // This should return errors if any and update UI in Login page, custom toast that lasts long and is large??
                mainVm.authService.FinishLogin(result.data!!, onSuccessfulLogin, fcmToken)
            }
             */
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // (this.application as Application).setMainActivity(this)

        val mainModel: MainActivityViewModel by viewModels()
        this.mainVm = mainModel

        this.createViews()

        /*
        if (this.mainVm.authService.IsLoggedIn()){
            this.InitAndMoveToHome()
        }
         */

        // TODO: this.model.initialize(this::onLoaded)

        enableEdgeToEdge()
    }

    override fun onPause() {
        super.onPause()

        // todo: if (!userWantsBackground location and messages)
        //this.mainVM.homeVm.stopLocationService()
    }

    override fun onResume() {
        super.onResume()
        if (this.mainVm.authService.IsLoggedIn()) {
            this.mainVm.homeVm.startServices()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //this.mainVM.homeVm.stopLocationService()
    }

    private fun createViews(){
        setContent {
            MetInProximityFrontTheme {
                val nhc = rememberNavController()
                this.mainVm.navHostController = nhc

                NavHost(
                    navController = nhc,
                    // instead of this, check for tokens, if present, run InitAndMoveToHome
                    startDestination = "Login"
                ) {
                    // TODO: Blank Composable? for when loading
                    composable("Login") {
                    LoginView(
                        providers = mainVm.oAuthProviderFactory.getProviders(),
                        // This looks hella complicated, but its very nice
                        // pass 1 parameter here, pass a second parameter in login view
                        StartLogin = { provider ->
                            mainVm.authService.StartLogin(provider) { intent ->
                                loginLauncher.launch(intent)
                        }}
                    ) }

                    composable("Home") {
                    HomeView(
                        mainVm.homeVm,
                        {mainVm.authService.Logout({mainVm.navHostController.navigate("Login")})}
                    ) }
                }
            }
        }
    }

    private fun InitAndMoveToHome() {
        this.mainVm.InitHomeViewModel()

        //this.mainVm.homeVm.startServices()

        mainVm.navHostController.navigate("Home")
    }

}




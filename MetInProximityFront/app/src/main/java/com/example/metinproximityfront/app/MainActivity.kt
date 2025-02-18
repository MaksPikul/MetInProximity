package com.example.metinproximityfront.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metinproximityfront.services.locaction.LocationService
import com.example.metinproximityfront.ui.theme.MetInProximityFrontTheme
import com.example.metinproximityfront.views.Home.HomeView
import com.example.metinproximityfront.views.loading.LoadingView
import com.example.metinproximityfront.views.Login.LoginView


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

            val onFailedLogin = {errorMsg : String?, errorCode : String? ->
                this.mainVm.stopLoadingView("Login")
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                Log.e("Auth Error",errorMsg.toString())
                Unit
            }

            val fcmToken = "" //task.result
            // This should return errors if any and update UI in Login page, custom toast that lasts long and is large??
            this.mainVm.startLoadingView()
            mainVm.authService.FinishLogin(result.data!!,fcmToken, onSuccessfulLogin, onFailedLogin)

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
        Log.i("Starting", "starting onCreate")

        val mainModel: MainActivityViewModel by viewModels()
        this.mainVm = mainModel

        this.createViews()

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
        /*
        Log.i("correct", this.mainVm.authService.IsLoggedIn().toString())
        if (this.mainVm.authService.IsLoggedIn() /* TODO HOMEVM && Check if initialized */) {
            this.mainVm.homeVm.startServices()
        }
         */
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
                    navController = this.mainVm.navHostController,
                    // instead of this, check for tokens, if present, run InitAndMoveToHome
                    startDestination = "Loading"
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

                    composable("Loading"){
                    LoadingView()
                    }
                }
                val loggedIn by remember { mutableStateOf(mainVm.authService.IsLoggedIn()) }
                if (loggedIn){
                    this.InitAndMoveToHome()
                }
                else {
                    this.mainVm.stopLoadingView("Login")
                }
            }
        }
    }

    private fun InitAndMoveToHome() {
        this.mainVm.InitHomeViewModel()
        //this.mainVm.homeVm.startServices()

        val intent = Intent(this, LocationService::class.java);
        applicationContext.startForegroundService(intent)
        this.mainVm.stopLoadingView("Home")
    }

}




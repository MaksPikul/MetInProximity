package com.example.metinproximityfront.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.services.location.LocationService
import com.example.metinproximityfront.ui.theme.MetInProximityFrontTheme
import com.example.metinproximityfront.views.Home.HomeView
import com.example.metinproximityfront.views.loading.LoadingView
import com.example.metinproximityfront.views.Login.LoginView


class MainActivity : ComponentActivity() {

    private lateinit var mainVm : MainActivityViewModel

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE_LOCATION
    )

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        run {

            //val fcmToken = task.result
            // This should return errors if any and update UI in Login page, custom toast that lasts long and is large??
            this.mainVm.startLoadingView()
            mainVm.authService.FinishLogin(
                result.data!!,
                "",
                mainVm.onSuccLogin,
                mainVm.onFailLogin
            )

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

        this.PremissionCheck()

        val mainModel: MainActivityViewModel by viewModels()
        this.mainVm = mainModel

        this.createViews()

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

                if (mainVm.authService.IsLoggedIn()){
                    mainVm.InitAndLoadHomeVm()
                }
                else {
                    this.mainVm.stopLoadingView("Login")
                }
            }
        }
    }





    // I need a permission Manager

    private fun PremissionCheck() {

        if (arePermissionsGranted()) {

            Log.e("prems", "prems granted")
        } else {
            requestPermissions()
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == 1) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.e("prems","permissions accepted")
            } else {
                Toast.makeText(this, "Permissions are required for location access.", Toast.LENGTH_SHORT).show()
            }
        }

        Log.e("Permissions", "Fine location granted: ${ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED}")
        Log.e("Permissions", "Coarse location granted: ${ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED}")
        Log.e("Permissions", "Background location granted: ${ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED}")
        Log.e("Permissions", "Foreground service location granted: ${ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED}")
    }
}




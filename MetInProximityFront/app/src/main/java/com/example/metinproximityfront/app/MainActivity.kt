package com.example.metinproximityfront.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metinproximityfront.app.viewModels.AuthViewModel
import com.example.metinproximityfront.app.viewModels.HomeViewModel
import com.example.metinproximityfront.app.viewModels.MainViewModel
import com.example.metinproximityfront.services.permissions.PermissionListener
import com.example.metinproximityfront.ui.theme.MetInProximityFrontTheme
import com.example.metinproximityfront.views.Home.HomeView
import com.example.metinproximityfront.views.loading.LoadingView
import com.example.metinproximityfront.views.Login.LoginView


class MainActivity : ComponentActivity() {

    private lateinit var mainVm : MainViewModel
    private lateinit var authVm : AuthViewModel
    private lateinit var homeVm : HomeViewModel

    val permissionListener = PermissionListener(this) // Really dont like the fact this is here, dont want to have a large nesting of parameters tho

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        run {

            //val fcmToken = task.result
            // This should return errors if any and update UI in Login page, custom toast that lasts long and is large??
            this.authVm.startLoadingView()
            this.authVm.authService.FinishLogin(
                result.data!!,
                "",
                this.authVm.onSuccLogin,
                this.authVm.onFailLogin
            )

        /*
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM token failed :(", task.exception)
                    return@addOnCompleteListener
                }

                val fcmToken = task.result
                this.authVm.startLoadingView()
                this.authVm.authService.FinishLogin(
                    result.data!!,
                    fcmToken,
                    this.authVm.onSuccLogin,
                    this.authVm.onFailLogin
                )
            }
        */
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainModel: MainViewModel by viewModels( )
        this.mainVm = mainModel

        this.authVm = AuthViewModel(
            application,
            mainVm
        )

        this.homeVm = HomeViewModel(
            mainVm.userActionService,
            mainVm.mapService,
            mainVm.msgService
        )

        this.authVm.permissionManager.checkPermissions(this, permissionListener)
        this.authVm.permissionManager.logPermissionsStatus(this)

        this.createViews()

        enableEdgeToEdge()
    }

    private fun createViews(){
        setContent {
            MetInProximityFrontTheme {
                val nc = rememberNavController()
                mainVm.navController = nc

                NavHost(
                    navController = nc,
                    // instead of this, check for tokens, if present, run InitAndMoveToHome
                    startDestination = "Loading"
                ) {
                    composable("Login") {
                    LoginView(
                        // This looks hella complicated, but its very nice
                        // pass 1 parameter here, pass a second parameter in login view
                        StartLogin = { provider ->
                            this@MainActivity.authVm.authService.StartLogin(provider) { intent ->
                                loginLauncher.launch(intent)
                        }}
                    ) }

                    composable("Home") {
                    HomeView(
                        homeVm,
                        { this@MainActivity.authVm.authService.Logout({ this@MainActivity.mainVm.navController.navigate("Login")})}
                    ) }

                    composable("Loading"){
                    LoadingView()
                    }
                }

                this.authVm.CheckLoginStatus()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        this.authVm.permissionManager.handlePermissionsResult(this, requestCode, grantResults, permissionListener)
    }


    override fun onPause() {
        super.onPause()
        this.mainVm.stopServices()
    }
    override fun onResume() {
        super.onResume()
        if (this.authVm.authService.IsLoggedIn() /* TODO HOMEVM && Check if initialized */) {
            this.mainVm.startServices()

        }
    }
    override fun onDestroy() {
        this.mainVm.stopServices()
        super.onDestroy()
    }
}




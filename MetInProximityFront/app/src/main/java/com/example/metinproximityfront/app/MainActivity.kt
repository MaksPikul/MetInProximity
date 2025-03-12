package com.example.metinproximityfront.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.NavHostController
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
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : ComponentActivity() {

    private lateinit var mainVm : MainViewModel
    private lateinit var authVm : AuthViewModel
    private lateinit var homeVm : HomeViewModel

    val permissionListener = PermissionListener(this) // Really dont like the fact this is here, dont want to have a large nesting of parameters tho

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        run {
            Log.e("Micosoft", "got here")
            //val fcmToken = task.result
            // This should return errors if any and update UI in Login page, custom toast that lasts long and is large??

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM token failed :(", task.exception)
                    return@addOnCompleteListener
                }

                val fcmToken = task.result
                Log.i("FcmToken" , fcmToken)
                this.authVm.startLoadingView()
                this.authVm.authService.FinishLogin(
                    result.data!!,
                    fcmToken,
                    this.authVm.onSuccLogin,
                    this.authVm.onFailLogin
                )
            }
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

        this.createNotificationChannel()


        this.createViews()

            /*
            LaunchedEffect(intentState.value) {
                Log.i("Main", "launchedEffect")
                if (authVm.authService.IsLoggedIn()){
                    homeVm.handleMessageNotifIntent(
                        intentState.value
                    )
                }
                else {
                    mainVm.navController.navigate("Login")
                }
            }
             */


        enableEdgeToEdge()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.i("Main", "Handles intent")
        setIntent(intent)
        homeVm.handleMessageNotifIntent(
            intent
        )
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
                                }
                            }
                        )
                    }

                    composable("Home") {
                        HomeView(
                            homeVm,
                            {
                                this@MainActivity.authVm.authService.Logout({
                                    this@MainActivity.mainVm.navController.navigate(
                                        "Login"
                                    )
                                })
                            }
                        )
                    }

                    composable("Loading") {
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
        Log.i("MainActivity", "pauses")
        this.mainVm.stopServices()
    }
    override fun onResume() {
        super.onResume()
        Log.i("MainActivity", "resumes")
        if (this.authVm.authService.IsLoggedIn()) {
            this.mainVm.startServices()
        }
    }
    override fun onDestroy() {
        this.mainVm.stopServices()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "firebase_channel"
            val channelName = "Default Notifications"
            val description = "Firebase default notification channel"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = description

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}




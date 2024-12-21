package com.example.metinproximityfront

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import com.example.metinproximityfront.ViewModels.AuthViewModel
import com.example.metinproximityfront.ui.Screens.HomeScreen
import com.example.metinproximityfront.ui.Screens.SignInScreen
import com.example.metinproximityfront.ui.theme.MetInProximityFrontTheme


class MainActivity : ComponentActivity() {
    private val deepLinkState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetInProximityFrontTheme {
                val navController = rememberNavController()

                LaunchedEffect(deepLinkState.value) {
                    deepLinkState.value?.let { destination ->
                        navController.navigate(destination)
                        // Clear the state to avoid repeated navigation
                        deepLinkState.value = null
                    }
                }
                // Navigation host to handle navigation between composable screens
                NavHost(navController = navController, startDestination = "signIn") {
                    composable("signIn") {
                        SignInScreen()
                    }
                    composable("home") {
                        HomeScreen(deepLinkState)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        intent.data?.let { uri ->
            if (uri.scheme == "maks.example.scheme" && uri.host == "callback") {
                val jwt = uri.getQueryParameter("token")
                if (jwt != null) {
                    authViewModel.saveJwt(jwt) // Save the JWT securely for future use
                    deepLinkState.value = "home"
                } else {
                    // Handle case JWT is missing or bad
                    // Toast An Error
                }
            }
        }
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
/*
Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    Greeting(
        name = "Android",
        modifier = Modifier.padding(innerPadding)
    )
}
 */
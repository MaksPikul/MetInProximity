package com.example.metinproximityfront.app

import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metinproximityfront.app.ui.theme.MetInProximityFrontTheme
import com.example.metinproximityfront.views.Home.HomeView

class HomeActivity : ComponentActivity() {

    private lateinit var model :HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val model: HomeActivityViewModel by viewModels()
        this.model = model

        //this.model.initialize(this::onLoaded)
        setContent {

            MetInProximityFrontTheme {
                val navHostController = rememberNavController()

                NavHost(navController = navHostController, startDestination = "Home") {
                    composable("Home") { HomeView() }
                    composable("Chat") { }
                    // Add more destinations similarly.
                }
            }
    }
}
    }

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MetInProximityFrontTheme {
        Greeting2("Android")
    }
}
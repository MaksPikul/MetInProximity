package com.example.metinproximityfront.views.Home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.app.MainActivityViewModel
import com.example.metinproximityfront.config.OAuth.GoogleOAuthConfig
import com.example.metinproximityfront.config.OAuth.OAuthConfig
import com.example.metinproximityfront.ui.theme.MetInProximityFrontTheme
import java.security.Provider


@Composable
fun LoginView(
    providers : List<OAuthConfig>,

    StartLogin: (provider: OAuthConfig) -> Unit,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color.White, Color.Black),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f), // Top-left corner
                    end = androidx.compose.ui.geometry.Offset(
                        1500F,//Float.POSITIVE_INFINITY,
                        1500F//Float.POSITIVE_INFINITY
                    ) // Bottom-right
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            providers.forEach { provider ->
                Button(
                    onClick = { StartLogin(provider) }
                ) {
                    Text("Login with " + provider.name.replaceFirstChar { it.uppercase() })
                }
            }

        }
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    val list = mutableListOf<OAuthConfig>()
    //Add more to extend
    list.add(
        GoogleOAuthConfig()
    )

    LoginView (providers = list, {})
}


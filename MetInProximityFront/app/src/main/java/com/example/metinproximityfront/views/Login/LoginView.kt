package com.example.metinproximityfront.views.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.config.oauth.GoogleOAuthConfig
import com.example.metinproximityfront.config.oauth.MicrosoftOAuthConfig
import com.example.metinproximityfront.config.oauth.OAuthConfig
import com.example.metinproximityfront.factories.OAuthProviderFactory


@Composable
fun LoginView(
    StartLogin: (provider: OAuthConfig) -> Unit,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color.Black) // Gradient from Red to Blue
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

            OAuthProviderFactory.getProviders().forEach { provider ->
                Button(
                    onClick = { StartLogin(provider) },
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
    val list = listOf(
        GoogleOAuthConfig(),
        MicrosoftOAuthConfig()
    )

    LoginView ({})
}


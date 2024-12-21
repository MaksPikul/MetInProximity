package com.example.metinproximityfront.ui.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.Managers.OAuthManager

@Composable
fun SignInScreen(
    //deepLinkState : MutableState<String?>
){
    val oAuthManager  = OAuthManager()
    val providers = oAuthManager.getProviders()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        //loop the providers
        providers.forEach { provider ->
            SocialLoginButton(
                buttonText = "Login with ${provider.name}",
                onClick = { oAuthManager.startOAuthFlow(provider) },
                icon = Icons.Filled.Face
            )
        }
    }
}

@Composable
fun SocialLoginButton(
    buttonText: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Button(
        onClick = onClick,
        modifier = Modifier.width(250.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue) // Customize button color
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
                tint = Color.White
            )
            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}


@Preview
@Composable
fun PreviewLoginScreen() {

    SignInScreen()
}
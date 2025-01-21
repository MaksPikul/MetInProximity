package com.example.metinproximityfront.views.Home

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.config.OAuth.OAuthConfig
import com.example.metinproximityfront.ui.theme.MetInProximityFrontTheme
import java.security.Provider


@Composable
fun LoginView(
    providers : List<OAuthConfig>,

    StartLogin: (
        launchAction: (i: Intent) -> Unit,
        provider: OAuthConfig
    ) -> Unit,

    LoginLauncher: (
        i: Intent
    ) -> Unit
){

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        providers.forEach { provider ->
            Button(
                onClick = { StartLogin(LoginLauncher, provider) }
            ) {
                Text("Login with " + provider.name.replaceFirstChar{it.uppercase()})
            }
        }




    }
}


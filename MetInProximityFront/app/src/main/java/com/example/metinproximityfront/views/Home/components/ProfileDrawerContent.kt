package com.example.metinproximityfront.views.Home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metinproximityfront.data.entities.account.User

@Composable
fun ProfileDrawerContent (
    logout : () -> Unit
) {
    ModalDrawerSheet (
    modifier =  Modifier.width((LocalConfiguration.current.screenWidthDp * 0.7).dp)
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val user = User.userData.collectAsState()

            Text(user.value?.username.toString(), fontSize = 22.sp)
            Text(user.value?.email.toString(), fontSize = 14.sp)

            HorizontalDivider(modifier = Modifier.padding(0.dp, 10.dp))
            NavigationDrawerItem(
                label = { Text(text = "Light Mode") },
                selected = false,
                onClick = { /*TODO*/ }
            )

            NavigationDrawerItem(
                label = { Text(text = "Settings") },
                selected = false,
                onClick = { /*TODO*/ }
            )

            NavigationDrawerItem(
                label = { Text(text = "Log out", color = Color.Red) },
                selected = false,
                onClick = { logout() }
            )
        }
    }
}
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileDrawerContent (
    logout : () -> Unit
) {
    ModalDrawerSheet (
    modifier =  Modifier.width((LocalConfiguration.current.screenWidthDp * 0.7).dp)
    ){
        var checked = false;
        Column (
            modifier = Modifier
                .fillMaxSize() // Allow scrolling for long text
                .padding(16.dp), // Add padding to avoid edge clipping
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Text("Username", fontSize = 22.sp)
            Text("email", fontSize = 14.sp)

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
                label = { Text(text = "Notifications") },
                badge = {  Switch(
                    modifier = Modifier.scale(0.7f),
                    checked = checked,
                    onCheckedChange = {
                        false
                    },thumbContent = if (checked) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Green,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Red,
                    )
                ) },
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
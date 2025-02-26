package com.example.metinproximityfront.views.Home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.enums.ScreenState

@Composable
fun BottomNavBar(
    changeSheetState : () -> Unit,
    changeScreen : (ScreenState, ChatUser?) -> Unit
) {

    NavigationBar(
        modifier = Modifier.padding(12.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        NavigationBarItem(
            onClick = {
                changeScreen(ScreenState.MAP, null)
            },
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home , contentDescription = "Home Page") },
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick = {
                changeScreen(ScreenState.PUBLIC, null)
            },
            label = { Text("Public") },
            icon = { Icon(Icons.Default.Menu , contentDescription = "Home Page") },
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick =  changeSheetState ,
            label = { Text("Private") },
            icon = { Icon(Icons.Default.Menu , contentDescription = "Home Page") },
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick = {},
            label = { Text("Events") },
            icon = { Icon(Icons.Default.Place , contentDescription = "Home Page") },
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
    }
}

package com.example.metinproximityfront.views.Home

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

@Composable
fun BottomNavBar(
    changeState : () -> Unit
) {

    NavigationBar(
        modifier = Modifier.padding(12.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        NavigationBarItem(
            onClick = {},
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home , contentDescription = "Home Page") },
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick = {},
            label = { Text("Public") },
            icon = { Icon(Icons.Default.Menu , contentDescription = "Home Page") },
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick =  changeState ,
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

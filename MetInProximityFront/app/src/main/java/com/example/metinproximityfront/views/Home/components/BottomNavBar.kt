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
import com.example.metinproximityfront.data.enums.ScreenState
import com.example.metinproximityfront.app.viewModels.HomeViewModel

@Composable
fun BottomNavBar(
    homeVm: HomeViewModel
) {

    NavigationBar(
        modifier = Modifier.padding(12.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        NavigationBarItem(
            onClick = { homeVm.changeScreen(ScreenState.MAP) }
            ,
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home , contentDescription = "Home Page") },
            selected = homeVm.uiState.value.currentScreen == ScreenState.MAP,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick = {
                homeVm.changeScreen(ScreenState.PUBLIC)
            },
            label = { Text("Public") },
            icon = { Icon(Icons.Default.Menu , contentDescription = "Public Chat Page") },
            selected = homeVm.uiState.value.currentScreen == ScreenState.PUBLIC,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick =  { homeVm.toggleBottomSheet() } ,
            label = { Text("Private") },
            icon = { Icon(Icons.Default.Menu , contentDescription = "Private Chat Page") },
            selected = homeVm.uiState.value.currentScreen == ScreenState.PRIVATE,
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

package com.example.metinproximityfront.views.Home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.app.ui.theme.MetInProximityFrontTheme
import kotlinx.coroutines.launch


@Composable
fun HomeView() {

    var drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        content = {
            Scaffold (
                bottomBar = {BotNavbar()},
                content= {padding ->
                    FloatingActionButton(onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }}
            )
        },
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
            }
        }
    )
}


@Composable
fun BotNavbar() {
    // Give it some padding, round corners
    NavigationBar {
        NavigationBarItem(
            onClick = {},
            label = {Text("Home")},
            icon = {Icon(Icons.Default.Home , contentDescription = "Home Page")},
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick = {},
            label = {Text("Public")},
            icon = {Icon(Icons.Default.Home , contentDescription = "Home Page")},
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick = {},
            label = {Text("Private")},
            icon = {Icon(Icons.Default.Home , contentDescription = "Home Page")},
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick = {},
            label = {Text("Events")},
            icon = {Icon(Icons.Default.Home , contentDescription = "Home Page")},
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )


    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MetInProximityFrontTheme {
        HomeView()
    }
}
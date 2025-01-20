package com.example.metinproximityfront.views.Home

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.app.ui.theme.MetInProximityFrontTheme
import kotlinx.coroutines.launch


@Composable
fun HomeView() {

    /*
    var extras = i.extras
    var s = i.getStringExtra("result")

    extras?.keySet()?.forEach { key ->
        s += "Key: $key, Value: ${extras.get(key)} "
    }
    */
    var drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        content = {
            Scaffold (
                bottomBar = {BotNavbar()},
                content= {padding ->
                    /*FloatingActionButton(
                        modifier = Modifier.padding(12.dp),
                        onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }) {
                        Text("x" + result)
                        //Icon(Icons.Default.Person, contentDescription = "Add")
                    }*/
                    Box(
                        modifier = Modifier
                            .fillMaxSize() // Take the full screen size
                            .padding(padding)
                    ) {
                        // Center content vertically and horizontally
                        Column(
                            modifier = Modifier
                                .fillMaxSize() // Allow scrolling for long text
                                .padding(16.dp), // Add padding to avoid edge clipping
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "x" ,
                                modifier = Modifier
                                    .fillMaxWidth(), // Ensures the text spans the screen width
                                textAlign = TextAlign.Center,// Apply default styling
                            )
                        }
                    }


                }


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
    NavigationBar(
        modifier = Modifier.padding(12.dp).clip(RoundedCornerShape(16.dp))
    ) {
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
            icon = {Icon(Icons.Default.Menu , contentDescription = "Home Page")},
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick = {},
            label = {Text("Private")},
            icon = {Icon(Icons.Default.Menu , contentDescription = "Home Page")},
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )
        NavigationBarItem(
            onClick = {},
            label = {Text("Events")},
            icon = {Icon(Icons.Default.Place , contentDescription = "Home Page")},
            selected = false,
            //unselectedIcon = Icons.Outlined.Home,
        )


    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MetInProximityFrontTheme {
       // HomeView()
    }
}
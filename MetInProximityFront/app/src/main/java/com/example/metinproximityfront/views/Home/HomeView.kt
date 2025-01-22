package com.example.metinproximityfront.views.Home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metinproximityfront.app.ui.theme.MetInProximityFrontTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView() {

    var drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    var changeState = { showBottomSheet = !showBottomSheet}

    ModalNavigationDrawer(
        drawerState = drawerState,
        content = {

            Scaffold (
                bottomBar = {BotNavbar(changeState)},
                content= {padding ->


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


                            Box(
                                modifier = Modifier
                                    .fillMaxSize()  // Or you can use specific width/height like .height(200.dp)
                                    .padding(0.dp)
                                    .background(Color(0xFF4E88B5), shape = RoundedCornerShape(16.dp))  // Set the background color to Cyan
                            ){

                                FloatingActionButton(
                                    containerColor = Color(0xFFFF9F00),
                                    modifier = Modifier.padding(8.dp),
                                    onClick = {
                                        scope.launch {
                                            drawerState.apply {
                                                if (isClosed) open() else close()
                                            }
                                        }
                                    }) {
                                    Text("x")
                                    //Icon(Icons.Default.Person, contentDescription = "Add")
                                }
                            }


                        }

                    }


                }


            )
        },
        drawerContent = {
            ModalDrawerSheet (
                modifier =  Modifier.width((LocalConfiguration.current.screenWidthDp * 0.7).dp)
            ){
                Column (
                    modifier = Modifier
                    .fillMaxSize() // Allow scrolling for long text
                    .padding(16.dp), // Add padding to avoid edge clipping
                    //verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    var checked = false;
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
                        onClick = { /*TODO*/ }
                    )
                }
            }
        }
    )
}


@Composable
fun BotNavbar(
    changeState : () -> Unit
) {
    // Give it some padding, round corners
    NavigationBar(
        modifier = Modifier.padding(12.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
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
            onClick =  changeState ,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartialBottomSheet() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = { showBottomSheet = true }
        ) {
            Text("Display partial bottom sheet")
        }

        if (true) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false }
            ) {
                Text(
                    "Swipe up to open sheet. Swipe down to dismiss.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun sheet () {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val sheetState = rememberModalBottomSheetState()
            var isSheetOpen by rememberSaveable {
                mutableStateOf(false)
            }
            val scaffoldState = rememberBottomSheetScaffoldState()
            val scope = rememberCoroutineScope()



            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetContent = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(color = Color.Red),

                    )
                },
                sheetPeekHeight = 0.dp
            ) {



                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }) {
                        Text(text = "Open sheet")
                    }
                }

            }
        }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MetInProximityFrontTheme {
        sheet()
    }
}
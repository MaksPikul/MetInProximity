package com.example.metinproximityfront.views.Home

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.metinproximityfront.app.ui.theme.MetInProximityFrontTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    //homeVm : HomeViewModel,
    logout : ()-> Unit
) {

    // TODO : will need to create a view model which Holds states
    var currentScreenState by remember { mutableStateOf("Map") }

    var drawerState = rememberDrawerState(DrawerValue.Closed)

    var showBottomSheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    var changeSheetState = {
        showBottomSheet = !showBottomSheet
    }

    PrivateChatListSheet(
        showBottomSheet,
        sheetState,
        changeSheetState
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        content = {
            Scaffold (
                bottomBar = {BottomNavBar(changeSheetState)},
                content= {padding -> NestedHomeView(padding, currentScreenState, drawerState)}
            )
        },
        drawerContent = { ProfileDrawerContent(logout) }
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MetInProximityFrontTheme {
        //HomeView({})
    }
}
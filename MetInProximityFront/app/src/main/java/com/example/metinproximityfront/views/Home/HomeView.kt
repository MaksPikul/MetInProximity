package com.example.metinproximityfront.views.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.metinproximityfront.data.enums.ScreenState
import com.example.metinproximityfront.views.Home.components.BottomNavBar
import com.example.metinproximityfront.views.Home.components.PrivateChatListSheet
import com.example.metinproximityfront.views.Home.components.ProfileDrawerContent
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.metinproximityfront.app.viewModels.HomeViewModel
import com.example.metinproximityfront.views.Chat.ChatView
import com.example.metinproximityfront.views.Home.components.ProfileButton
import com.example.metinproximityfront.views.Map.MapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    homeVm : HomeViewModel,
    logout : ()-> Unit
){
    val nc = rememberNavController()
    homeVm.navController = nc

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    PrivateChatListSheet(
        sheetState,
        homeVm
    )

    ModalNavigationDrawer(
        drawerState = homeVm.uiState.value.drawerState,
        content = {
            Scaffold (
                bottomBar = { BottomNavBar(
                    homeVm
                ) },
                content= {padding ->
                    // ------------
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                PaddingValues(
                                    start = 16.dp,
                                    top = padding.calculateTopPadding() + 16.dp,
                                    end = 16.dp,
                                    bottom = padding.calculateBottomPadding()
                                )
                            )
                            .background(color = Color.Red, shape = RoundedCornerShape(16.dp)),
                    ) {

                        NavHost(navController = nc, startDestination = ScreenState.MAP.toString()) {
                            composable( ScreenState.MAP.toString() ) {
                                MapView(homeVm)
                            }
                            composable(ScreenState.PUBLIC.toString()) {
                                ChatView(homeVm, null)
                            }
                            composable( ScreenState.PRIVATE.toString() )
                            {
                                ChatView(homeVm, homeVm.uiState.value.currentChatUser)
                            }
                        }

                        ProfileButton(homeVm.uiState.value.drawerState)                    }
                    // ----------------------
                }
            )
        },
        drawerContent = { ProfileDrawerContent(logout) }
    )

}
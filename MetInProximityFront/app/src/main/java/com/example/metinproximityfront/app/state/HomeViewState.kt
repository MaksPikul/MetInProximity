package com.example.metinproximityfront.app.state

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import com.example.metinproximityfront.data.enums.UserLoadState
import com.example.metinproximityfront.data.enums.ScreenState

data class HomeViewState (
    val currentScreen: ScreenState = ScreenState.MAP,
    val botSheetVisible: Boolean = false,
    var drawerState: DrawerState = DrawerState(DrawerValue.Closed),
)
package com.example.metinproximityfront.views.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.app.ui.theme.MetInProximityFrontTheme
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.enums.ScreenState
import com.example.metinproximityfront.views.Chat.ChatView
import com.example.metinproximityfront.views.Home.components.ProfileButton

@Composable
fun NestedHomeView (
    padding: PaddingValues,
    currentScreen: ScreenState,
    currentChatUser : ChatUser?,
    drawerState: DrawerState,
    homeVm : HomeViewModel
) {
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
        ScreenManager(
            currentScreen,
            homeVm,
            currentChatUser
        )
        ProfileButton(drawerState)
    }
}

@Composable
fun ScreenManager(
    currentScreen: ScreenState,
    homeVm: HomeViewModel,
    currentChatUser : ChatUser?
) {
    when (currentScreen) {
        ScreenState.MAP -> MapScreen()
        ScreenState.PUBLIC -> ChatView(homeVm, null)
        ScreenState.PRIVATE -> ChatView(homeVm, currentChatUser)
    }
}

@Composable
fun MapScreen(){

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview7() {
    MetInProximityFrontTheme {
        //HomeView({})
    }
}

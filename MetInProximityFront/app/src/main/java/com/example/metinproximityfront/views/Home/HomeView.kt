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
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.metinproximityfront.app.ui.theme.MetInProximityFrontTheme
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.enums.LoadingState
import com.example.metinproximityfront.data.enums.ScreenState
import com.example.metinproximityfront.views.Home.components.BottomNavBar
import com.example.metinproximityfront.views.Home.components.PrivateChatListSheet
import com.example.metinproximityfront.views.Home.components.ProfileDrawerContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    homeVm : HomeViewModel,
    logout : ()-> Unit
) {

    // TODO : will need to create a view model which Holds states
    var currentScreenState by remember { mutableStateOf(ScreenState.MAP) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    var showBottomSheet by remember { mutableStateOf(false) }

    var privUsersLoadState by remember { mutableStateOf(LoadingState.READY) }
    var currentChatUser by remember { mutableStateOf<ChatUser?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    val changeSheetState = {
        if (showBottomSheet == false){
            privUsersLoadState = LoadingState.LOADING
            homeVm.userActionService.getPrivateUsers()
            privUsersLoadState = LoadingState.READY
        }

        showBottomSheet = !showBottomSheet
    }

    val changeScreen : (ScreenState, ChatUser?) -> Unit = { newScreen, newChatUser ->
        var key = Constants.PUBLIC_CHAT_KEY
        if (newChatUser != null){
            key = Constants.PRIVATE_CHAT_KEY("MY USER ID", newChatUser.Id)
            currentChatUser = newChatUser
        }
        showBottomSheet = false
        if (newScreen != ScreenState.MAP){
            homeVm.msgService.retrieveMessages(passedkey = key)
        }
        currentScreenState = newScreen
    }

    PrivateChatListSheet(
        showBottomSheet,
        sheetState,
        changeSheetState,
        changeScreen,
        privUsersLoadState,
        homeVm
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        content = {
            Scaffold (
                bottomBar = { BottomNavBar(
                    changeSheetState,
                    changeScreen
                ) },
                content= {padding -> NestedHomeView(
                    padding,
                    currentScreenState,
                    currentChatUser,
                    drawerState,
                    homeVm
                )}
            )
        },
        drawerContent = { ProfileDrawerContent(logout) }
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MetInProximityFrontTheme {
        // HomeView(HomeViewModel(), {})
    }
}

@Composable
fun TESTHomeView(){

}
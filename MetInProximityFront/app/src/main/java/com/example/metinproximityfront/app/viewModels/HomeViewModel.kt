package com.example.metinproximityfront.app.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.entities.account.User
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.enums.LoadingState
import com.example.metinproximityfront.data.enums.ScreenState
import com.example.metinproximityfront.services.map.MapService
import com.example.metinproximityfront.services.message.MessageService
import com.example.metinproximityfront.services.userAction.UserActionService


data class HomeVmState (
    val currentScreen: ScreenState = ScreenState.MAP,
    val botSheetVisible: Boolean = false,
    val loadingState: LoadingState = LoadingState.READY,
    val currentChatUser: ChatUser? = null,
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed),
)

class HomeViewModel(
    val userActionService : UserActionService,
    val mapService: MapService,
    val msgService: MessageService
) : ViewModel() {
    private val _uiState = mutableStateOf(HomeVmState())
    val uiState: State<HomeVmState> = _uiState

    lateinit var navController : NavHostController

    fun toggleBottomSheet() {
        if (!_uiState.value.botSheetVisible) {
            _uiState.value = _uiState.value.copy(loadingState = LoadingState.LOADING)
            //userActionService.getPrivateUsers()
            _uiState.value = _uiState.value.copy(loadingState = LoadingState.READY)
        }

        _uiState.value = _uiState.value.copy(botSheetVisible = !_uiState.value.botSheetVisible)
    }

    fun changeScreen(newScreen: ScreenState, newChatUser: ChatUser?=null) {
        var key = Constants.PUBLIC_CHAT_KEY
        if (newChatUser != null) {
            key = Constants.PRIVATE_CHAT_KEY("MY USER ID", newChatUser.Id)
            _uiState.value = _uiState.value.copy(currentChatUser = newChatUser)
        }
        _uiState.value = _uiState.value.copy(
            botSheetVisible = false,
            currentScreen = newScreen
        )

        if (newScreen != ScreenState.MAP) {
            msgService.retrieveMessages(passedkey = key)
        }
        navController.navigate(newScreen.toString())
    }

}
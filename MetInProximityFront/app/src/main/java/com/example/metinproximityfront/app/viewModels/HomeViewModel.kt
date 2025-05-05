package com.example.metinproximityfront.app.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import com.example.metinproximityfront.app.state.ChatViewState
import com.example.metinproximityfront.app.state.HomeViewState
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.entities.account.User
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.enums.ScreenState
import com.example.metinproximityfront.services.map.MapService
import com.example.metinproximityfront.services.message.MessageService
import com.example.metinproximityfront.services.userAction.UserActionService
import kotlinx.coroutines.delay


class HomeViewModel(
    val userActionService : UserActionService?,
    val mapService: MapService?,
    val msgService: MessageService?
) : ViewModel() {

    private val _chatState = mutableStateOf(ChatViewState())
    val chatState: State<ChatViewState> = _chatState

    private val _uiState = mutableStateOf(HomeViewState())
    val uiState: State<HomeViewState> = _uiState

    lateinit var homeNavController : NavHostController

    fun changeDrawerState() {

        if (_uiState.value.drawerState.isClosed){
            _uiState.value.drawerState = DrawerState(DrawerValue.Open)
        } else {
            _uiState.value.drawerState = DrawerState(DrawerValue.Closed)
        }
    }

    fun changeVisibility() {
        userActionService?.changeVisibility()
        val afterChange = User.userData.value.openToPrivate
        if (afterChange){
            userActionService?.getPrivateUsers()
        }
    }

    fun changeTextValue(newText : String) {
        _chatState.value = _chatState.value.copy(inputText = newText)
        _chatState.value = _chatState.value.copy(error = false)
    }

    suspend fun onMsgSend() {
        val error: String? = msgService?.sendMessage(
            chatState.value.inputText,
            chatState.value.currentChatUser
        )
        _chatState.value = _chatState.value.copy(inputText = "")
        error?.let { str ->
            _chatState.value = _chatState.value.copy(error = true)
            delay(1000)
        } ?: run {
            _chatState.value = _chatState.value.copy(error = false)
        }
    }

    fun toggleBottomSheet() {
        // if was false, hence sheet is now appearing
        if (!_uiState.value.botSheetVisible) {
            userActionService?.getPrivateUsers(
            )
        }

        _uiState.value = _uiState.value.copy(botSheetVisible = !_uiState.value.botSheetVisible)
    }

    fun changeScreen(
        newScreen: ScreenState,
        newChatUser: ChatUser?=null
    ) {
        var key = Constants.PUBLIC_CHAT_KEY(User.userData.value.userId)

        if (newChatUser != null) {
            key = Constants.PRIVATE_CHAT_KEY(User.userData.value.userId, newChatUser.id)
            _chatState.value = _chatState.value.copy(currentChatUser = newChatUser)
        }else {
            _chatState.value = _chatState.value.copy(currentChatUser = null)
        }
        _uiState.value = _uiState.value.copy(
            botSheetVisible = false,
            currentScreen = newScreen
        )

        if (newScreen != ScreenState.MAP) {
            msgService?.retrieveMessages(passedKey = key)
        }
        homeNavController.navigate(newScreen.toString())
    }
}
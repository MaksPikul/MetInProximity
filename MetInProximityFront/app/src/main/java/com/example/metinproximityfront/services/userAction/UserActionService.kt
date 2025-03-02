package com.example.metinproximityfront.services.userAction

import com.example.metinproximityfront.binders.LocationBinder
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.repositories.UserActionRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserActionService(
    private val userActionRepo: UserActionRepo,
    private val msgLocBinder : LocationBinder
) {
    private var _visibility = MutableStateFlow(false)
    val visibility : StateFlow<Boolean> = _visibility

    val testUser = ChatUser(
        "1234",
        "tester"
    )

    private var _chatUsers = MutableStateFlow<List<ChatUser>>(mutableListOf(testUser))
    val chatUsers : StateFlow<List<ChatUser>> = _chatUsers

    fun changeVisibility() {
        CoroutineScope(Dispatchers.IO).launch {

            val result : String? = userActionRepo.changeVisibilityRepo()

            result?.let { accessToken ->
                changeVisibilityState(accessToken)
            } // ? : Show error
        }
    }

    fun getPrivateUsers() {
        CoroutineScope(Dispatchers.IO).launch {

            val locObj : LocationObject = msgLocBinder.getCurrentLocation()

            val result : List<ChatUser>? = userActionRepo.getPrivateUsersRepo(locObj)

            result?.let { chatUsers ->
                updateChatUsers(chatUsers)
            } // ? : Show that 0 Users showed up
        }
    }

    private fun changeVisibilityState(accessToken : String?){
        // For now
        _visibility.value =  !_visibility.value
    }

    private fun updateChatUsers( newChatUsers : List<ChatUser>) {
        _chatUsers.value = newChatUsers
    }

}
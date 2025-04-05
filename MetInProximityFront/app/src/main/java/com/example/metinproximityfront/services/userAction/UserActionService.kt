package com.example.metinproximityfront.services.userAction

import android.util.Log
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.entities.account.StringRes
import com.example.metinproximityfront.data.entities.account.User
import com.example.metinproximityfront.services.location.LocationServiceBinder
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.enums.UserLoadState
import com.example.metinproximityfront.data.repositories.UserRepo
import com.example.metinproximityfront.services.preference.IStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserActionService(
    private val userActionRepo: UserRepo?,
    private val msgLocBinder : LocationServiceBinder,
    private val encryptedStoreService: IStoreService
) {

    private var _chatUsers = MutableStateFlow<List<ChatUser>>(mutableListOf())
    val chatUsers : StateFlow<List<ChatUser>> = _chatUsers

    private var _loadState = MutableStateFlow(UserLoadState.READY)
    val loadState : StateFlow<UserLoadState> = _loadState

    fun changeVisibility(
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            val result : StringRes? = userActionRepo?.changeVisibilityRepo()

            result?.let { res ->
                encryptedStoreService.saveIntoPref(Constants.ACCESS_TOKEN_KEY, res.message)
                User.update(res.message)
            }
        }
    }

    fun getPrivateUsers(
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            val locObj : LocationObject = msgLocBinder.getCurrentLocation()

            _loadState.value = UserLoadState.LOADING
            val result : List<ChatUser>? = userActionRepo?.getPrivateUsersRepo(locObj)
            _loadState.value = UserLoadState.READY

            result?.let { chatUsers ->
                _chatUsers.value = chatUsers
                Log.i("user count", "Count: " + chatUsers.count())
            }
        }
    }

}
package com.example.metinproximityfront.data.entities.account

import android.util.Log
import com.example.metinproximityfront.data.enums.UserLoadState
import com.example.metinproximityfront.services.token.TokenService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


object User {

    private var _userData = MutableStateFlow(UserData())
    val userData : StateFlow<UserData> = _userData

    fun create(accessToken : String) {

        val claims = TokenService.decodeJWT(accessToken)

        val username = claims.get("unique_name") as String
        val userId = claims.get("nameid") as String
        val email = claims.get("email") as String
        val openToPrivate = claims.get("OpenToPrivate") as String

        Log.i("Current Visibility", openToPrivate)

        _userData.value = UserData(
            userId = userId,
            username = username,
            email = email,
            openToPrivate = openToPrivate == "True"
        )
    }

    // This is just for me to know when user is updating, the logic is the same,
    // Will look weird if userAction Service "updates a user" but im running .Create()
    fun update(accessToken: String) : Boolean? {
        create(accessToken)
        return userData.value?.openToPrivate
    }

    fun delete(){
        _userData.value = UserData()
    }
}

data class UserData(
    var userId : String = "empty",
    var username : String = "empty",
    val openToPrivate: Boolean = false,
    val email: String = "empty"
)

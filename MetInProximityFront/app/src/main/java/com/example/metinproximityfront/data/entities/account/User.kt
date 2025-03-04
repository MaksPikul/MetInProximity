package com.example.metinproximityfront.data.entities.account

import com.example.metinproximityfront.services.token.TokenService


object User {
    var userData: UserData? = null

    fun create(accessToken : String) {

        val claims = TokenService.decodeJWT(accessToken)

        val username = claims["unique_name"] as String
        val email = claims["email"] as String
        val openToPrivate = claims["OpenToPrivate"] as Boolean

        userData = UserData(
            username = username,
            email = email,
            openToPrivate = openToPrivate
        )
    }

    // This is just for me to know when user is updating, the logic is the same,
    // Will look weird if userAction Service "updates a user" but im running .Create()
    fun update(accessToken: String) : Boolean{
        create(accessToken)
        return userData?.openToPrivate ?: false
    }

    fun delete(){
        userData = null
    }
}

data class UserData(
    var username : String,
    val openToPrivate: Boolean,
    val email: String
)

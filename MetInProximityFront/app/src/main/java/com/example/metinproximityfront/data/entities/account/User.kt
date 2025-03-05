package com.example.metinproximityfront.data.entities.account

import com.example.metinproximityfront.services.token.TokenService


object User {
    var userData: UserData? = null

    fun create(accessToken : String) {

        val claims = TokenService.decodeJWT(accessToken)
        val username = claims.get("unique_name") as String
        val email = claims.get("email") as String
        val openToPrivate = claims.get("OpenToPrivate") as String



        userData = UserData(
            username = username,
            email = email,
            openToPrivate = if (openToPrivate == "true") true else false
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

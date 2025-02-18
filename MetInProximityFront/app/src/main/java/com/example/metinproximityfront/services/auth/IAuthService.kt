package com.example.metinproximityfront.services.auth

import android.content.Intent
import android.service.autofill.UserData
import com.example.metinproximityfront.config.oauth.OAuthConfig
import com.example.metinproximityfront.data.entities.account.User

interface IAuthService{
    var curProvider : String?

    fun StartLogin(
        provider: OAuthConfig,
        launchAction: (i: Intent) -> Unit
    )

    fun FinishLogin(
        responseIntent: Intent,
        fcmToken : String,
        onSuccessfulLogin : ()-> Unit,
        onFailedLogin : (errorMsg : String?, errorCode : String?)-> Unit
    )

    fun Logout(
        successRedirect: () -> Unit
    )

    fun IsLoggedIn (): Boolean

    //fun GetUser(): User


}
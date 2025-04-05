package com.example.metinproximityfront.services.auth

import android.content.Intent
import com.example.metinproximityfront.config.oauth.OAuthConfig

interface IAuthService{
    var curProvider : String?

    fun StartLogin(
        provider: OAuthConfig,
        launchAction: (i: Intent) -> Unit
    )

    fun FinishLogin(
        responseIntent: Intent,
        fcmToken: String,
        onSuccessfulLogin: ()-> Unit,
        onFailedLogin: (errorMsg : String?, errorCode : String?) -> Unit
    )

    fun Logout(
        successRedirect: () -> Unit
    )

    fun IsLoggedIn (): Boolean

    //fun GetUser(): User


}
package com.example.metinproximityfront.services.auth

import android.content.Intent
import android.service.autofill.UserData
import com.example.metinproximityfront.config.oauth.OAuthConfig

interface IAuthService{
    fun StartLogin(
        provider: OAuthConfig,
        launchAction: (i: Intent) -> Unit
    )

    fun FinishLogin(
        responseIntent: Intent,
        onSuccessfulLogin : ()-> Unit,
        fcmToken : String
    )

    fun Logout(
        successRedirect: () -> Unit
    )

    suspend fun RefreshAndReturnToken (

    ) : String

    fun IsLoggedIn (): Boolean


}
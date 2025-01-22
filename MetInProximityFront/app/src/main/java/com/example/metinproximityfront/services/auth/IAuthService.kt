package com.example.metinproximityfront.services.auth

import android.content.Intent
import com.example.metinproximityfront.config.OAuth.OAuthConfig

interface IAuthService{
    fun StartLogin(
        provider: OAuthConfig,
        launchAction: (i: Intent) -> Unit
    )

    fun FinishLogin(
        responseIntent: Intent,
        successRedirect : ()-> Unit
    )

    fun Logout(
        successRedirect: () -> Unit
    )

    fun IsLoggedIn (): Boolean
}
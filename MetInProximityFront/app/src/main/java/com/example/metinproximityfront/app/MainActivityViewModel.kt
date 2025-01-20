package com.example.metinproximityfront.app

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.example.metinproximityfront.config.OAuth.GoogleOAuthConfig
import com.example.metinproximityfront.config.OAuth.OAuthConfig
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.auth.IAuthService
import com.example.metinproximityfront.services.preference.IPrefStoreService
import com.example.metinproximityfront.services.preference.PrefStoreService

class MainActivityViewModel(private val app: Application) : AndroidViewModel(app) {

    val authenticator : IAuthService
    val prefStore : IPrefStoreService
    val OAuthProviders : MutableList<OAuthConfig>

    init{
        this.authenticator = AuthService(this.app.applicationContext)

        this.prefStore = PrefStoreService()

        this.OAuthProviders = InitOAuthProviders()
    }


    fun StartLogin(
        provider:OAuthConfig,
        launchAction: (i: Intent) -> Unit,
    ){
        this.authenticator.StartLogin(provider, launchAction)
    }

    fun FinishLogin(responseIntent: Intent){
        this.authenticator.FinishLogin(responseIntent)

        // TODO: save auth code into shared preferences

        // TODO: redirect
    }





    private fun InitOAuthProviders() : MutableList<OAuthConfig>{
        val list = mutableListOf<OAuthConfig>()
        //Add more to extend
        list.add(
            GoogleOAuthConfig()
        )

        return list
    }

    fun GetOAuthProviders (): List<OAuthConfig>{
        return this.OAuthProviders
    }







}

package com.example.metinproximityfront.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.metinproximityfront.data.repositories.AccountRepository
import com.example.metinproximityfront.data.remote.HttpClient
import com.example.metinproximityfront.config.OAuth.GoogleOAuthConfig
import com.example.metinproximityfront.config.OAuth.OAuthConfig
import com.example.metinproximityfront.data.api.AccountApi
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.HttpClient.retrofit
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.auth.IAuthService
import com.example.metinproximityfront.services.preference.IPrefStoreService
import com.example.metinproximityfront.services.preference.PrefStoreService

class MainActivityViewModel(
    private val app: Application
) : AndroidViewModel(app) {


    val authService : IAuthService
    val prefStore : IPrefStoreService

    val OAuthProviders : MutableList<OAuthConfig>

    val accountRepo: AccountRepository

    init{

        this.prefStore = PrefStoreService(this.app.applicationContext)

        // TODo Create seperate function which returns void but creates repo objects
        this.accountRepo = AccountRepository()


        this.authService = AuthService(
            this.app.applicationContext,
            this.prefStore,
            this.accountRepo
        )


        this.OAuthProviders = InitOAuthProviders()

    }

    fun GetOAuthProviders (): List<OAuthConfig>{
        return this.OAuthProviders
    }

    private fun InitOAuthProviders() : MutableList<OAuthConfig>{
        val list = mutableListOf<OAuthConfig>()
        //Add more to extend
        list.add(
            GoogleOAuthConfig()
        )

        return list
    }

}

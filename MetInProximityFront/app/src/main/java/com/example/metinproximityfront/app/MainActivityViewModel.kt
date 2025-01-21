package com.example.metinproximityfront.app

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.metinproximityfront.api.AccountRepo
import com.example.metinproximityfront.clients.HttpClient
import com.example.metinproximityfront.config.OAuth.GoogleOAuthConfig
import com.example.metinproximityfront.config.OAuth.OAuthConfig
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.auth.IAuthService
import com.example.metinproximityfront.services.preference.IPrefStoreService
import com.example.metinproximityfront.services.preference.PrefStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MainActivityViewModel(
    private val app: Application
) : AndroidViewModel(app) {


    val authService : IAuthService
    val prefStore : IPrefStoreService

    val OAuthProviders : MutableList<OAuthConfig>

    private val accountRepo: AccountRepo

    init{

        this.prefStore = PrefStoreService(this.app.applicationContext)

        this.accountRepo = AccountRepo(HttpClient.apiClient)

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

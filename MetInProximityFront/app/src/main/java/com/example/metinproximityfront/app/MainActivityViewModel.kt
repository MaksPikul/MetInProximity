package com.example.metinproximityfront.app

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.ApiTokenWrapper
import com.example.metinproximityfront.data.repositories.AccountRepository
import com.example.metinproximityfront.data.repositories.LocationRepo
import com.example.metinproximityfront.factories.OAuthProviderFactory
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.auth.IAuthService
import com.example.metinproximityfront.services.locaction.LocationService
import com.example.metinproximityfront.services.message.MsgStoreListener
import com.example.metinproximityfront.services.message.MsgStoreService
import com.example.metinproximityfront.services.message.SignalRMsgReceiver
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import com.example.metinproximityfront.services.preference.IStoreService
import com.example.metinproximityfront.services.preference.SharedStoreService
import com.example.metinproximityfront.views.Home.HomeViewModel

class MainActivityViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    // Todo : ADD state variables and create two initialisation stages
    // TODO : Either split up the view model, or two functions which initialise at different times

    val accountRepo: AccountRepository

    private val encryptedStoreService : IStoreService

    val authService : IAuthService
    val oAuthProviderFactory : OAuthProviderFactory

    lateinit var homeVm : HomeViewModel

    // These necessary for login page,
    // Once user logs in, other necessary objects are initialised
    init{
        // Dependency injections handled here
        this.accountRepo = AccountRepository()

        this.encryptedStoreService = EncryptedStoreService(this.app.applicationContext)

        this.authService = AuthService(
            this.app.applicationContext,
            this.encryptedStoreService,
            this.accountRepo
        )

        this.oAuthProviderFactory = OAuthProviderFactory()
    }

    fun InitHomeViewModel(){
        this.homeVm = HomeViewModel(app, encryptedStoreService)
    }

}

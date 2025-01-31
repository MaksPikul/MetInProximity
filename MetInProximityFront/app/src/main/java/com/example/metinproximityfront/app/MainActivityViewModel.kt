package com.example.metinproximityfront.app

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.example.metinproximityfront.data.repositories.AccountRepository
import com.example.metinproximityfront.factories.OAuthProviderFactory
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.auth.IAuthService
import com.example.metinproximityfront.services.loc.LocationService
import com.example.metinproximityfront.services.preference.IStoreService
import com.example.metinproximityfront.services.preference.EncryptedStoreService

class MainActivityViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    // Todo : State Variables
    val accountRepo: AccountRepository

    val storeService : IStoreService
    val authService : IAuthService
    val locationService : LocationService

    val oAuthProviderFactory : OAuthProviderFactory

    init{
        // TODo Create seperate function which returns void but creates repo objects
        this.accountRepo = AccountRepository()

        this.storeService = EncryptedStoreService(this.app.applicationContext)
        this.authService = AuthService(
            this.app.applicationContext,
            this.storeService,
            this.accountRepo
        )
        this.locationService = LocationService();

        this.oAuthProviderFactory = OAuthProviderFactory()
    }


    fun startLocationGathering() {
        val intent = Intent(app, LocationService::class.java);
        app.startForegroundService(intent)
    }

    fun stopLocationGathering() {

    }

}

package com.example.metinproximityfront.app

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.example.metinproximityfront.data.api.ApiTokenWrapper
import com.example.metinproximityfront.data.repositories.AccountRepository
import com.example.metinproximityfront.data.repositories.LocationRepo
import com.example.metinproximityfront.factories.OAuthProviderFactory
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.auth.IAuthService
import com.example.metinproximityfront.services.loc.LocationService
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import com.example.metinproximityfront.services.preference.IStoreService

class MainActivityViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    // Todo : State Variables
    val accountRepo: AccountRepository
    val locationRepo : LocationRepo

    val storeService : IStoreService
    val authService : IAuthService
    val locationService : LocationService

    val oAuthProviderFactory : OAuthProviderFactory



    init{
        // Dependency injections handled here
        this.accountRepo = AccountRepository()

        this.storeService = EncryptedStoreService(this.app.applicationContext)
        this.authService = AuthService(
            this.app.applicationContext,
            this.storeService,
            this.accountRepo
        )

        this.locationRepo = LocationRepo(
            ApiTokenWrapper(authService, storeService)
        )
        this.locationService = LocationService(
            locationRepo
        );

        this.oAuthProviderFactory = OAuthProviderFactory()
    }

    fun startLocationService() {
        val intent = Intent(app, LocationService::class.java);
        app.startForegroundService(intent)

    }

    fun stopLocationService() {
        val serviceIntent = Intent(app, LocationService::class.java)
        serviceIntent.setAction( "STOP_SERVICE" )
        app.stopService(serviceIntent)
    }

}

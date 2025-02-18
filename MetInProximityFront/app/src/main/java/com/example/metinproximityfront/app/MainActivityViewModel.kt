package com.example.metinproximityfront.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.example.metinproximityfront.data.repositories.AccountRepository
import com.example.metinproximityfront.factories.OAuthProviderFactory
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.auth.IAuthService
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import com.example.metinproximityfront.services.preference.IStoreService
import com.example.metinproximityfront.views.Home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel(
    private val app: Application
) : AndroidViewModel(app) {
    // Todo : ADD state variables and create two initialisation stages

    lateinit var navHostController: NavHostController

    private val encryptedStoreService : IStoreService

    val accountRepo: AccountRepository
    val authService : IAuthService
    val oAuthProviderFactory : OAuthProviderFactory

    lateinit var homeVm : HomeViewModel

    // https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

    fun startLoadingView(){
        _isLoading.value = true
        navHostController.navigate("Loading")
    }

    fun stopLoadingView(nextScreen : String){
        _isLoading.value = false
        navHostController.navigate(nextScreen)
    }

}

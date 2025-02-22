package com.example.metinproximityfront.app

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.example.metinproximityfront.data.repositories.AccountRepository
import com.example.metinproximityfront.factories.OAuthProviderFactory
import com.example.metinproximityfront.services.permissions.PermissionManager
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

    // Utility Classes
    lateinit var navHostController: NavHostController
    private val encryptedStoreService : IStoreService
    var permissionManager: PermissionManager

    val accountRepo: AccountRepository
    val authService : IAuthService
    val oAuthProviderFactory : OAuthProviderFactory

    lateinit var homeVm : HomeViewModel

    // https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow() // adding this just in case i need to use it as a state

    // These necessary for login page,
    // Once user logs in, other necessary objects are initialised
    init{
        // Dependency injections handled here
        this.permissionManager = PermissionManager()
        this.accountRepo = AccountRepository()

        this.encryptedStoreService = EncryptedStoreService(this.app.applicationContext)

        this.authService = AuthService(
            this.app.applicationContext,
            this.encryptedStoreService,
            this.accountRepo
        )

        this.oAuthProviderFactory = OAuthProviderFactory()
    }

    fun InitAndLoadHomeVm(){
        this.homeVm = HomeViewModel(app, encryptedStoreService, navHostController)
        this.stopLoadingView("Home")

        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.route == "Login") {
                homeVm.stopServices()
            }
        }

        this.homeVm.startServices()
    }

    fun startLoadingView(){

        _isLoading.value = true
        navHostController.navigate("Loading")
    }

    fun stopLoadingView(nextScreen : String){

        _isLoading.value = false
        navHostController.navigate(nextScreen)
    }

    val onSuccLogin = {
        InitAndLoadHomeVm()
        authService.curProvider = null
    }

    val onFailLogin = {errorMsg : String?, errorCode : String? ->
        stopLoadingView("Login")
        Toast.makeText(app.applicationContext, errorMsg, Toast.LENGTH_LONG).show()
        authService.curProvider = null
        Log.e("Auth Error",errorMsg.toString())
        Unit
    }

}

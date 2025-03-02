package com.example.metinproximityfront.app.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.metinproximityfront.data.repositories.AccountRepository
import com.example.metinproximityfront.services.permissions.PermissionManager
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.auth.IAuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(
    private val app: Application,
    private val mainVm : MainViewModel
) : ViewModel() {
    // Todo : ADD state variables and create two initialisation stages

    // Utility Classes
    var permissionManager: PermissionManager

    val accountRepo: AccountRepository
    val authService : IAuthService

    // https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow() // adding this just in case i need to use it as a state

    // These necessary for login page,
    // Once user logs in, other necessary objects are initialised
    init{
        // Dependency injections handled here
        this.permissionManager = PermissionManager()
        this.accountRepo = AccountRepository()

        this.authService = AuthService(
            this.app.applicationContext,
            mainVm.encryptedStoreService,
            this.accountRepo
        )

    }

    fun startLoadingView(){

        _isLoading.value = true
        mainVm.navController.navigate("Loading")
    }

    fun stopLoadingView(nextScreen : String){

        _isLoading.value = false
        mainVm.navController.navigate(nextScreen)
    }

    val onSuccLogin = {
        this.stopLoadingView("Home")
        mainVm.startServices()
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

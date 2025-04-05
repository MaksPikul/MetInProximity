package com.example.metinproximityfront.app.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.entities.account.User
import com.example.metinproximityfront.data.repositories.AccountRepository
import com.example.metinproximityfront.services.permissions.PermissionManager
import com.example.metinproximityfront.services.auth.AuthService
import com.example.metinproximityfront.services.auth.IAuthService

class AuthViewModel(
    private val app: Application,
    private val mainVm : MainViewModel
) : ViewModel() {
    // Todo : ADD state variables and create two initialisation stages

    // Utility Classes
    var permissionManager: PermissionManager

    val accountRepo: AccountRepository
    val authService : IAuthService

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

    fun CheckLoginStatus() {
        if (authService.IsLoggedIn()){
            onSuccLogin()
        }
        else {
            onFailLogin("Session Ended", "401")
        }
    }

    val onSuccLogin = {
        this.stopLoadingView("Home")
        mainVm.startServices()
        User.create(mainVm.encryptedStoreService.getFromPref(Constants.ACCESS_TOKEN_KEY).toString())
        authService.curProvider = null
    }

    val onFailLogin = {errorMsg : String?, errorCode : String? ->
        stopLoadingView("Login")
        Toast.makeText(app.applicationContext, errorMsg, Toast.LENGTH_LONG).show()
        authService.curProvider = null
        Log.e("Auth Error",errorMsg.toString())
        Unit
    }

    fun startLoadingView(){
        mainVm.navController.navigate("Loading")
    }

    fun stopLoadingView(nextScreen : String){
        mainVm.navController.navigate(nextScreen)
    }
}

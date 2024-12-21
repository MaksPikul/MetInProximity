package com.example.metinproximityfront.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.metinproximityfront.Repositories.AuthRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    // Method to save JWT
    fun saveJwt(jwt: String) {
        authRepository.saveJwt(jwt)
    }

    // Method to retrieve JWT
    fun getJwt(): String? {
        return authRepository.getJwt()
    }
}

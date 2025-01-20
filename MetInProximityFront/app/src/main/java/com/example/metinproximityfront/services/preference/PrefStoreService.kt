package com.example.metinproximityfront.services.preference

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class PrefStoreService : IPrefStoreService {


    private fun saveIntoSharedPref(value: String) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Initialize/open an instance of EncryptedSharedPreferences on below line.
        val sharedPreferences = EncryptedSharedPreferences.create(
            // passing a file name to share a preferences
            "preferences",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        // on below line we are storing data in shared preferences file.
        sharedPreferences.edit().putString("auth_token", value).apply()
    }
}
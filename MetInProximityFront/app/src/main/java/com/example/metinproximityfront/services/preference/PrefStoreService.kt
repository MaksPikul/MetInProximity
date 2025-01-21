package com.example.metinproximityfront.services.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class PrefStoreService(
    appContext: Context
): IPrefStoreService {

    private var sharedPreferences: SharedPreferences

    init {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        this.sharedPreferences = EncryptedSharedPreferences.create(
            // passing a file name to share a preferences
            "preferences",
            masterKeyAlias,
            appContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    override fun saveIntoPref(key: String, value: String) {
        // on below line we are storing data in shared preferences file.
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getFromPref (key: String) : String? {
        return sharedPreferences.getString(key, "");
    }
}
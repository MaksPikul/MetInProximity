package com.example.metinproximityfront.services.preference

import android.content.Context
import android.content.SharedPreferences

class SharedStoreService(
    appContext: Context
) : IStoreService {

    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences("messages", Context.MODE_PRIVATE)

    override fun saveIntoPref(key: String, value: String) {
        sharedPreferences.edit().apply {
            putString(key, value)
            apply() // Asynchronous save
        }
    }

    override fun getFromPref(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun removeFromPref(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}
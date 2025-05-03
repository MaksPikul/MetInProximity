package com.example.metinproximityfront.services.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


class SharedStoreService(
    appContext: Context,
    fileName : String
) : IStoreService {

    val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE)

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
        //sharedPreferences.edit().remove(key).apply()
        val editor = sharedPreferences.edit()
        editor.clear() // This clears all the data
        editor.apply()
    }
}
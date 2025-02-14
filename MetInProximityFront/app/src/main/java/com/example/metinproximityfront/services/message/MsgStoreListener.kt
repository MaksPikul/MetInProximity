package com.example.metinproximityfront.services.message

import android.content.SharedPreferences
import com.example.metinproximityfront.services.preference.SharedStoreService


/*
https://medium.com/@jurajkunier/android-shared-preferences-listener-implemented-by-rxjava-and-livedata-cfac02683eac
 */
class MsgStoreListener(
    private val msgStoreService: SharedStoreService
) {

    val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            val newValue = msgStoreService.sharedPreferences.all[key]
            //onPreferenceChanged(key, newValue)
        }

    fun startListening() {
        msgStoreService.
        sharedPreferences.
        registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    fun stopListening() {
        msgStoreService.
        sharedPreferences.
        unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }


}
package com.example.metinproximityfront.services.preference

interface IPrefStoreService {
    fun saveIntoPref(key: String, value: String)

    fun getFromPref(key: String) : String?

    fun removeFromPref(key: String)
}
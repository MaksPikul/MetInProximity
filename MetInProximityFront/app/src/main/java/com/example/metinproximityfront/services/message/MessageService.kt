package com.example.metinproximityfront.services.message

import android.content.SharedPreferences
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.entities.message.MsgReqObject
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.data.repositories.MessageRepository
import com.example.metinproximityfront.services.locaction.LocationService
import com.example.metinproximityfront.services.preference.SharedStoreService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageService(
    private val sharedStore: SharedStoreService,
    private val msgRepo: MessageRepository? = null,
    private val locationService: LocationService
)
{

    private val _messages = MutableStateFlow<List<MsgResObject>>(emptyList()) // StateFlow for UI
    val messages: StateFlow<List<MsgResObject>> = _messages // Expose immutable StateFlow

    init {
        startListening()
        retrieveMessages()
    }

    fun storeMessage(msg : MsgResObject) : String{

        var key = "public"/*-${msg.UserId}"*/
        if (!msg.isPublic){
            key = "private-${msg.UserId}-${msg.RecipientId}"
        }

        val json = Gson().toJson(_messages.value + msg)
        sharedStore.saveIntoPref(key, json)

        return key
    }

    fun retrieveMessages() {
        val json = sharedStore.getFromPref("public")
        val type = object : TypeToken<List<MsgResObject>>() {}.type
        val messagesList: List<MsgResObject> = Gson().fromJson(json, type) ?: emptyList()

        _messages.value = messagesList
    }

    fun sendMessage(
        textToSend : String,
        //TODO : isPublic : Boolean - Currently only for public
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val locObj : LocationObject = locationService.GetCurrentLocation()

            val msgObj = MsgReqObject(
                Body = textToSend,
                Longitude = locObj.Longitude,
                Latitude = locObj.Latitude
            )

            val response : MsgResObject? =  msgRepo?.SendMessage(msgObj)
            response?.let { msg ->
                storeMessage(msg)
            }
            retrieveMessages() // this changes UI?
        }
    }

    val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "public") {
                retrieveMessages()
            }
        }

    fun startListening() {
        sharedStore.
        sharedPreferences.
        registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    fun stopListening() {
        sharedStore.
        sharedPreferences.
        unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

}
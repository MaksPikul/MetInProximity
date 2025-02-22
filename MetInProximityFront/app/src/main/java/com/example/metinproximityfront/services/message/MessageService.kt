package com.example.metinproximityfront.services.message

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import com.example.metinproximityfront.binders.MessageLocationBinder
import com.example.metinproximityfront.data.entities.location.LocationObject
import com.example.metinproximityfront.data.entities.message.MsgReqObject
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.data.repositories.MessageRepository
import com.example.metinproximityfront.services.location.LocationService
import com.example.metinproximityfront.services.preference.SharedStoreService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class MessageService(
    private val sharedStore: SharedStoreService,
    private val msgRepo: MessageRepository? = null,
    private val msgLocBinder : MessageLocationBinder,
)
{

    private val _messages = MutableStateFlow<List<MsgResObject>>(emptyList()) // StateFlow for UI
    val messages: StateFlow<List<MsgResObject>> = _messages // Expose immutable StateFlow

    init {
        //startListening()
        //retrieveMessages()
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
        try {
            CoroutineScope(Dispatchers.IO).launch {
                //val locObj: LocationObject = locationService.GetCurrentLocation()
                // that into this
                Log.e("msg","in msgSer")
                val locObj : LocationObject = msgLocBinder.getCurrentLocation()

                val msgObj = MsgReqObject(
                    Body = textToSend,
                    Longitude = locObj.Longitude,
                    Latitude = locObj.Latitude
                )
                Log.e("long", locObj.Longitude.toString())

                val response: MsgResObject? = msgRepo?.SendMessage(msgObj)

                /*
                val response = MsgResObject(
                    Body= textToSend,
                    UserId = "2",
                    true,
                    "3",
                    Date()
                )
                 */

                Log.e("server respo", response.toString())
                response?.let { msg ->
                    storeMessage(msg)
                }
                retrieveMessages() // this changes UI?
            }
        }
        catch (ex : Throwable) {
            Log.e("location error", ex.message.toString())
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
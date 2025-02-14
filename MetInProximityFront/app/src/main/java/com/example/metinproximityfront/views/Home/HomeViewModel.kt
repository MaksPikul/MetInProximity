package com.example.metinproximityfront.views.Home

import android.app.Application
import android.content.Intent
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.ApiTokenWrapper
import com.example.metinproximityfront.data.repositories.LocationRepo
import com.example.metinproximityfront.services.locaction.LocationService
import com.example.metinproximityfront.services.message.MsgStoreListener
import com.example.metinproximityfront.services.message.MsgStoreService
import com.example.metinproximityfront.services.message.SignalRMsgReceiver
import com.example.metinproximityfront.services.preference.IStoreService
import com.example.metinproximityfront.services.preference.SharedStoreService

class HomeViewModel(
    private val app : Application,
    private val encryptedStoreService : IStoreService
){

    val msgStoreService : IStoreService

    val signalRMsgReceiver : SignalRMsgReceiver
    val msgStoreListener : MsgStoreListener

    val locationRepo : LocationRepo
    val locationService : LocationService

    init {
        this.msgStoreService = SharedStoreService(
            this.app.applicationContext,
            Constants.MsgSharedStoreServiceFileName
        )

        this.signalRMsgReceiver = SignalRMsgReceiver(
            MsgStoreService(this.msgStoreService)
        )
        this.msgStoreListener = MsgStoreListener(this.msgStoreService)

        this.locationRepo = LocationRepo(
            ApiTokenWrapper(encryptedStoreService)
        )
        this.locationService = LocationService(
            locationRepo
        )
    }

    /*
        Sending Location to Backend
        Listening for Public and Private Messages
        Listening for changes to message shared pref file (to update UI)
     */

    fun startServices() {
        val intent = Intent(app, LocationService::class.java);
        app.startForegroundService(intent)

        this.signalRMsgReceiver.startConnection()

        this.msgStoreListener.startListening()
    }

    fun stopLocationService() {
        val serviceIntent = Intent(app, LocationService::class.java)
        serviceIntent.setAction( "STOP_SERVICE" )
        app.stopService(serviceIntent)
    }


}
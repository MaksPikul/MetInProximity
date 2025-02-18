package com.example.metinproximityfront.views.Home

import android.app.Application
import android.content.Intent
import android.util.Log
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.repositories.LocationRepo
import com.example.metinproximityfront.data.repositories.MessageRepository
import com.example.metinproximityfront.services.location.LocationService
import com.example.metinproximityfront.services.message.MessageService
import com.example.metinproximityfront.services.message.SignalRMsgReceiver
import com.example.metinproximityfront.services.preference.IStoreService
import com.example.metinproximityfront.services.preference.SharedStoreService

class HomeViewModel(
    private val app : Application,
    private val encryptedStoreService : IStoreService
){

    private val storeService : IStoreService

    private val signalRMsgReceiver : SignalRMsgReceiver

    private val msgRepo : MessageRepository
    val msgService : MessageService

    private val locationRepo : LocationRepo
    // val locationService : LocationService

    /*
    private val userActionRepo : UserActionRepo
    val userActionService : UserActionService
     */

    init {
        this.storeService = SharedStoreService(
            this.app.applicationContext,
            Constants.MsgSharedStoreServiceFileName
        )

        this.locationRepo = LocationRepo(
            ApiTokenWrapper(encryptedStoreService)
        )

        this.msgRepo = MessageRepository(
            ApiTokenWrapper(encryptedStoreService)
        )
        this.msgService = MessageService(
            this.storeService,
            this.msgRepo,
            //this.locationService
        )

        this.signalRMsgReceiver = SignalRMsgReceiver(
            this.msgService
        )

        /*
        this.userActionRepo = UserActionRepo()
        this.userActionService = UserActionService()
         */
    }

    /*
        Sending Location to Backend
        Listening for Public and Private Messages
        Listening for changes to message shared pref file (to update UI)
     */

    fun startServices() {
        Log.e("Starting Services", "starting")

        // Starts Location Service
        Intent(app.applicationContext, LocationService::class.java).apply {
            action = Constants.START_LOC_SERVICE
            app.startService(this)
        }

        // this.signalRMsgReceiver.startConnection()
        // this.msgStoreListener.startListening()
    }

    fun stopServices() {
        val serviceIntent = Intent(app.applicationContext, LocationService::class.java)
        serviceIntent.setAction( "STOP_SERVICE" )
        app.applicationContext.stopService(serviceIntent)
    }


}
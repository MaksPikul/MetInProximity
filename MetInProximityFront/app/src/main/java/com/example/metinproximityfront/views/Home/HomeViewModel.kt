package com.example.metinproximityfront.views.Home

import android.app.Application
import android.content.Intent
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.ApiTokenWrapper
import com.example.metinproximityfront.data.repositories.LocationRepo
import com.example.metinproximityfront.data.repositories.MessageRepository
import com.example.metinproximityfront.data.repositories.UserActionRepo
import com.example.metinproximityfront.data.repositories.UserActionRepository
import com.example.metinproximityfront.services.locaction.LocationService
import com.example.metinproximityfront.services.message.MessageService
import com.example.metinproximityfront.services.message.SignalRMsgReceiver
import com.example.metinproximityfront.services.preference.IStoreService
import com.example.metinproximityfront.services.preference.SharedStoreService
import com.example.metinproximityfront.services.userAction.UserActionService

class HomeViewModel(
    private val app : Application,
    private val encryptedStoreService : IStoreService
){

    private val storeService : IStoreService

    private val signalRMsgReceiver : SignalRMsgReceiver

    private val msgRepo : MessageRepository
    val msgService : MessageService

    private val locationRepo : LocationRepo
    val locationService : LocationService

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
        this.locationService = LocationService(
            this.app.applicationContext,
            locationRepo
        )

        this.msgRepo = MessageRepository(
            ApiTokenWrapper(encryptedStoreService)
        )
        this.msgService = MessageService(
            this.storeService,
            this.msgRepo,
            this.locationService
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

    fun startServices(

    ) {
        val intent = Intent(app, LocationService::class.java);
        app.startForegroundService(intent)

        this.signalRMsgReceiver.startConnection()
        // this.msgStoreListener.startListening()
    }

    fun stopLocationService() {
        val serviceIntent = Intent(app, LocationService::class.java)
        serviceIntent.setAction( "STOP_SERVICE" )
        app.stopService(serviceIntent)
    }


}
package com.example.metinproximityfront.views.Home

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.navigation.NavController
import com.example.metinproximityfront.binders.MessageLocationBinder
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
    private val encryptedStoreService : IStoreService,
    private val navController: NavController
){

    private val storeService : IStoreService


    private val signalRMsgReceiver : SignalRMsgReceiver

    private val msgRepo : MessageRepository
    val msgService : MessageService

    /*
    private val userActionRepo : UserActionRepo
    val userActionService : UserActionService
     */

    init {
        this.storeService = SharedStoreService(
            this.app.applicationContext,
            Constants.MsgSharedStoreServiceFileName
        )

        this.msgRepo = MessageRepository(
            ApiTokenWrapper(encryptedStoreService),
            navController
        )

        val msgLocBinder = MessageLocationBinder(app.applicationContext)
        msgLocBinder.bindLocationService()

        this.msgService = MessageService(
            this.storeService,
            this.msgRepo,
            msgLocBinder,
        )

        this.signalRMsgReceiver = SignalRMsgReceiver(
            this.msgService,
            encryptedStoreService
        )

        /*
        this.userActionRepo = UserActionRepo()
        this.userActionService = UserActionService()
         */
    }

    fun startServices() {
        Log.e("Starting Services", "starting")

        // Starts Location Service
        Intent(app.applicationContext, LocationService::class.java).apply {
            action = Constants.START_LOC_SERVICE
            app.startService(this)
        }

        this.signalRMsgReceiver.startConnection()
        // this.msgStoreListener.startListening()
    }

    fun stopServices() {
        val serviceIntent = Intent(app.applicationContext, LocationService::class.java)
        serviceIntent.setAction( "STOP_SERVICE" )
        app.applicationContext.stopService(serviceIntent)
        this.signalRMsgReceiver.stopConnection()
    }

}
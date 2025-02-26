package com.example.metinproximityfront.views.Home

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.example.metinproximityfront.binders.MessageLocationBinder
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.repositories.MessageRepository
import com.example.metinproximityfront.data.repositories.UserActionRepo
import com.example.metinproximityfront.services.location.LocationService
import com.example.metinproximityfront.services.message.MessageService
import com.example.metinproximityfront.services.message.SignalRMsgReceiver
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import com.example.metinproximityfront.services.preference.IStoreService
import com.example.metinproximityfront.services.preference.SharedStoreService
import com.example.metinproximityfront.services.userAction.UserActionService

class MainViewModel(
    private val app : Application,
) : AndroidViewModel(app) {

    val encryptedStoreService : IStoreService = EncryptedStoreService(this.app.applicationContext)
    private val storeService : IStoreService = SharedStoreService(
        this.app.applicationContext,
        Constants.MsgSharedStoreServiceFileName
    )

    private val msgLocBinder : MessageLocationBinder = MessageLocationBinder(app.applicationContext)

    var navController : NavHostController = NavHostController(app.applicationContext)

    private val msgRepo : MessageRepository  = MessageRepository(
        ApiTokenWrapper(encryptedStoreService),
        navController
    )

    val msgService : MessageService = MessageService(
        this.storeService,
        this.msgRepo,
        msgLocBinder,
    )

    private val signalRMsgReceiver : SignalRMsgReceiver = SignalRMsgReceiver(
        this.msgService,
        encryptedStoreService
    )

    private val userActionRepo : UserActionRepo = UserActionRepo(
        ApiTokenWrapper(encryptedStoreService),
        navController
    )
    val userActionService : UserActionService = UserActionService(
        this.userActionRepo,
        msgLocBinder,
    )

    init {
        this.msgLocBinder.bindLocationService()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.route == "Login") {
                stopServices()
            }
        }
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
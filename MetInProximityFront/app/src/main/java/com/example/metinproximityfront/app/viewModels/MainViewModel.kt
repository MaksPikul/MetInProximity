package com.example.metinproximityfront.app.viewModels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.example.metinproximityfront.services.location.LocationServiceBinder
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.repositories.MessageRepository
import com.example.metinproximityfront.data.repositories.UserRepo
import com.example.metinproximityfront.services.location.LocationService
import com.example.metinproximityfront.services.map.MapService
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

    private val locBinder : LocationServiceBinder = LocationServiceBinder(app.applicationContext)

    var navController : NavHostController = NavHostController(app.applicationContext)

    private val msgRepo : MessageRepository  = MessageRepository(
        ApiTokenWrapper(encryptedStoreService),
        navController
    )

    val msgService : MessageService = MessageService(
        this.storeService,
        this.msgRepo,
        locBinder,
    )

    private val signalRMsgReceiver : SignalRMsgReceiver = SignalRMsgReceiver(
        this.msgService,
        encryptedStoreService
    )

    private val userActionRepo : UserRepo = UserRepo(
        ApiTokenWrapper(encryptedStoreService),
        navController
    )
    val userActionService : UserActionService = UserActionService(
        this.userActionRepo,
        locBinder,
    )

    val mapService : MapService = MapService()

    init {
        this.locBinder.bindLocationService()

        this.locBinder.registerObserver(mapService)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.route == "Login") {
                stopServices()
            }
        }
    }

    fun startServices() {
        Log.e("Starting Services", "starting")

        // Location Service
        Intent(app.applicationContext, LocationService::class.java).apply {
            action = Constants.START_LOC_SERVICE
            app.startService(this)
        }

        // SignalR
        this.signalRMsgReceiver.startConnection()

        // Firebase

    }

    fun stopServices() {
        // Location Service
        Intent(app.applicationContext, LocationService::class.java).apply {
            action = Constants.STOP_LOC_SERVICE
            app.startService(this)
        }

        // SignalR
        this.signalRMsgReceiver.stopConnection()

        // Firebase

    }
}
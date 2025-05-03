package com.example.metinproximityfront.app.viewModels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.example.metinproximityfront.services.location.LocationServiceBinder
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.RefreshTokenApi
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
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
    val storeService : IStoreService = SharedStoreService(
        this.app.applicationContext,
        Constants.MsgSharedStoreServiceFileName
    )

    private val locBinder : LocationServiceBinder = LocationServiceBinder(app.applicationContext)

    var navController : NavHostController = NavHostController(app.applicationContext)

    private val refreshTokenApi: RefreshTokenApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    private var msgRepo = MessageRepository(
        ApiTokenWrapper(encryptedStoreService, refreshTokenApi),
        navController
    )

    val msgService = MessageService(
        this.storeService,
        this.msgRepo,
        locBinder,
    )

    val signalRMsgReceiver = SignalRMsgReceiver(
    this.msgService,
    encryptedStoreService
    )

    val userActionRepo = UserRepo(
        ApiTokenWrapper(encryptedStoreService, refreshTokenApi),
        navController
    )

    val userActionService = UserActionService(
        this.userActionRepo,
        locBinder,
        encryptedStoreService
    )

    val mapService = MapService(
        locBinder,
        navController,
        ApiTokenWrapper(encryptedStoreService, refreshTokenApi)
    )

    fun startServices() {
        // Location Service
        Intent(app.applicationContext, LocationService::class.java).apply {
            action = Constants.START_LOC_SERVICE
            app.startService(this)
        }
        // Binds and calls callback once locationService Bound
        this.locBinder.bindLocationService {
            // registers observer
            locBinder.registerObserver(mapService)
            // Initial Map load
            mapService.getMap()
        }
        // SignalR
        this.signalRMsgReceiver.startConnection()
    }

    fun stopServices() {
        // Location Service
        Intent(app.applicationContext, LocationService::class.java).apply {
            action = Constants.STOP_LOC_SERVICE
            app.startService(this)
        }
        // SignalR
        this.signalRMsgReceiver.stopConnection()
    }

}
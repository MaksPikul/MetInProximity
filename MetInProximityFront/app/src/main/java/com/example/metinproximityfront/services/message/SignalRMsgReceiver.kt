package com.example.metinproximityfront.services.message

import android.util.Log
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.services.preference.IStoreService
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single

/*
    https://learn.microsoft.com/en-us/aspnet/core/signalr/java-client?view=aspnetcore-9.0
    https://proandroiddev.com/signalr-android-tutorial-fe2302b8bbca
 */
class SignalRMsgReceiver(
    private val msgService : MessageService,
    private val encryptedStoreService : IStoreService,
) {

    private lateinit var hubConnection: HubConnection;

    fun startConnection() {

        val accessToken = encryptedStoreService.getFromPref(Constants.ACCESS_TOKEN_KEY)
        Log.i("SignalR", accessToken.toString())

        this.hubConnection = HubConnectionBuilder
            .create(Constants.SIGNALR_URL)
            .withAccessTokenProvider(Single.just(accessToken.toString()))
            .build()

        this.defineHubMethods()

        Log.i("SIGNALR", "Connected")

        this.hubConnection.start().subscribe(
            { Log.d("SignalR", "Connection Successful!") },
            { error -> Log.e("SignalR", "Connection Failed: ${error.message} + ${error.localizedMessage}") }
        )

        //Log.i("ConnId", hubConnection.get)
    }

    fun stopConnection(){
        this.hubConnection.stop().subscribe()
    }

    private fun defineHubMethods() {

        hubConnection.on("ReceiveMessage", { message: MsgResObject  ->
            val key : String = msgService.storeMessage( message )
            msgService.retrieveMessages()

        }, String::class.java)

        // TDOO : Place holder for now, Show UI error if user cannot connect
        hubConnection?.onClosed { exception ->
            if (exception != null) {
                Log.e("SignalR", "Disconnected: ${exception.message} , ${exception.localizedMessage}")

                if (exception.message?.contains("401") == true) {
                    Log.e("SignalR", "Unauthorized: Token might be invalid")
                }

                reconnect()
            } else {
                Log.e("SignalR", "Disconnected normally")
            }
        }
    }

    private fun reconnect() {
        if (hubConnection?.connectionState == HubConnectionState.DISCONNECTED) {
            Log.i("SignalR", "Attempting to reconnect...")
            hubConnection?.start()
        }
    }

}
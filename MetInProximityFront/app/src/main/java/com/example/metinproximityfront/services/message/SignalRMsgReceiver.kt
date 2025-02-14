package com.example.metinproximityfront.services.message

import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.services.preference.SharedStoreService
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single

/*
    https://learn.microsoft.com/en-us/aspnet/core/signalr/java-client?view=aspnetcore-9.0
    https://proandroiddev.com/signalr-android-tutorial-fe2302b8bbca
 */
class SignalRMsgReceiver(
    private val msgStore : MsgStoreService
) {

    private lateinit var hubConnection: HubConnection;

    fun startConnection() {

        this.hubConnection = HubConnectionBuilder
            .create(Constants.SIGNALR_URL)
            .build()
            /* No need for this since it only receives messages?
            .withAccessTokenProvider(
                Single.defer { Single.just(/* TODO AccessToken*/"token") }
            )
             */

        this.defineHubMethods()

        this.hubConnection.start()
    }

    fun stopConnection(){
        this.hubConnection.stop()
    }

    private fun defineHubMethods(){
/*
        hubConnection.on("ReceiveMessage", { message: String ->
            val key : String = msgStore.storeMessage(message)
        }, String::class.java)
*/
    }

}
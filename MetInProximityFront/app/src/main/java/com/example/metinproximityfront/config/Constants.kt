package com.example.metinproximityfront.config

object Constants {

    private val ip = "192.168.1.134"
    val BASE_URL: String = "http://${ip}:5000/api/" // 5177 , 7238

    val ACCESS_TOKEN_KEY = "Access_Token"
    val REFRESH_TOKEN_KEY = "Refresh_Token"

    val SIGNALR_URL = "http://${ip}:5000/chathub"

    val MsgSharedStoreServiceFileName = "MESSAGES"

    val START_LOC_SERVICE = "start_location_service"
    val STOP_LOC_SERVICE = "stop_location_service"

    fun PUBLIC_CHAT_KEY(
        userId: String
    ) : String {
        return "public-${userId}"
    }

    fun PRIVATE_CHAT_KEY (
        userId : String,
        recipientId: String?
    ) : String {
        return "private-${recipientId}-${userId}"
    }

}


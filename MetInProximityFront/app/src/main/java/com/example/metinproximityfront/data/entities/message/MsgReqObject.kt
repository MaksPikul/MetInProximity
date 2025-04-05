package com.example.metinproximityfront.data.entities.message

data class MsgReqObject(
    val body : String,
    val lon : Double,
    val lat : Double,
    var msgRecipientId : String?
)

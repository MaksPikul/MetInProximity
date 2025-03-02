package com.example.metinproximityfront.data.entities.message

data class MsgReqObject(
    val body : String,
    val longitude : Double,
    val latitude : Double,
    var recipientId : String?
)

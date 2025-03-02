package com.example.metinproximityfront.data.entities.message

import java.util.Date

data class MsgResObject(
    val body : String,
    val userId : String,
    val isPublic : Boolean,
    val recipientId : String?,
    val timestamp : Date
)

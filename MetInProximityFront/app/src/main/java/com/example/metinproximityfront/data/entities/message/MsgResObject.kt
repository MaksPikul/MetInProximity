package com.example.metinproximityfront.data.entities.message

import java.util.Date

data class MsgResObject(
    val Body : String,
    val UserId : String,
    val isPublic : Boolean,
    val RecipientId : String?,
    val Timestamp : Date
)

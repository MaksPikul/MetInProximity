package com.example.metinproximityfront.factories

import com.example.metinproximityfront.data.entities.message.MsgReqObject

object MessageFactory {

    fun CreateMsg(
        Body : String,
        Longitude : Double,
        Latitude : Double
    ) : MsgReqObject {
        return MsgReqObject(
            Body,
            Longitude,
            Latitude
        )
    }

}
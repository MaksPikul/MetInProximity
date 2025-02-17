package com.example.metinproximityfront.data.entities.location

class LocationObject(
    val Longitude : Double ,
    val Latitude : Double
) {

    val type: String = "Point"
    val coordinates = doubleArrayOf(Longitude, Latitude)

}




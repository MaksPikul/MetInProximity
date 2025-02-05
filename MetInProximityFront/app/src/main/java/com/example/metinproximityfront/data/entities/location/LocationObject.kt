package com.example.metinproximityfront.data.entities.location

data class LocationObject(
    val location: () -> Unit = {
        val type = "Point"
        val coordinates = doubleArrayOf(-122.4194, 37.7749)
    }
)

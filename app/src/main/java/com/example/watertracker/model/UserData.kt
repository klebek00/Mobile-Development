package com.example.watertracker.model


data class UserData(
    var gender: String? = "",
    var height: Int = 0,
    var weight: Int = 0,
    var age: Int = 0,
    var dailyWaterIntake: Double? = null,
    var counter: Double? = null
)

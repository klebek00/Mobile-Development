package com.example.watertracker.utility

class WaterIntakeCalculator {
    fun calculateDailyWaterIntake(age: Int, weight: Int, gender: String?): Double {
        val baseIntake = weight * 0.03

        val ageFactor = when {
            age < 30 -> 1.0
            age in 30..50 -> 0.9
            else -> 0.8
        }

        val genderFactor = if (gender == "Male") 1.0 else 0.9

        return baseIntake * ageFactor * genderFactor
    }
}
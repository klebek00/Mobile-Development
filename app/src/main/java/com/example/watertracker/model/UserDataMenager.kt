package com.example.watertracker.model

import android.content.Context
import com.example.watertracker.utility.WaterIntakeCalculator

class UserDataManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
    private val waterIntakeCalculator = WaterIntakeCalculator()


    fun saveUserData(userData: UserData) {
        val editor = sharedPreferences.edit()
        editor.putString("gender", userData.gender)
        editor.putInt("height", userData.height)
        editor.putInt("weight", userData.weight)
        editor.putInt("age", userData.age)
        val dailyWaterIntake = waterIntakeCalculator.calculateDailyWaterIntake(userData.age, userData.weight, userData.gender)
        editor.putFloat("dailyWaterIntake", dailyWaterIntake.toFloat() ?: -1f)
        editor.putFloat("counter", userData.counter?.toFloat() ?: -1f)
        editor.apply()
    }


    fun hasUserData(): Boolean {
        return sharedPreferences.contains("gender") &&
                sharedPreferences.contains("height") &&
                sharedPreferences.contains("weight") &&
                sharedPreferences.contains("age")
    }


    fun loadUserData(): UserData? {
        if (!hasUserData()) return null

        val gender = sharedPreferences.getString("gender", "")
        val height = sharedPreferences.getInt("height", 0)
        val weight = sharedPreferences.getInt("weight", 0)
        val age = sharedPreferences.getInt("age", 0)
        val dailyWaterIntake = sharedPreferences.getFloat("dailyWaterIntake", -1f).toDouble()
        val counter = sharedPreferences.getFloat("counter", -1f).toDouble()

        return UserData(gender, height, weight, age, dailyWaterIntake, counter)
    }

    fun updateCounter(newAmount: Double) {
        val currentCounter = sharedPreferences.getFloat("counter", 0f).toDouble()
        val updatedCounter = currentCounter + newAmount

        val editor = sharedPreferences.edit()
        editor.putFloat("counter", updatedCounter.toFloat())
        editor.apply()
    }
}

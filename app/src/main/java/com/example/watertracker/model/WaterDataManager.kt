package com.example.watertracker.model

import android.content.Context
import com.example.watertracker.utility.WaterIntakeCalculator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WaterDataManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("WaterData", Context.MODE_PRIVATE)
    private val waterIntakeCalculator = WaterIntakeCalculator()

//    fun hasUserData(): Boolean {
//        return sharedPreferences.contains("gender") &&
//                sharedPreferences.contains("height") &&
//                sharedPreferences.contains("weight") &&
//                sharedPreferences.contains("age")
//    }

    fun saveUserData(waterData: WaterData, userData: UserData) {
        val editor = sharedPreferences.edit()
//        editor.putString("gender", userData.gender)
//        editor.putInt("height", userData.height)
//        editor.putInt("weight", userData.weight)
//        editor.putInt("age", userData.age)
        val dailyWaterIntake = waterIntakeCalculator.calculateDailyWaterIntake(userData.age, userData.weight, userData.height, userData.gender)
        editor.putFloat("dailyWaterIntake", dailyWaterIntake.toFloat() ?: -1f)
        editor.putFloat("counter", waterData.counter?.toFloat() ?: -1f)
        editor.apply()
    }


    fun loadUserData(): WaterData{
//        val gender = sharedPreferences.getString("gender", "")
//        val height = sharedPreferences.getInt("height", 0)
//        val weight = sharedPreferences.getInt("weight", 0)
//        val age = sharedPreferences.getInt("age", 0)
        val dailyWaterIntake = sharedPreferences.getFloat("dailyWaterIntake", -1f).toDouble()
        val counter = sharedPreferences.getFloat("counter", -1f).toDouble()

        return WaterData(dailyWaterIntake, counter)
    }


    fun updateCounter(newAmount: Double) {
        val editor = sharedPreferences.edit()
        editor.putFloat("counter", newAmount.toFloat())
        editor.apply()
    }

    fun saveLastUpdateDate(date: String) {
        val editor = sharedPreferences.edit()
        editor.putString("lastUpdateDate", date)
        editor.apply()
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun loadLastUpdateDate(): String? {
        return sharedPreferences.getString("lastUpdateDate", null)
    }

}

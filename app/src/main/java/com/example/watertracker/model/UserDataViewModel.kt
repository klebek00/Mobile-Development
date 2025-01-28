package com.example.watertracker.model
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class UserDataViewModel(application: Application) : AndroidViewModel(application) {
    val userData = UserData()
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    init {
        loadUserData()
    }
    fun calculateDailyWaterIntake() {
        userData.weight?.let {
            var baseIntake = it * 0.033

            if (userData.gender == "F") {
                baseIntake *= 0.9
            }

            if (userData.age != null && userData.age!! > 50) {
                baseIntake *= 0.9
            }

            userData.dailyWaterIntake = baseIntake / 1000
        }
    }

    public fun saveUserData() {
        sharedPreferences.edit().apply {
            putString("gender", userData.gender)
            putInt("height", userData.height)
            putInt("weight", userData.weight)
            putInt("age", userData.age)
            putFloat("dailyWaterIntake", userData.dailyWaterIntake?.toFloat() ?: 0f)
            apply()
        }
    }

    private fun loadUserData() {
        userData.gender = sharedPreferences.getString("gender", "")
        userData.height = sharedPreferences.getInt("height", 0)
        userData.weight = sharedPreferences.getInt("weight", 0)
        userData.age = sharedPreferences.getInt("age", 0)
        userData.dailyWaterIntake = sharedPreferences.getFloat("dailyWaterIntake", 0f).toDouble()
    }


}
package com.example.watertracker.repository

import android.content.Context
import com.example.watertracker.model.UserData
import com.example.watertracker.model.WaterData
import com.example.watertracker.model.WaterDataManager

class WaterRepository(context: Context) {

    private val waterDataManager = WaterDataManager(context)

    fun loadUserData(): WaterData{
        return waterDataManager.loadUserData()
    }
    fun saveUserData(waterData: WaterData, userData: UserData) {
        waterDataManager.saveUserData(waterData, userData)
    }

    fun updateCounter(newAmount: Double) {
        waterDataManager.updateCounter(newAmount)
    }

    fun getCurrentDate(): String {
        return waterDataManager.getCurrentDate()
    }

    fun saveLastUpdateDate(date: String) {
        waterDataManager.saveLastUpdateDate(date)
    }

    fun loadLastUpdateDate(): String? {
        return  waterDataManager.loadLastUpdateDate()
    }
}

package com.example.watertracker.repository

import com.example.watertracker.model.UserData
import com.example.watertracker.model.UserDataManager

class UserRepository(private val userDataManager: UserDataManager) {

    fun saveUserData(userData: UserData) {
        userDataManager.saveUserData(userData)
    }

    fun getUserData(): UserData? {
        return userDataManager.loadUserData()
    }

    fun hasUserData(): Boolean {
        return userDataManager.hasUserData()
    }

    fun updateCounter(newAmount: Double) {
        userDataManager.updateCounter(newAmount)
    }

    fun getLastUpdateDate(): String? {
        return userDataManager.loadLastUpdateDate()
    }

    fun saveLastUpdateDate(date: String) {
        userDataManager.saveLastUpdateDate(date)
    }
}

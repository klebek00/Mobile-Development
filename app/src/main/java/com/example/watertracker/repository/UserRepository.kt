package com.example.watertracker.repository

import android.content.Context
import com.example.watertracker.model.UserData
import com.example.watertracker.model.UserDataManager

class UserRepository(context: Context) {

    private val userDataManager = UserDataManager(context)

    fun loadUserData(): UserData?{
        return userDataManager.loadUserData()
    }
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

    fun saveLastUpdateDate(date: String) {
        userDataManager.saveLastUpdateDate(date)
    }

    fun getLastUpdateDate(): String? {
        return userDataManager.loadLastUpdateDate()
    }

    fun getCurrentDate(): String {
        return userDataManager.getCurrentDate()
    }

    fun loadLastUpdateDate(): String? {
        return  userDataManager.loadLastUpdateDate()
    }
}

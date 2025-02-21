package com.example.watertracker.repository

import android.content.Context
import android.util.Log
import com.example.watertracker.model.ThemesData
import com.example.watertracker.model.UserData
import com.example.watertracker.model.UserDataModel

class UserDataRepository(private val context: Context) {
    private val userDataModel = UserDataModel(context)

    fun saveUserData(userData: UserData) {
        userDataModel.saveUserData(userData)
    }

    fun checkIfUserDataExists(callback: (Boolean) -> Unit) {
        userDataModel.hasUserData { exists ->
            callback(exists)
        }
    }

    fun loadUserData(callback: (UserData?) -> Unit) {
        userDataModel.loadUserData { userData ->
            if (userData != null) {
                callback(userData)
            } else {
                Log.e("UserDataViewModel", "Данные пользователя не найдены")
                callback(null)
            }
        }
    }

    fun loadThemeData(callback: (ThemesData?) -> Unit) {
        userDataModel.loadThemeData { themeData ->
            if (themeData != null) {
                callback(themeData)
            } else {
                Log.e("UserDataViewModel", "Данные пользователя не найдены")
                callback(null)
            }
        }
    }

    fun saveThemeData(themesData: ThemesData) {
        userDataModel.saveThemeData(themesData) { success ->
            if (success) {
                Log.d("Firestore", "Тема успешно сохранена")
            } else {
                Log.e("Firestore", "Ошибка при сохранении темы")
            }
        }
    }






}
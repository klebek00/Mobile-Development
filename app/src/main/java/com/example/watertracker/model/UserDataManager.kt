package com.example.watertracker.model

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class UserDataModel(private val context : Context) {


    val db = Firebase.firestore
    private val userCollection = db.collection("users")

    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun saveUserData(userData: UserData) {
        val deviceId = getDeviceId()

        val userMap = hashMapOf(
            "gender" to userData.gender,
            "height" to userData.height,
            "weight" to userData.weight,
            "age" to userData.age,

        )

        db.collection("users").document(deviceId)
            .set(userMap)
            .addOnSuccessListener {
                Log.d("Firestore", "Данные успешно сохранены!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Ошибка при сохранении данных", e)
            }

    }
    fun hasUserData(callback: (Boolean) -> Unit) {
        db.collection("users").document(getDeviceId())
            .get()
            .addOnSuccessListener { document ->
                callback(document.exists())
            }
            .addOnFailureListener { e ->
                callback(false)
            }
    }

    fun loadUserData(callback: (UserData?) -> Unit) {
        db.collection("users").document(getDeviceId())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = document.toObject(UserData::class.java)
                    callback(userData)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Ошибка при загрузке данных", e)
                callback(null)
            }
    }



}
package com.example.watertracker.model
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watertracker.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

    private val _counter = MutableLiveData<Double>()
    val counter: LiveData<Double> get() = _counter

    fun loadUserData() {
        _userData.value = repository.getUserData()
    }

    fun saveUserData(userData: UserData) {
        repository.saveUserData(userData)
        _userData.value = userData
    }

    fun updateCounter(newAmount: Double) {
        repository.updateCounter(newAmount)
        _counter.value = newAmount
    }

    fun resetCounterIfNeeded() {
        val lastUpdate = repository.getLastUpdateDate()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (lastUpdate != today) {
            repository.updateCounter(0.0)
            repository.saveLastUpdateDate(today)
            _counter.value = 0.0
        }
    }
}

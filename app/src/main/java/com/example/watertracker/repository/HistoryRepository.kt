package com.example.watertracker.repository

import android.content.Context
import android.util.Log
import com.example.watertracker.model.HistoryData
import com.example.watertracker.model.HistoryDataManager
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoryRepository(private val context: Context){
    private val historyDataManager = HistoryDataManager(context)

    fun saveUserData(historyData: HistoryData) {
        historyDataManager.saveUserData(historyData)
    }

    fun checkIfUserDataExists(callback: (Boolean) -> Unit) {
        historyDataManager.hasUserData { exists ->
            callback(exists)
        }
    }

    fun loadUserData(callback: (List<HistoryData>?) -> Unit) {
        historyDataManager.loadUserData { historyData ->
            if (historyData != null) {
                callback(historyData)
            } else {
                Log.e("UserDataViewModel", "Данные пользователя не найдены")
                callback(null)
            }
        }
    }

    // Получение данных за сегодняшний день
    fun getTodayHistory(historyList: List<HistoryData>): List<HistoryData> {
        return historyDataManager.getTodayHistory(historyList)
    }

    // Получение данных за текущую неделю
    fun getWeekData(): Map<String, Double> {
        var weekHistory = mapOf<String, Double>()
        historyDataManager.loadUserData { historyList ->
            if (historyList != null) {
                weekHistory = historyDataManager.getWeekHistory(historyList)
            }
        }
        return weekHistory
    }

    // Получение данных за текущий месяц
    fun getMonthData(): Map<String, Double> {
        var monthHistory = mapOf<String, Double>()
        historyDataManager.loadUserData { historyList ->
            if (historyList != null) {
                monthHistory = historyDataManager.getMonthHistory(historyList)
            }
        }
        return monthHistory
    }

    fun getTotalWaterIntake(callback: (String) -> Unit) {
        loadUserData { historyList ->
            if (historyList != null) {
                val totalWaterIntake = historyList.sumOf { it.amount }
                val formattedTotal = String.format("%.2f", totalWaterIntake) // Округление до 2 знаков
                callback(formattedTotal)
            } else {
                callback("0.00") // Если данных нет, возвращаем "0.00"
            }
        }
    }

    fun getDailyGoalCompletionCount(dailyGoal: Double): Int {
        var completedGoals = 0

        loadUserData { historyList ->
            if (historyList != null) {
                val dailyIntakes = historyList.groupBy { entry ->
                    val entryDate = Date(entry.date)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateFormat.format(entryDate)
                }

                dailyIntakes.forEach { (date, entries) ->
                    val totalIntakeForDay = entries.sumOf { it.amount }
                    if (totalIntakeForDay >= dailyGoal) {
                        completedGoals++
                    }
                }
            }
        }

        return completedGoals
    }



}
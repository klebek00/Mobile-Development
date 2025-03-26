package com.example.watertracker.model

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoryDataManager(private val context : Context) {
    val db = Firebase.firestore
    private val userCollection = db.collection("history")

    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun saveUserData(historyData: HistoryData) {

        val userMap = hashMapOf(
            "amount" to historyData.amount,
            "date" to historyData.date,
            )

        db.collection("users")
            .document(getDeviceId())
            .collection("history")
            .add(userMap)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Данные успешно сохранены: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Ошибка при сохранении данных", e)
            }

//        db.collection("history").document(deviceId)
//            .set(userMap)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Данные успешно сохранены!")
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Ошибка при сохранении данных", e)
//            }

    }


    fun hasUserData(callback: (Boolean) -> Unit) {
        db.collection("history").document(getDeviceId())
            .get()
            .addOnSuccessListener { document ->
                callback(document.exists())
            }
            .addOnFailureListener { e ->
                callback(false)
            }
    }

    fun loadUserData(callback: (List<HistoryData>) -> Unit) {
        db.collection("users")
            .document(getDeviceId())
            .collection("history")
            .get()
            .addOnSuccessListener { documents ->
                val historyList = documents.mapNotNull { it.toObject(HistoryData::class.java) }
                callback(historyList)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Ошибка при загрузке данных", e)
                callback(emptyList()) // Если ошибка — возвращаем пустой список
            }
    }



    fun getTodayHistory(historyList: List<HistoryData>): List<HistoryData> {
        val today = LocalDate.now()

        return historyList.filter { entry ->
            Instant.ofEpochMilli(entry.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate() == today
        }
    }

    fun getWeekHistory(historyList: List<HistoryData>): Map<String, Double> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Получаем понедельник текущей недели
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = calendar.time

        // Получаем воскресенье текущей недели
        calendar.add(Calendar.DAY_OF_YEAR, 6)
        val endOfWeek = calendar.time

        val daysOfWeek = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

        // Инициализируем результат с нулевыми значениями
        val result = mutableMapOf<String, Double>().apply {
            daysOfWeek.forEach { put(it, 0.0) }
        }

        for (entry in historyList) {
            val entryDate = Date(entry.date.toLong())
            val entryDateString = dateFormat.format(entryDate)
            val entryDateParsed = dateFormat.parse(entryDateString) ?: continue

            if (!entryDateParsed.before(startOfWeek) && !entryDateParsed.after(endOfWeek)) {
                // Определяем день недели записи (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
                calendar.time = entryDateParsed
                val dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Приводим к 0-based индексу

                // Присваиваем значение в result
                val dayName = daysOfWeek[dayOfWeekIndex]
                result[dayName] = result.getOrDefault(dayName, 0.0) + entry.amount
            }
        }

        return result
    }



    fun getMonthHistory(historyList: List<HistoryData>): Map<String, Double> {
        val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault()) // Формат "ГГГГ-ММ" для месяца

        val result = mutableMapOf<String, Double>()

        // Получаем текущий год
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        // Создаем карту с месяцами от "YYYY-01" до "YYYY-12"
        for (month in 1..12) {
            val monthString = String.format("%d-%02d", currentYear, month)
            result[monthString] = 0.0 // Устанавливаем 0.0 для каждого месяца
        }

        // Обрабатываем данные пользователя
        for (entry in historyList) {
            val entryDate = Date(entry.date) // Преобразуем timestamp в Date
            val monthYear = dateFormat.format(entryDate) // Получаем строку вида "2025-02" (ГГГГ-ММ)

            // Обновляем сумму для месяца
            result[monthYear] = result.getOrDefault(monthYear, 0.0) + entry.amount
        }

        return result
    }

}
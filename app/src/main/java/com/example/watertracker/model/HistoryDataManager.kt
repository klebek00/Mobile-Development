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
        val today = Date()

        // Получаем день недели для сегодняшнего дня
        calendar.time = today
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Устанавливаем начало недели (понедельник)
        calendar.add(Calendar.DAY_OF_YEAR, Calendar.MONDAY - currentDayOfWeek)
        val startOfWeek = calendar.time

        // Получаем дату конца недели (воскресенье)
        calendar.add(Calendar.DAY_OF_YEAR, 6)
        val endOfWeek = calendar.time

        // Форматирование для сравнения даты
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Фильтрация записей, которые попадают в текущую неделю
        val weekData = historyList.filter { entry ->
            val entryDate = Date(entry.date) // Преобразуем timestamp в Date
            val entryDateString = dateFormat.format(entryDate) // Форматируем дату в строку
            val entryDateParsed = dateFormat.parse(entryDateString)
            entryDateParsed != null && !entryDateParsed.before(startOfWeek) && !entryDateParsed.after(endOfWeek)
        }

        // Группируем по дням недели и суммируем количество воды для каждого дня
        val result = mutableMapOf<String, Double>()
        for (entry in weekData) {
            val entryDate = Date(entry.date)
            calendar.time = entryDate
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            // Преобразуем день недели в строку (например, Понедельник, Вторник)
            val dayName = when (dayOfWeek) {
                Calendar.MONDAY -> "Понедельник"
                Calendar.TUESDAY -> "Вторник"
                Calendar.WEDNESDAY -> "Среда"
                Calendar.THURSDAY -> "Четверг"
                Calendar.FRIDAY -> "Пятница"
                Calendar.SATURDAY -> "Суббота"
                Calendar.SUNDAY -> "Воскресенье"
                else -> "Неизвестный день"
            }

            // Добавляем количество воды к сумме для данного дня
            result[dayName] = result.getOrDefault(dayName, 0.0) + entry.amount
        }

        return result
    }

    fun getMonthHistory(historyList: List<HistoryData>): Map<String, Double> {
        val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault()) // Формат "ГГГГ-ММ" для месяца

        val result = mutableMapOf<String, Double>()

        for (entry in historyList) {
            val entryDate = Date(entry.date) // Преобразуем timestamp в Date
            val monthYear = dateFormat.format(entryDate) // Получаем строку вида "2025-02" (ГГГГ-ММ)

            result[monthYear] = result.getOrDefault(monthYear, 0.0) + entry.amount
        }

        return result
    }
}
package com.example.watertracker

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.watertracker.api.NotificationHelper
import com.example.watertracker.api.sensor.SensorManagerHelper
import com.example.watertracker.api.widget.AppWidget
import com.example.watertracker.dataCollection.WaightActivity
import com.example.watertracker.model.HistoryData
import com.example.watertracker.repository.HistoryRepository
import com.example.watertracker.repository.UserDataRepository
import com.example.watertracker.repository.WaterRepository


class MainActivity : AppCompatActivity() {
    private var progress = 0
    private lateinit var updateAppReceiver: BroadcastReceiver
    private lateinit var sensorManagerHelper: SensorManagerHelper
    private lateinit var button: Button
    private lateinit var buttonToday: Button
    private lateinit var buttonHistory: Button
    private lateinit var buttonMe: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView
    private lateinit var waterRepository: WaterRepository
    private lateinit var userDataRepository: UserDataRepository
    private var counter: Double = 0.0
    private var dailyWaterIntake: Double = 0.0
    private var water: Double = 200.0
    val intervalMillis = 600 * 1000L

    private lateinit var historyRepository: HistoryRepository

    private lateinit var notificationHelper: NotificationHelper

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerUpdateReceiver() {
        val appUpdateFilter = IntentFilter("com.example.watertracker.ACTION_UPDATE_APP")
        updateAppReceiver = object : BroadcastReceiver() {
            var isUpdateInProgress = false  // флаг для предотвращения бесконечного цикла

            override fun onReceive(context: Context?, intent: Intent?) {
                if (isUpdateInProgress) {
                    return  // Если уже идет обработка, ничего не делаем
                }
                isUpdateInProgress = true  // Устанавливаем флаг, что обработка началась

                val userData = waterRepository.loadUserData()
                if (userData != null) {
                    counter = userData.counter ?: 0.0
                    Log.d("Counter", "Значение counter: $counter")
                    dailyWaterIntake = userData.dailyWaterIntake ?: 0.0
                    amountCounter()
                    progressCounter()
                    updateProgressBar()

                    // Проверяем флаг перед отправкой сигнала, чтобы избежать зацикливания
                    if (!isUpdateInProgress) {
                        sendBroadcast(intent)  // если нужно отправить, но нужно быть осторожным с этим
                    }
                }

                isUpdateInProgress = false  // Сбрасываем флаг после завершения обработки
            }
        }

        registerReceiver(updateAppReceiver, appUpdateFilter)
    }



    override fun onStart() {
        super.onStart()
        registerUpdateReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(updateAppReceiver)
    }

    override fun onPause() {
        super.onPause()
        sensorManagerHelper.stop()
    }


    override fun onResume() {
        super.onResume()
        sensorManagerHelper.start()
        val ml = resources.getStringArray(R.array.array)
        val arr = ArrayAdapter(this, R.layout.dropdown_item, ml)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(arr)
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()
        notificationHelper.setWaterReminderAlarm(intervalMillis)

        sensorManagerHelper = SensorManagerHelper(this) { newCounter ->
            counter += newCounter
            waterRepository.updateCounter(counter)
            amountCounter()
            progressCounter()
            updateProgressBar()
            val date = System.currentTimeMillis()
            val historyData = HistoryData(0.05, date)
            historyRepository.saveUserData(historyData)
            val intent = Intent(AppWidget.ACTION_UPDATE_WIDGET)
            sendBroadcast(intent)
        }

        waterRepository = WaterRepository(this)
        userDataRepository = UserDataRepository(this)
        historyRepository = HistoryRepository(this)

        val ml = resources.getStringArray(R.array.array)
        val arr = ArrayAdapter(this, R.layout.dropdown_item, ml)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(arr)

        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()

            val number = selectedItem.replace(Regex("[^0-9]"), "").toDoubleOrNull() ?: 0.2
            Log.d("MyTag", "Значение number: $number")

            water = number
        }

        button = findViewById(R.id.button7)
        progressBar = findViewById(R.id.progress_bar)
        textView = findViewById(R.id.text_view_progress)
        buttonToday = findViewById(R.id.button11)
        buttonHistory = findViewById(R.id.button12)
        buttonMe = findViewById(R.id.button13)



        val lastUpdateDate = waterRepository.loadLastUpdateDate()
        val currentDate = waterRepository.getCurrentDate()

        Log.d("MyTag", "Значение lastUpdateDate: $lastUpdateDate")
        Log.d("MyTag", "Значение currentDate: $currentDate")

        if (lastUpdateDate == null || lastUpdateDate != currentDate) {
            counter = 0.0
            progress = 0
            waterRepository.updateCounter(counter)
            waterRepository.saveLastUpdateDate(currentDate)

        } else {
            val userData = waterRepository.loadUserData()
            if (userData != null) {
                dailyWaterIntake = userData.dailyWaterIntake ?: 0.0
                counter = userData.counter ?: 0.0
                water = 200.0
            }
        }



        if (savedInstanceState != null) {
            progress = savedInstanceState.getInt("progress", 0)
            counter = savedInstanceState.getDouble("counter", 0.0)
            dailyWaterIntake = savedInstanceState.getDouble("dailyWaterIntake", 0.0)
            water = savedInstanceState.getDouble("water", 0.2)
        } else {
            val userData = waterRepository.loadUserData()
            if (userData != null) {
                dailyWaterIntake = userData.dailyWaterIntake ?: 0.0
                counter = userData.counter ?: 0.0
                water
            }
        }

        if (dailyWaterIntake > 0) {
            progressCounter()
        }

        amountCounter()
        updateProgressBar()

        button.setOnClickListener {

            val amount = water.div(1000)
            val date = System.currentTimeMillis()
            counter += amount

            amountCounter()

            waterRepository.updateCounter(counter)

            if (progress < 100) {
                progress = (counter / dailyWaterIntake * 100).toInt()
                if (progress > 100) {
                    progress = 100
                }
                updateProgressBar()

            }
            val intent = Intent(AppWidget.ACTION_UPDATE_WIDGET)
            val historyData = HistoryData(amount, date)
            historyRepository.saveUserData(historyData)
            sendBroadcast(intent)
        }

        buttonHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
            finish()

        }
        buttonMe.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
            finish()

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("progress", progress)
        outState.putDouble("counter", counter)
        outState.putDouble("dailyWaterIntake", dailyWaterIntake)
        outState.putDouble("water", water)
    }
    private fun progressCounter()
    {
        progress = (counter / dailyWaterIntake * 100).toInt()
        if (progress > 100) progress = 100
    }
    private fun updateProgressBar() {
        progressBar.progress = progress
        textView.text = "${progress}%"
    }

    private fun amountCounter() {
        val textViewResult = findViewById<TextView>(R.id.textView3)
        val formattedDailyWaterIntake = String.format("%.2f", dailyWaterIntake)
        val formattedCounter = String.format("%.2f", counter)
        textViewResult.text = "$formattedCounter / $formattedDailyWaterIntake L"
    }

    fun checkAndSetReminder() {
        val lastReminderTime = notificationHelper.getLastReminderTime()
        val currentTime = System.currentTimeMillis()

        if (lastReminderTime == 0L || currentTime - lastReminderTime >= intervalMillis) {
            notificationHelper.setWaterReminderAlarm(intervalMillis)
            notificationHelper.saveLastReminderTime(currentTime)
        }
    }

}


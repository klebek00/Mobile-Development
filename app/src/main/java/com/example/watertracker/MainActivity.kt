package com.example.watertracker

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View;
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.watertracker.model.UserDataManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var progress = 0
    private lateinit var button: Button
    private lateinit var buttonToday: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView
    private lateinit var userDataManager: UserDataManager
    private var counter: Double = 0.0
    private var dailyWaterIntake: Double = 0.0
    private var water: Double = 200.0


    override fun onResume() {
        super.onResume()
        val ml = resources.getStringArray(R.array.array)
        val arr = ArrayAdapter(this, R.layout.dropdown_item, ml)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(arr)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


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
        buttonToday = findViewById(R.id.button11)
        progressBar = findViewById(R.id.progress_bar)
        textView = findViewById(R.id.text_view_progress)

        userDataManager = UserDataManager(this)

        val lastUpdateDate = userDataManager.loadLastUpdateDate()
        val currentDate = getCurrentDate()

        Log.d("MyTag", "Значение lastUpdateDate: $lastUpdateDate")
        Log.d("MyTag", "Значение currentDate: $currentDate")

        if (lastUpdateDate == null || lastUpdateDate != currentDate) {
            counter = 0.0
            progress = 0
            userDataManager.updateCounter(counter)
            userDataManager.saveLastUpdateDate(currentDate)

        } else {
            val userData = userDataManager.loadUserData()
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
            val userData = userDataManager.loadUserData()
            if (userData != null) {
                dailyWaterIntake = userData.dailyWaterIntake ?: 0.0
                counter = userData.counter ?: 0.0
                water
            }
        }

        if (dailyWaterIntake > 0) {
            progress = (counter / dailyWaterIntake * 100).toInt()
            if (progress > 100) progress = 100
        }

        amountCounter()
        updateProgressBar()

        button.setOnClickListener {

            counter += water.div(1000)

            amountCounter()

            userDataManager.updateCounter(counter)

            if (progress < 100) {
                progress = (counter / dailyWaterIntake * 100).toInt()
                if (progress > 100) {
                    progress = 100
                }
                updateProgressBar()
            }
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

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}


//choose counter
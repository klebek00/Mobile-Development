package com.example.watertracker.dataCollection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.watertracker.MainActivity
import com.example.watertracker.R
import com.example.watertracker.model.UserData
import com.example.watertracker.model.UserDataManager

class WaightActivity : AppCompatActivity() {
    private lateinit var weight : NumberPicker
    private lateinit var userDataManager: UserDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_waight)

        weight = findViewById(R.id.numPicker2)

        val buttonNext = findViewById<Button>(R.id.buttonNext)
        val gender = intent.getStringExtra("gender")
        val age = intent.getStringExtra("age")
        val hight = intent.getStringExtra("hight")

        weight.setMinValue(20)
        weight.setMaxValue(150)

        userDataManager = UserDataManager(this)

        buttonNext.setOnClickListener {
            val selectedWeight = weight.value

            val userData = UserData(
                gender = gender,
                height = hight?.toInt() ?: 0,
                weight = selectedWeight,
                age = age?.toInt() ?: 0,
                dailyWaterIntake = 0.0,
                counter = 0.0
            )

            userDataManager.saveUserData(userData)

            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
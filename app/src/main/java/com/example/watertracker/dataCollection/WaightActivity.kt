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
import com.example.watertracker.model.WaterData
import com.example.watertracker.repository.UserDataRepository
import com.example.watertracker.repository.WaterRepository

class WaightActivity : AppCompatActivity() {
    private lateinit var weight : NumberPicker
    private lateinit var waterRepository: WaterRepository
    private lateinit var userDataRepository: UserDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_waight)

        weight = findViewById(R.id.numPicker2)

        val buttonNext = findViewById<Button>(R.id.buttonNext)
        val gender = intent.getStringExtra("gender")
        val age = intent.getIntExtra("age", 0)
        val height = intent.getIntExtra("height", 0)

        weight.setMinValue(20)
        weight.setMaxValue(150)

        if (savedInstanceState != null) {
            val savedW = savedInstanceState.getInt("selectedW", 100)
            if (savedW > 0) {
                weight.value = savedW
            }
        }

        waterRepository = WaterRepository(this)

        userDataRepository = UserDataRepository(this)

        buttonNext.setOnClickListener {
            val selectedWeight = weight.value

            val userData = UserData(
                gender = gender,
                height = height,
                weight = selectedWeight,
                age = age
            )
            val waterData = WaterData(
                dailyWaterIntake = 0.0,
                counter = 0.0
            )

            waterRepository.saveUserData(waterData, userData)
            userDataRepository.saveUserData(userData)

            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
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
        outState.putInt("selectedW", weight.value)
    }
}
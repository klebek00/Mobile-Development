package com.example.watertracker.dataCollection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.NumberPicker
import android.widget.RadioGroup
import com.example.watertracker.MainActivity
import com.example.watertracker.R

class AgeActivity : AppCompatActivity() {
    private lateinit var age : NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_age)

        val buttonNext = findViewById<Button>(R.id.buttonNext)
        val gender = intent.getStringExtra("gender")


        age = findViewById(R.id.numPicker1)

        age.setMinValue(5)
        age.setMaxValue(100)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonNext.setOnClickListener {
            val selectedAge = age.value

            val intent = Intent(this, HightActivity::class.java)
            intent.putExtra("gender", gender)
            intent.putExtra("age", selectedAge)
            startActivity(intent)
        }
    }
}
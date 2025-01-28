package com.example.watertracker.dataCollection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.watertracker.R

class HightActivity : AppCompatActivity() {

    private lateinit var hight : NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hight)

        hight = findViewById(R.id.numPicker1)

        val buttonNext = findViewById<Button>(R.id.buttonNext)
        val gender = intent.getStringExtra("gender")
        val age = intent.getStringExtra("age")

        hight.setMinValue(100)
        hight.setMaxValue(250)

        buttonNext.setOnClickListener {
            val selectedhight = hight.value
            val intent = Intent(this, WaightActivity::class.java)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            intent.putExtra("hight", selectedhight)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
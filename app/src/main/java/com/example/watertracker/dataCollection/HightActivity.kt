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

    private lateinit var height : NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hight)

        height = findViewById(R.id.numPicker1)

        val buttonNext = findViewById<Button>(R.id.buttonNext)
        val gender = intent.getStringExtra("gender")
        val age = intent.getIntExtra("age", 0)

        height.setMinValue(100)
        height.setMaxValue(250)

        if (savedInstanceState != null) {
            val savedH = savedInstanceState.getInt("selectedH", 100)
            if (savedH > 0) {
                height.value = savedH
            }
        }
        buttonNext.setOnClickListener {
            val selectedhight = height.value
            val intent = Intent(this, WaightActivity::class.java)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            intent.putExtra("height", selectedhight)
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
        outState.putInt("selectedH", height.value)
    }
}
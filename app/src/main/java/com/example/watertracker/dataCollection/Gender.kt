package com.example.watertracker.dataCollection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.watertracker.MainActivity
import com.example.watertracker.R

class Gender : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gender)

        val buttonNext = findViewById<Button>(R.id.buttonNext)
        val genderRadioGroup = findViewById<RadioGroup>(R.id.gen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonNext.setOnClickListener {
            val selectedGender = getSelectedGender(genderRadioGroup)

            val intent = Intent(this, AgeActivity::class.java)
            intent.putExtra("gender", selectedGender)
            startActivity(intent)
            finish()
        }
    }

    private fun getSelectedGender(radioGroup: RadioGroup): String? {
        val selectedId = radioGroup.checkedRadioButtonId
        if (selectedId != -1) {
            val selectedRadioButton = findViewById<RadioButton>(selectedId)
            return selectedRadioButton.text.toString()
        }
        return null
    }
}
package com.example.watertracker

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.watertracker.model.ThemesData
import com.example.watertracker.repository.UserDataRepository

class SettingActivity : AppCompatActivity() {
    private lateinit var buttonToday: Button
    private lateinit var buttonHistory: Button
    private lateinit var buttonMe: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioLight: RadioButton
    private lateinit var radioDark: RadioButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userDataRepository: UserDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)

        buttonToday = findViewById(R.id.button11)
        buttonHistory = findViewById(R.id.button12)
        buttonMe = findViewById(R.id.button13)
        radioGroup = findViewById(R.id.gen)
        radioLight = findViewById(R.id.radioButton)
        radioDark = findViewById(R.id.radioButton2)
        userDataRepository = UserDataRepository(this)

        if (isDarkTheme) {
            radioDark.isChecked = true
        } else {
            radioLight.isChecked = true
        }



//        radioGroup.setOnCheckedChangeListener { _, checkedId ->
//            val isDark = checkedId == R.id.radioButton2
//            val currentTheme = sharedPreferences.getBoolean("dark_theme", false)
//
//            if (currentTheme != isDark) {
//                val userTheme = ThemesData(isDark)
//                userDataRepository.saveThemeData(userTheme)
//                saveThemePreference(isDark)
//
//                // Передаем тему в MainActivity через Intent
//                val intent = Intent(this, MainActivity::class.java).apply {
//                    putExtra("theme_is_dark", isDark)
//                }
//                val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
//                startActivity(intent, options.toBundle())
//                finish()
//            }
//        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton -> {
                    val userTheme = ThemesData(false)
                    // Сохранить светлую тему в Firebase
                    userDataRepository.saveThemeData(userTheme)
                    saveThemePreference(isDark = false)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    val intent2 = Intent(this, SettingActivity::class.java)
                    val options2 = ActivityOptions.makeCustomAnimation(this, 0, 0)
                    startActivity(intent2, options2.toBundle())
                    finish()
//                    val intent = Intent(this, MainActivity::class.java)
//                    val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
//                    startActivity(intent, options.toBundle())
//                    finish()
                }
                R.id.radioButton2 -> {
                    val userTheme = ThemesData(true)
                    // Сохранить темную тему в Firebase
                    userDataRepository.saveThemeData(userTheme)
                    saveThemePreference(isDark = true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    val intent2 = Intent(this, SettingActivity::class.java)
                    val options2 = ActivityOptions.makeCustomAnimation(this, 0, 0)
                    startActivity(intent2, options2.toBundle())
                    finish()
//                    val intent = Intent(this, MainActivity::class.java)
//                    val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
//                    startActivity(intent, options.toBundle())
//                    finish()
                }
            }
        }



        buttonToday.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
            finish()
        }
        buttonHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
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

    private fun saveThemePreference(isDark: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("dark_theme", isDark)
            apply()
        }
    }
}
package com.example.watertracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.watertracker.dataCollection.Gender
import com.example.watertracker.model.UserDataManager

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen)

        val userDataManager = UserDataManager(this)
        if (userDataManager.hasUserData()) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, Gender::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }


    }
}
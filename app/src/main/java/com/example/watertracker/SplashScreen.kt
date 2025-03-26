package com.example.watertracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.app.PassKeyManager

import com.example.watertracker.passkey.AuthActivity
import com.example.watertracker.passkey.CreatePassKeyActivity
import com.example.watertracker.repository.UserDataRepository

class SplashScreen : AppCompatActivity() {
    private lateinit var passKeyManager: PassKeyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen)

//        val userRepository = UserRepository(this)
        val userDataRepository = UserDataRepository(this)
        passKeyManager = PassKeyManager(this)


//        if (userRepository.hasUserData()) {
//            Handler(Looper.getMainLooper()).postDelayed({
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }, 2000)
//        } else {
//            Handler(Looper.getMainLooper()).postDelayed({
//                val intent = Intent(this, Gender::class.java)
//                startActivity(intent)
//                finish()
//            }, 2000)
//        }
//        userDataRepository.checkIfUserDataExists { exists ->
//            if (exists) {
//                Handler(Looper.getMainLooper()).postDelayed({
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }, 2000)
////                if (KeystoreManager.isPassKeyAvailable()) {
////                    Handler(Looper.getMainLooper()).postDelayed({
////                        val intent = Intent(this, AuthActivity::class.java)
////                        startActivity(intent)
////                        finish()
////                    }, 2000)
////                } else{
////                    Handler(Looper.getMainLooper()).postDelayed({
////                        val intent = Intent(this, CreatePassKeyActivity::class.java)
////                        startActivity(intent)
////                        finish()
////                    }, 2000)
////                }
//
//            } else {
////                Handler(Looper.getMainLooper()).postDelayed({
////                    val intent = Intent(this, CreatePassKeyActivity::class.java)
////                    startActivity(intent)
////                    finish()
////                }, 2000)
//                Handler(Looper.getMainLooper()).postDelayed({
//                    val intent = Intent(this, Gender::class.java)
//                    startActivity(intent)
//                    finish()
//                }, 2000)
//            }

        userDataRepository.checkIfUserDataExists { exists ->
            if (exists) {
                if (passKeyManager.isPassKeySet()) {
                    Log.d("Counter", "1")
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                } else{
                    Log.d("Counter", "2")
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this, CreatePassKeyActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }

            } else {
                Log.d("Counter", "3")
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, CreatePassKeyActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
//                Handler(Looper.getMainLooper()).postDelayed({
//                    val intent = Intent(this, Gender::class.java)
//                    startActivity(intent)
//                    finish()
//                }, 2000)
            }
        }


    }
}
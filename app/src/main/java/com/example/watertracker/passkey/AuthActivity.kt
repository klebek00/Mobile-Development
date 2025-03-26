package com.example.watertracker.passkey

import CredentialManagerHelper
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.lifecycle.lifecycleScope
import com.example.app.BiometricAuthHelper
import com.example.app.PassKeyManager
import com.example.watertracker.MainActivity
import com.example.watertracker.R
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var passKeyManager: PassKeyManager
    private lateinit var biometricAuthHelper: BiometricAuthHelper
    private lateinit var login : Button
    private lateinit var input : TextView
    private lateinit var restart : Button
    private lateinit var credentialManagerHelper: CredentialManagerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        passKeyManager = PassKeyManager(this)
        biometricAuthHelper = BiometricAuthHelper(this)
        login = findViewById(R.id.loginButton)
        input = findViewById(R.id.passKeyInput)
        restart = findViewById(R.id.resetButton)
        credentialManagerHelper = CredentialManagerHelper(this)


        // Если Pass Key не установлен, переходим на экран регистрации
        if (!passKeyManager.isPassKeySet()) {
            startActivity(Intent(this, CreatePassKeyActivity::class.java))
            finish()
            return
        }


        input.setOnClickListener{
            lifecycleScope.launch {
                requestSignIn()
            }
        }

        login.setOnClickListener {
            val enteredPassKey = input.text.toString()
            if (passKeyManager.validatePassKey(enteredPassKey)) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }


        restart.setOnClickListener {
            authenticateAndResetPassKey()

        }

    }
    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private suspend fun requestSignIn() {
        val credential: Credential? = credentialManagerHelper.requestSignIn()

        when (credential) {
            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                if (passKeyManager.validatePassKey(password)) {
                    startMainActivity()
                } else {
                    Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show()
                }
            }

            is PublicKeyCredential -> {
                startMainActivity()
            }

            else -> {
                // Нет сохраненных учетных данных
                Toast.makeText(this, "Не найдено сохраненных учетных данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authenticateAndResetPassKey() {
        biometricAuthHelper.authenticate(object : BiometricAuthHelper.AuthCallback {
            override fun onSuccess() {
                // Биометрия успешно проверена, сбрасываем пароль
                passKeyManager.resetPassKey()
                if (!passKeyManager.isPassKeySet()) {
                    startActivity(Intent(this@AuthActivity, CreatePassKeyActivity::class.java))
                    finish()
                }
            }

            override fun onError() {
                // Ошибка биометрической аутентификации
                Toast.makeText(this@AuthActivity, "Биометрическая аутентификация не удалась", Toast.LENGTH_SHORT).show()
            }
        })
    }

}


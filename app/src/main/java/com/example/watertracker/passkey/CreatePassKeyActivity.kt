package com.example.watertracker.passkey

import CredentialManagerHelper
import android.app.AlertDialog
import android.content.Intent
import androidx.biometric.BiometricPrompt;
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.app.PassKeyManager
import com.example.watertracker.R
import kotlinx.coroutines.launch

class CreatePassKeyActivity : AppCompatActivity() {

    private lateinit var credentialManagerHelper: CredentialManagerHelper
    private lateinit var passKeyManager: PassKeyManager
    private lateinit var passKeyEditText: EditText
    private lateinit var saveButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pass_key)

        val username = "sleepingmarmot666@gmail.com"

        credentialManagerHelper = CredentialManagerHelper(this)
        passKeyManager = PassKeyManager(this)
        passKeyEditText = findViewById(R.id.passKeyInput)
        saveButton = findViewById(R.id.saveButton)

        // Кнопка для установки Pass Key
        saveButton.setOnClickListener {
            val passKey = passKeyEditText.text.toString()
            if (passKey.isNotEmpty()) {
                if (passKey.length < 4) {
                Toast.makeText(this, "Пароль должен быть минимум 4 символа!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
                passKeyManager.setPassKey(passKey)
                savePassword(username, passKey)
                startActivity(Intent(this@CreatePassKeyActivity, AuthActivity::class.java))
                finish()

            } else {
                Toast.makeText(this, "Пароль должен быть минимум 4 символа!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }

    private fun savePassword(username: String, password: String) {
        lifecycleScope.launch {
            val isSuccess = credentialManagerHelper.savePassword(username, password)
            if (isSuccess) {
                Toast.makeText(this@CreatePassKeyActivity, "Пароль сохранен!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@CreatePassKeyActivity, "Ошибка сохранения пароля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBiometry(username: String) {
        lifecycleScope.launch {
            val isSuccess = credentialManagerHelper.saveBiometry(username)
            if (isSuccess) {
                Toast.makeText(this@CreatePassKeyActivity, "Биометрия сохранена!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@CreatePassKeyActivity, "Ошибка сохранения биометрии", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




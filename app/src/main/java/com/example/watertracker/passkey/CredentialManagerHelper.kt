import androidx.credentials.CredentialManager
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.exceptions.CreateCredentialException
import android.app.Activity
import android.util.Base64
import android.util.Log
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.Credential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom


class CredentialManagerHelper(private val activity: Activity) {

    private val credentialManager = CredentialManager.create(activity)

    suspend fun requestSignIn(): Credential? {
        return withContext(Dispatchers.IO) {
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(GetPasswordOption()) // Запрос пароля
                .addCredentialOption(GetPublicKeyCredentialOption(createPasskeyChallenge())) // Запрос Passkey
                .build()

            try {
                val response = credentialManager.getCredential(
                    context = activity,
                    request = request
                )
                response.credential
            } catch (e: GetCredentialException) {
                Log.e("CredentialManagerHelper", "Ошибка получения учетных данных", e)
                null
            }
        }
    }

    // Сохранение пароля
    suspend fun savePassword(username: String, password: String): Boolean {
        return try {
            val request = CreatePasswordRequest(
                id = username,    // Логин пользователя
                password = password // Пароль пользователя
            )
            credentialManager.createCredential(
                context = activity,
                request = request
            )
            true // Пароль успешно сохранен
        } catch (e: CreateCredentialException) {
            Log.e("Passkey", "Ошибка сохранения пароля", e)
            false // Ошибка сохранения пароля
        }
    }


    // Сохранение биометрии (Passkey)
    suspend fun saveBiometry(username: String): Boolean {
        val request = CreatePublicKeyCredentialRequest(
            requestJson = requestJson(username), // Используем JSON-запрос
            clientDataHash = null,  // Оставляем null
            preferImmediatelyAvailableCredentials = true, // Используем уже доступные Passkey
            origin = null, // Оставляем null
            preferDefaultProvider = "com.google.android.gms.credentials", // Google Password Manager
            isAutoSelectAllowed = true // Разрешаем автоматический выбор
        )
        return try {
            credentialManager.createCredential(
                request = request,
                context = activity
            )
            Log.d("CredentialManagerHelper", "Passkey успешно создан")
            true // Успешное сохранение
        } catch (e: CreateCredentialException) {
            when (e) {
                is CreatePublicKeyCredentialDomException -> {
                    Log.e("CredentialManagerHelper", "Ошибка валидации JSON-запроса", e)
                }
                is CreateCredentialUnknownException -> {
                    Log.e("CredentialManagerHelper", "Неизвестная ошибка при создании Passkey", e)
                }
                else -> {
                    Log.e("CredentialManagerHelper", "Другая ошибка при создании Passkey", e)
                }
            }
            false // Ошибка сохранения
        }
    }

    // Генерация JSON-запроса для создания Passkey
    private fun requestJson(username: String): String {
        return """
        {
            "challenge": "${generateRandomChallenge()}",
            "rp": {
                "name": "Water Tracker",
                "id": "com.example.watertracker"
            },
            "user": {
                "id": "${generateUserId(username)}",
                "name": "$username",
                "displayName": "$username"
            },
            "pubKeyCredParams": [
                {
                    "type": "public-key",
                    "alg": -7
                },
                {
                    "type": "public-key",
                    "alg": -257
                }
            ],
            "timeout": 1800000,
            "attestation": "none",
            "excludeCredentials": ${generateExcludeCredentials()},
            "authenticatorSelection": {
                "authenticatorAttachment": "platform",
                "requireResidentKey": true,
                "residentKey": "required",
                "userVerification": "required"
            }
        }
        """.trimIndent()
    }

    // Генерация случайного challenge
    private fun generateRandomChallenge(): String {
        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes)
        return Base64.encodeToString(randomBytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    // Генерация user ID
    private fun generateUserId(username: String): String {
        val byteArray = username.toByteArray(Charsets.UTF_8)
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    // Генерация excludeCredentials
    private fun generateExcludeCredentials(): String {
        val excludeCredentials = listOf(
            mapOf("id" to "ghi789", "type" to "public-key"),
            mapOf("id" to "jkl012", "type" to "public-key")
        )
        return excludeCredentials.joinToString(",", "[", "]") {
            """{"id": "${Base64.encodeToString(it["id"]!!.toByteArray(), Base64.NO_WRAP)}", "type": "${it["type"]}"}"""
        }
    }

    // Генерация challenge для запроса Passkey
    private fun createPasskeyChallenge(): String {
        return """
        {
            "challenge": "${generateRandomChallenge()}",
            "rpId": "credential-manager-test.example.com",
            "allowCredentials": [],
            "userVerification": "required"
        }
        """.trimIndent()
    }

}

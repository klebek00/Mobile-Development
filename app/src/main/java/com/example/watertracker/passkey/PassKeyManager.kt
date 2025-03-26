package com.example.app

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class PassKeyManager(private val context: Context) {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val alias = "pass_key_alias_fin"

    fun isPassKeySet(): Boolean {
        val sharedPreferences = context.getSharedPreferences("app_pref_fin", Context.MODE_PRIVATE)
        return sharedPreferences.contains("encrypted_pass_key_fin")
    }

    fun setPassKey(passKey: String) {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()
        )
        keyGenerator.generateKey()

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(alias, null) as SecretKey)
        val encryptedPassKey = cipher.doFinal(passKey.toByteArray())

        val sharedPreferences = context.getSharedPreferences("app_pref_fin", Context.MODE_PRIVATE)
        val encodedPassKey = Base64.encodeToString(encryptedPassKey, Base64.DEFAULT)
        val encodedIV = Base64.encodeToString(cipher.iv, Base64.DEFAULT)
        sharedPreferences.edit().apply {
            putString("encrypted_pass_key_fin", encodedPassKey)
            putString("encrypted_iv", encodedIV)
        }.apply()
    }

    fun validatePassKey(enteredPassKey: String): Boolean {
        val sharedPreferences = context.getSharedPreferences("app_pref_fin", Context.MODE_PRIVATE)
        val encodedPassKey = sharedPreferences.getString("encrypted_pass_key_fin", null)
        val encodedIV = sharedPreferences.getString("encrypted_iv", null)
        if (encodedPassKey == null || encodedIV == null) {
            return false
        }

        val encryptedPassKey = Base64.decode(encodedPassKey, Base64.DEFAULT)
        val iv = Base64.decode(encodedIV, Base64.DEFAULT)
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, keyStore.getKey(alias, null) as SecretKey, ivParameterSpec)
        val decryptedPassKey = cipher.doFinal(encryptedPassKey)

        return enteredPassKey == String(decryptedPassKey, Charsets.UTF_8)
    }
    fun resetPassKey() {
        // Удаляем зашифрованный Pass Key и IV из SharedPreferences
        val sharedPreferences = context.getSharedPreferences("app_pref_fin", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            remove("encrypted_pass_key_fin")
            remove("encrypted_iv")
        }.apply()

        // Удаляем ключ из Android KeyStore
        try {
            keyStore.deleteEntry(alias)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
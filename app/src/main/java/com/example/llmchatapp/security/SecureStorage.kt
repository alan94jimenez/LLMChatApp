package com.example.llmchatapp.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * A wrapper class for Android's EncryptedSharedPreferences to securely store key-value data.
 * This class handles the initialization of the master key and the SharedPreferences instance.
 *
 * @param context The application context, required to create the master key and SharedPreferences.
 */
class SecureStorage(context: Context) {

    // Generates or gets an existing master key. The key is stored in the Android Keystore.
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    // Initializes EncryptedSharedPreferences, which will be used to store data securely.
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secret_shared_prefs", // A unique name for the preferences file.
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Saves a string value securely.
     * @param key The key under which to store the value.
     * @param value The string value to store.
     */
    fun save(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    /**
     * Retrieves a string value securely.
     * @param key The key of the value to retrieve.
     * @return The stored string value, or null if the key is not found.
     */
    fun get(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}

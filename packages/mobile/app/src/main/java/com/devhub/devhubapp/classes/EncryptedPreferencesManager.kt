package com.devhub.devhubapp.classes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.devhub.devhubapp.activity.WelcomeActivity
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.dataClasses.LoginResponse
import com.devhub.devhubapp.dataClasses.TokenResponse
import com.devhub.devhubapp.dataClasses.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class EncryptedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences
    private lateinit var authAPI: AuthAPI

    init {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "encrypted_preferences",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveUserData(userData: User) {
        saveData("user_id", userData.id)
        saveData("name", userData.name ?: "")
        saveData("username", userData.userName ?: "")
        saveData("email", userData.email ?: "")
        saveData("avatar", userData.avatar ?: "")
        saveData("createdAt", userData.createdAt?.toString() ?: "")
        saveData("devPoints", userData.devPoints.toString())
        saveData("activationCode", userData.activationCode ?: "")
        saveData("isActivated", userData.isActivated.toString())
        saveData("roles", userData.roles?.joinToString(",") ?: "")
    }

    fun getUserData(): User {

        val createdAtLong = sharedPreferences.getLong("createdAt", 0)
        val createdAt = Date(createdAtLong)

        return User(
            id = sharedPreferences.getString("user_id", "") ?: "",
            name = sharedPreferences.getString("name", "") ?: "",
            userName = sharedPreferences.getString("username", "") ?: "",
            email = sharedPreferences.getString("email", "") ?: "",
            avatar = sharedPreferences.getString("avatar", "") ?: "",
            createdAt = createdAt,
            devPoints = sharedPreferences.getString("devPoints", "0")?.toIntOrNull() ?: 0,
            activationCode = sharedPreferences.getString("activationCode", "") ?: "",
            isActivated = sharedPreferences.getString("isActivated", "false")?.toBoolean() ?: false,
            roles = sharedPreferences.getString("roles", "")?.split(",")?.toTypedArray() ?: emptyArray()
        )
    }

    fun saveTokens(access_token: String?, refresh_token: String?){
        saveData("access_token", access_token ?: "")
        saveData("refresh_token", refresh_token ?: "")
    }

    fun getAccessToken(): String?{
        return sharedPreferences.getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun saveData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}
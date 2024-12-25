package com.devhub.devhubapp.classes

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.api.UserAPI
import com.devhub.devhubapp.dataClasses.SavedPostsResponse
import com.devhub.devhubapp.dataClasses.User
import com.devhub.devhubapp.dataClasses.UserReactions
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
        saveData("user_id", userData._id)
        saveData("name", userData.name ?: "")
        saveData("bio", userData.bio ?: "")
        saveData("username", userData.username ?: "")
        saveData("email", userData.email ?: "")
        saveData("avatar", userData.avatar ?: "")
        saveData("createdAt", userData.createdAt.time)
        saveData("devPoints", userData.devPoints.toString())
        saveData("activationCode", userData.activationCode ?: "")
        saveData("isActivated", userData.isActivated.toString())
        saveData("roles", userData.userRole?.joinToString(",") ?: "")
    }

    fun deleteUserData() {
        val editor = sharedPreferences.edit()
        editor.remove("user_id")
        editor.remove("name")
        editor.remove("bio")
        editor.remove("username")
        editor.remove("email")
        editor.remove("avatar")
        editor.remove("createdAt")
        editor.remove("devPoints")
        editor.remove("activationCode")
        editor.remove("isActivated")
        editor.remove("roles")
        editor.remove("access_token")
        editor.remove("refresh_token")
        editor.remove("likedPosts")
        editor.remove("dislikedPosts")
        editor.remove("saved_posts")
        editor.apply()
    }

    fun getUserData(): User {

        val createdAtLong = sharedPreferences.getLong("createdAt", 0L)
        val createdAt = Date(createdAtLong)

        return User(
            _id = sharedPreferences.getString("user_id", "") ?: "",
            name = sharedPreferences.getString("name", "") ?: "",
            bio = sharedPreferences.getString("bio", "") ?: "",
            username = sharedPreferences.getString("username", "") ?: "",
            email = sharedPreferences.getString("email", "") ?: "",
            avatar = sharedPreferences.getString("avatar", "") ?: "",
            createdAt = createdAt,
            devPoints = sharedPreferences.getString("devPoints", "0")?.toIntOrNull() ?: 0,
            activationCode = sharedPreferences.getString("activationCode", "") ?: "",
            isActivated = sharedPreferences.getString("isActivated", "false")?.toBoolean() ?: false,
            userRole = sharedPreferences.getString("roles", "")?.split(",")?.toTypedArray()
                ?: emptyArray()
        )
    }

    fun saveTokens(access_token: String?, refresh_token: String?) {
        saveData("access_token", access_token ?: "")
        saveData("refresh_token", refresh_token ?: "")
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun saveUserReactions(reactions: UserReactions) {
        saveData("likedPosts", reactions.likedPosts?.joinToString(",") ?: "")
        saveData("dislikedPosts", reactions.dislikedPosts?.joinToString(",") ?: "")
    }

    fun getUserReactions(): UserReactions {
        val likedPosts =
            sharedPreferences.getString("likedPosts", "")?.split(",")?.filter { it.isNotBlank() }
                ?: emptyList()
        val dislikedPosts =
            sharedPreferences.getString("dislikedPosts", "")?.split(",")?.filter { it.isNotBlank() }
                ?: emptyList()

        return UserReactions(
            likedPosts = likedPosts,
            dislikedPosts = dislikedPosts
        )
    }

    fun saveData(key: String, value: Any) {
        val editor = sharedPreferences.edit()

        when (value) {
            is String -> editor.putString(key, value)
            is Long -> editor.putLong(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> throw IllegalArgumentException("Unsupported data type")
        }

        editor.apply()
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun fetchAndSaveUserSavedPosts(userAPI: UserAPI) {
        val userId = getUserData()._id
        userAPI.getUserSavedPosts(userId).enqueue(object : Callback<SavedPostsResponse> {
            override fun onResponse(
                call: Call<SavedPostsResponse>,
                response: Response<SavedPostsResponse>
            ) {
                if (response.isSuccessful) {
                    val savedPosts = response.body()?.savedPosts ?: emptyList()
                    saveUserSavedPosts(savedPosts)
                }
            }

            override fun onFailure(call: Call<SavedPostsResponse>, t: Throwable) {
                Log.e("EncryptedPreferencesManager", "Error fetching user saved posts", t)
            }
        })
    }

    fun saveUserSavedPosts(savedPosts: List<String>) {
        val savedPostsString = savedPosts.joinToString(",")
        sharedPreferences.edit().putString("saved_posts", savedPostsString).apply()
    }

    fun getUserSavedPosts(): List<String> {
        val savedPostsString = sharedPreferences.getString("saved_posts", "") ?: ""
        return if (savedPostsString.isNotEmpty()) {
            savedPostsString.split(",")
        } else {
            emptyList()
        }
    }
}

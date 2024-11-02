package com.devhub.devhubapp.interceptors

import android.util.Log
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.dataClasses.TokenResponse
import com.devhub.devhubapp.dataClasses.User
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AuthInterceptor(
    private val authAPI: AuthAPI,
    private val encryptedPreferencesManager: EncryptedPreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val accessToken = encryptedPreferencesManager.getAccessToken()

        val requestWithAuth = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        var response = chain.proceed(requestWithAuth)

        if (response.code == 401) {
            val refreshToken = encryptedPreferencesManager.getRefreshToken()
            if (refreshToken != null) {
                val newAccessToken = refreshAccessToken(refreshToken)

                if (newAccessToken != null) {
                    response.close()
                    Log.i("AuthInterceptor", "New Access Token: $newAccessToken")
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                    response = chain.proceed(newRequest)
                }
            }
        }
        return response
    }

    private fun refreshAccessToken(refreshToken: String): String? {
        return try {
            runBlocking {
                val refreshResponse = authAPI.refreshToken(refreshToken).awaitResponse()

                if (refreshResponse.isSuccessful) {
                    val body = refreshResponse.body()
                    if (body != null) {
                        val userResponse = body.user
                        val accessToken = body.accessToken
                        val newRefreshToken = body.refreshToken

                        val user = User(
                            id = userResponse.id,
                            name = userResponse.name ?: "",
                            userName = userResponse.userName,
                            avatar = userResponse.avatar,
                            email = userResponse.email,
                            createdAt = userResponse.createdAt.toString().toDate(),
                            devPoints = userResponse.devPoints,
                            activationCode = userResponse.activationCode,
                            isActivated = userResponse.isActivated,
                            roles = arrayOf("user")
                        )

                        encryptedPreferencesManager.saveUserData(user)
                        encryptedPreferencesManager.saveTokens(accessToken, newRefreshToken)

                        Log.i("AuthInterceptor", "Token refreshed successfully")
                        accessToken
                    } else {
                        Log.e("AuthInterceptor", "Failed to parse response body")
                        null
                    }
                } else {
                    Log.e("AuthInterceptor", "Failed to refresh token: ${refreshResponse.message()}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Error refreshing token: ${e.message}")
            null
        }
    }

    private fun String.toDate(): Date {
        val format = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("GMT")
        return format.parse(this) ?: Date()
    }
}

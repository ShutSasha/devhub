package com.devhub.devhubapp.classes

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import android.content.Context
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.api.PostAPI
import com.devhub.devhubapp.interceptors.AuthInterceptor
import com.google.gson.GsonBuilder
import java.util.Date
import java.util.concurrent.TimeUnit

class RetrofitClient private constructor(context: Context) {
    private val BASE_URL = "http://10.0.2.2:5295/api/"

    private var retrofit: Retrofit
    private val encryptedPreferencesManager = EncryptedPreferencesManager(context)
    private val authAPI: AuthAPI
    val postAPI: PostAPI

    init {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateDeserializer())
            .create()

        val baseRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        authAPI = baseRetrofit.create(AuthAPI::class.java)

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(authAPI, encryptedPreferencesManager))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        postAPI = retrofit.create(PostAPI::class.java)
    }

    fun getRetrofit(): Retrofit {
        return retrofit
    }

    companion object {
        @Volatile
        private var instance: RetrofitClient? = null

        fun getInstance(context: Context): RetrofitClient {
            return instance ?: synchronized(this) {
                instance ?: RetrofitClient(context).also { instance = it }
            }
        }
    }
}

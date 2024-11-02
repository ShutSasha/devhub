package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.LoginResponse
import com.devhub.devhubapp.dataClasses.TokenResponse
import com.devhub.devhubapp.dataClasses.RegistrationRequest
import com.devhub.devhubapp.dataClasses.User
import com.devhub.devhubapp.dataClasses.LoginRequest
import com.devhub.devhubapp.dataClasses.RegistrationResponse
import com.devhub.devhubapp.dataClasses.VerifyEmailRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthAPI {

    @POST("auth/register")
    fun register(@Body user: RegistrationRequest): Call<RegistrationResponse>

    @POST("auth/verify-email")
    fun verifyEmail(@Body user: VerifyEmailRequest): Call<ResponseBody>

    @POST("auth/login")
    fun login(@Body user: LoginRequest): Call<LoginResponse>

    @POST("auth/refresh")
    fun refreshToken(
        @Header("X-Refresh-Token") refreshToken: String
    ): Call<TokenResponse>

    @GET("auth/testinfo")
    fun testinfo(
        @Header("Authorization") accessToken: String
    ): Call<ResponseBody>

}
package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.ChangePasswordRequest
import com.devhub.devhubapp.dataClasses.LoginRequest
import com.devhub.devhubapp.dataClasses.LoginResponse
import com.devhub.devhubapp.dataClasses.PasswordVerificationRequest
import com.devhub.devhubapp.dataClasses.RegistrationRequest
import com.devhub.devhubapp.dataClasses.RegistrationResponse
import com.devhub.devhubapp.dataClasses.TokenResponse
import com.devhub.devhubapp.dataClasses.VerifyEmailRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthAPI {

    @POST("auth/register")
    fun register(@Body registrationRequest: RegistrationRequest): Call<RegistrationResponse>

    @POST("auth/verify-email")
    fun verifyEmail(@Body verifyEmailRequest: VerifyEmailRequest): Call<ResponseBody>

    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @PATCH("auth/password-verification-code")
    fun passwordVerificationCode(@Body passwordVerificationRequest: PasswordVerificationRequest): Call<ResponseBody>

    @PATCH("auth/change-password")
    fun changePassword(@Body changePassword: ChangePasswordRequest): Call<ResponseBody>

    @POST("auth/refresh")
    fun refreshToken(
        @Header("X-Refresh-Token") refreshToken: String
    ): Call<TokenResponse>

    @GET("auth/testinfo")
    fun testinfo(
        @Header("Authorization") accessToken: String
    ): Call<ResponseBody>

}
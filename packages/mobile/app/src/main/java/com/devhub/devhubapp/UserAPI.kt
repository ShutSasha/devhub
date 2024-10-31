package com.devhub.devhubapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("auth/register")
    fun register(@Body user: UserRegistrationRequest): Call<User>

}
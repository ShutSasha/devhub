package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.UserDetail
import com.devhub.devhubapp.dataClasses.UserReactions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserAPI {

    @GET("users/user-details/{userId}")
    fun getUserDetail(@Path("userId") id: String): Call<UserDetail>

    @GET("users/user-reactions/{userId}")
    fun getUserReactions(@Path("userId") userId: String): Call<UserReactions>
}
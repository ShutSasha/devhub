package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.Post
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PostAPI {
    @GET("posts")
    suspend fun getPosts(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1
    ): List<Post>
}
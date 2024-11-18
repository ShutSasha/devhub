package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.Post
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PostAPI {
    @GET("posts")
    suspend fun getPosts(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1
    ): List<Post>

    @Multipart
    @POST("posts")
    fun createPost(
        @Part("userId") userId: RequestBody,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part headerImage: MultipartBody.Part?
    ): Call<Post>
}
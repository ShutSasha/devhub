package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.dataClasses.UserIdRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
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

    @Multipart
    @PATCH("posts/{id}")
    fun updatePost(
        @Path("id") postId: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part headerImage: MultipartBody.Part?
    ): Call<Post>

    @DELETE("posts/{id}")
    fun deletePost(
        @Path("id") postId: String
    ): Call<Void>

    @GET("posts/{id}")
    fun getPostById(
        @Path("id") postId: String
    ): Call<Post>

    @POST("posts/{id}/like")
    fun likePost(
        @Path("id") postId: String,
        @Body requestBody: UserIdRequest
    ): Call<Post>

    @POST("posts/{id}/dislike")
    fun dislikePost(
        @Path("id") postId: String,
        @Body requestBody: UserIdRequest
    ): Call<Post>

    @GET("posts/search")
    suspend fun searchPosts(
        @Query("q") query: String?,
        @Query("tags[]") tags: List<String>?,
        @Query("sort") sort: String?,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): List<Post>

    @GET("posts/get-popular-tags")
    suspend fun getTopTags(
        @Query("limit") limit: Int = 10
    ): List<String>
}
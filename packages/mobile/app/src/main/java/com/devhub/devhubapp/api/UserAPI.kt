package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.EditProfileRequest
import com.devhub.devhubapp.dataClasses.EditProfileResponse
import com.devhub.devhubapp.dataClasses.FollowRequest
import com.devhub.devhubapp.dataClasses.UpdatePhotoResponse
import com.devhub.devhubapp.dataClasses.UserDetail
import com.devhub.devhubapp.dataClasses.UserReactions
import com.devhub.devhubapp.dataClasses.Follower
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.dataClasses.SavedPostDetailsResponse
import com.devhub.devhubapp.dataClasses.SavedPostRequest
import com.devhub.devhubapp.dataClasses.SavedPostsResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserAPI {

    @GET("users/user-details/{userId}")
    fun getUserDetail(@Path("userId") id: String): Call<UserDetail>

    @PATCH("users")
    fun editUserProfile(@Body userData: EditProfileRequest): Call<EditProfileResponse>

    @POST("users/update-photo/{userId}")
    fun updatePhoto(
        @Path("userId") id: String,
        @Body file: MultipartBody
    ): Call<UpdatePhotoResponse>

    @GET("users/user-reactions/{userId}")
    fun getUserReactions(@Path("userId") userId: String): Call<UserReactions>

    @GET("users/is-following")
    fun isFollowing(
        @Query("userId") userId: String?,
        @Query("targetUserId") targetUserId: String
    ): Call<Boolean>

    @POST("users/user-followings")
    fun follow(
        @Body usersId: FollowRequest
    ): Call<ResponseBody>

    @DELETE("users/user-followings")
    fun unfollow(
        @Query("userId") userId: String?,
        @Query("followingUserId") followingUserId: String?,
    ): Call<ResponseBody>

    @GET("users/user-followings/{userId}")
    fun getFollowings(
        @Path("userId") id: String?,
    ): Call<List<Follower>>

    @GET("users/user-followers/{userId}")
    fun getFollowers(
        @Path("userId") id: String?,
    ): Call<List<Follower>>

    @POST("users/saved-posts")
    fun toggleSavePost(@Body requestBody: SavedPostRequest): Call<Post>

    @GET("users/saved-posts/{userId}")
    fun getUserSavedPosts(@Path("userId") userId: String): Call<SavedPostsResponse>

    @GET("users/saved-posts-details/{userId}")
    fun getSavedPosts(@Path("userId") userId: String): Call<List<SavedPostDetailsResponse>>
}
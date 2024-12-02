package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.EditProfileRequest
import com.devhub.devhubapp.dataClasses.EditProfileResponse
import com.devhub.devhubapp.dataClasses.UpdatePhotoResponse
import com.devhub.devhubapp.dataClasses.UserDetail
import com.devhub.devhubapp.dataClasses.UserReactions
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

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
}
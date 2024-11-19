package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.AddCommentRequest
import com.devhub.devhubapp.dataClasses.Comment
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CommentAPI {
    @POST("comments")
    fun createComment(
        @Body commentRequest: AddCommentRequest
    ): Call<Comment>
}


package com.devhub.devhubapp.api

import com.devhub.devhubapp.dataClasses.ReportRequest
import com.devhub.devhubapp.dataClasses.ReportResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportAPI {
    @POST("reports")
    fun sendReport(@Body report: ReportRequest): Call<ReportResponse>

    @GET("reports/{userId}")
    fun getUserReports(@Path("userId") userId: String): Call<List<ReportResponse>>
}
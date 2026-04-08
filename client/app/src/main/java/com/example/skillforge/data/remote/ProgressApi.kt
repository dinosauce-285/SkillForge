package com.example.skillforge.data.remote

import com.example.skillforge.domain.model.ActiveCourse
import retrofit2.http.GET
import retrofit2.http.Header

interface ProgressApi {
    @GET("progress/dashboard")
    suspend fun getDashboardProgress(
        @Header("Authorization") token: String
    ): List<ActiveCourse>
}
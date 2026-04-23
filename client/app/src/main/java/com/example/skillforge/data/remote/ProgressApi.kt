package com.example.skillforge.data.remote

import com.example.skillforge.domain.model.HomeDashboard
import retrofit2.http.GET

interface ProgressApi {
    /**
     * Fetches dashboard data. 
     * Authorization header is automatically handled by OkHttp Interceptor.
     */
    @GET("progress/dashboard")
    suspend fun getDashboardProgress(): HomeDashboard
}

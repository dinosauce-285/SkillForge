package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.ProgressApi
import com.example.skillforge.domain.model.HomeDashboard
import com.example.skillforge.domain.repository.ProgressRepository

class ProgressRepositoryImpl(
    private val progressApi: ProgressApi
) : ProgressRepository {

    /**
     * Fetches the dashboard data from the backend.
     * The token is handled automatically by the OkHttp Interceptor.
     */
    override suspend fun getDashboardProgress(token: String): HomeDashboard {
        // We ignore the token parameter here as the Interceptor provides it globally
        return progressApi.getDashboardProgress()
    }
}

package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.ProgressApi
import com.example.skillforge.domain.model.ActiveCourse
import com.example.skillforge.domain.repository.ProgressRepository

class ProgressRepositoryImpl(
    private val progressApi: ProgressApi
) : ProgressRepository {

    override suspend fun getDashboardProgress(token: String): List<ActiveCourse> {
        return progressApi.getDashboardProgress("Bearer $token")
    }
}
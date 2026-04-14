package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.HomeDashboard

interface ProgressRepository {
    suspend fun getDashboardProgress(token: String): HomeDashboard
}
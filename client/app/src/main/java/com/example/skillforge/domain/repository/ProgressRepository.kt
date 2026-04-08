package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.ActiveCourse

interface ProgressRepository {
    suspend fun getDashboardProgress(token: String): List<ActiveCourse>
}
package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
}
package com.example.skillforge.domain.repository

import com.example.skillforge.data.remote.CategoryDto

interface CategoryRepository {
    suspend fun getCategories(): Result<List<CategoryDto>>
}
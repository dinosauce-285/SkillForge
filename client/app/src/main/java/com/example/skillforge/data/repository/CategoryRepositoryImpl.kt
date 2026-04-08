package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.CategoryApi
import com.example.skillforge.domain.model.Category
import com.example.skillforge.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val api: CategoryApi
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = api.getCategories()

            // translated comment
            if (response.isSuccessful && response.body() != null) {
                // translated comment
                val categories = response.body()!!.map { dto ->
                    Category(
                        id = dto.id,
                        name = dto.name
                    )
                }
                Result.success(categories)
            } else {
                Result.failure(Exception("Failed to load categories: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}

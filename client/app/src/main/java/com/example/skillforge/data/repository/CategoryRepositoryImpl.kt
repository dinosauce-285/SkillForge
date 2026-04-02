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

            // Xử lý mở hộp Response chuẩn mực
            if (response.isSuccessful && response.body() != null) {
                // Map từ CategoryDto sang Category
                val categories = response.body()!!.map { dto ->
                    Category(
                        id = dto.id,
                        name = dto.name
                    )
                }
                Result.success(categories)
            } else {
                Result.failure(Exception("Lỗi tải danh mục: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi mạng: ${e.message}"))
        }
    }
}
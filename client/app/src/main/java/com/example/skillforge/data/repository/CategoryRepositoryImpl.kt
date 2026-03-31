package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.CategoryApi
import com.example.skillforge.data.remote.CategoryDto
import com.example.skillforge.data.remote.CourseApi
import com.example.skillforge.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val api: CategoryApi
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<CategoryDto>> {
        return try {
            // Lấy thẳng data từ API, không cần isSuccessful hay body() gì cả
            val categories = api.getCategories()
            Result.success(categories)
        } catch (e: Exception) {
            // Bất kỳ lỗi mạng, lỗi 404, 500 nào cũng sẽ bị tóm gọn ở đây
            Result.failure(e)
        }
    }
}
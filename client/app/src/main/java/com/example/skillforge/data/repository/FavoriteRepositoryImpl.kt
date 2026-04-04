package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.FavoriteApi
import com.example.skillforge.domain.model.FavoriteCourse
import com.example.skillforge.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl(
    private val api: FavoriteApi,
) : FavoriteRepository {

    override suspend fun getFavorites(token: String): Result<List<FavoriteCourse>> {
        return try {
            val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.getFavorites(bearerToken)

            if (response.isSuccessful && response.body() != null) {
                Result.success(
                    response.body()!!.map { item ->
                        FavoriteCourse(
                            id = item.course.id,
                            title = item.course.title,
                            instructorName = item.course.instructor.fullName,
                            price = item.course.price,
                            isFree = item.course.isFree,
                            thumbnailUrl = item.course.thumbnailUrl,
                        )
                    },
                )
            } else {
                Result.failure(Exception("Failed to load favorites"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to load favorites"))
        }
    }
}

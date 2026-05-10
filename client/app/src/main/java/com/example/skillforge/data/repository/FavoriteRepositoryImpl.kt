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

    override suspend fun addFavorite(token: String, courseId: String): Result<Unit> {
        return try {
            val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.addFavorite(bearerToken, mapOf("courseId" to courseId))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to add to wishlist"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to add to wishlist"))
        }
    }

    override suspend fun removeFavorite(token: String, courseId: String): Result<Unit> {
        return try {
            val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.removeFavorite(bearerToken, courseId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove from wishlist"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to remove from wishlist"))
        }
    }
}

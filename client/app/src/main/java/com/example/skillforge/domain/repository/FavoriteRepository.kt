package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.FavoriteCourse

interface FavoriteRepository {
    suspend fun getFavorites(token: String): Result<List<FavoriteCourse>>
    suspend fun addFavorite(token: String, courseId: String): Result<Unit>
    suspend fun removeFavorite(token: String, courseId: String): Result<Unit>
}

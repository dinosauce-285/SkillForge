package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.FavoriteCourse

interface FavoriteRepository {
    suspend fun getFavorites(token: String): Result<List<FavoriteCourse>>
}

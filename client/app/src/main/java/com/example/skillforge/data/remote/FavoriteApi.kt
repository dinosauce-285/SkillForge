package com.example.skillforge.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

data class FavoriteInstructorDto(
    val id: String,
    val fullName: String,
)

data class FavoriteCategoryDto(
    val id: String,
    val name: String,
)

data class FavoriteCourseDto(
    val id: String,
    val title: String,
    val price: Double,
    val isFree: Boolean,
    val thumbnailUrl: String?,
    val instructor: FavoriteInstructorDto,
    val category: FavoriteCategoryDto,
)

data class FavoriteItemDto(
    val id: String,
    val courseId: String,
    val course: FavoriteCourseDto,
)

interface FavoriteApi {
    @GET("favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String,
    ): Response<List<FavoriteItemDto>>

    @retrofit2.http.POST("favorites")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @retrofit2.http.Body body: Map<String, String>,
    ): Response<FavoriteItemDto>

    @retrofit2.http.DELETE("favorites/{courseId}")
    suspend fun removeFavorite(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("courseId") courseId: String,
    ): Response<Unit>
}

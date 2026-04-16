package com.example.skillforge.data.remote

import com.example.skillforge.data.remote.dto.ProfileResponseDTO
import com.example.skillforge.data.remote.dto.UpdateProfileRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Header

interface UserApi {
    @GET("users/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponseDTO>

    @PATCH("users/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: UpdateProfileRequestDTO
    ): Response<ProfileResponseDTO>
}
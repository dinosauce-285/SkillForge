package com.example.skillforge.data.remote

import com.example.skillforge.data.remote.dto.AvatarResponseDTO
import com.example.skillforge.data.remote.dto.ProfileResponseDTO
import com.example.skillforge.data.remote.dto.UpdateProfileRequestDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("users/profile")
    suspend fun getProfile(
    ): Response<ProfileResponseDTO>

    @PATCH("users/profile")
    suspend fun updateProfile(
        @Body body: UpdateProfileRequestDTO
    ): Response<ProfileResponseDTO>

    @Multipart
    @POST("users/profile/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): Response<AvatarResponseDTO>
}

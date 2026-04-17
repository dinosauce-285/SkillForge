package com.example.skillforge.data.repository

import com.example.skillforge.data.mapper.toDomain
import com.example.skillforge.data.remote.UserApi
import com.example.skillforge.data.remote.dto.UpdateProfileRequestDTO
import com.example.skillforge.domain.model.User
import com.example.skillforge.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepositoryImpl(
    private val userApi: UserApi
) : UserRepository {
    override suspend fun getProfile(): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getProfile()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body.toDomain())
                    } else {
                        Result.failure(Exception("Response body is empty"))
                    }
                } else {
                    Result.failure(Exception("Error fetching profile: HTTP ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateProfile(
        fullName: String?,
        avatarUrl: String?,
        skills: List<String>?,
        learningGoals: String?
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val requestDto = UpdateProfileRequestDTO(
                    fullName = fullName,
                    avatarUrl = avatarUrl,
                    skills = skills,
                    learningGoals = learningGoals
                )

                val response = userApi.updateProfile(requestDto)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body.toDomain())
                    } else {
                        Result.failure(Exception("Response body is empty"))
                    }
                } else {
                    Result.failure(Exception("Error updating profile: HTTP ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun uploadAvatar(
        imageBytes: ByteArray,
        fileName: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", fileName, requestFile)

                val response = userApi.uploadAvatar(body)

                if (response.isSuccessful) {
                    val avatarResponse = response.body()
                    if (avatarResponse != null) {
                        Result.success(avatarResponse.url)
                    } else {
                        Result.failure(Exception("Upload successful but URL is missing"))
                    }
                } else {
                    Result.failure(Exception("Error uploading avatar: HTTP ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

package com.example.skillforge.data.repository

import com.example.skillforge.data.mapper.toDomain
import com.example.skillforge.data.remote.UserApi
import com.example.skillforge.data.remote.dto.UpdateProfileRequestDTO
import com.example.skillforge.domain.model.User
import com.example.skillforge.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val userApi: UserApi
) : UserRepository {
    override suspend fun getProfile(token: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getProfile("Bearer $token")

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
        token: String,
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

                val response = userApi.updateProfile("Bearer $token", requestDto)

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
}
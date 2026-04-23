package com.example.skillforge.data.repository

import android.util.Log
import com.example.skillforge.data.remote.AuthApi
import com.example.skillforge.data.remote.LoginRequest
import com.example.skillforge.data.remote.RegisterRequest
import com.example.skillforge.domain.model.AuthSession
import com.example.skillforge.domain.model.AuthUser
import com.example.skillforge.domain.repository.AuthRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.skillforge.data.local.AuthPreferences
import com.example.skillforge.data.remote.UserInfo

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val supabase: SupabaseClient
) : AuthRepository {

    override val sessionFlow: Flow<AuthSession?> = supabase.auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> {
                val session = status.session
                val userMetadata = session.user?.userMetadata
                AuthSession(
                    accessToken = session.accessToken,
                    user = AuthUser(
                        id = session.user?.id ?: "",
                        email = session.user?.email ?: "",
                        fullName = userMetadata?.get("full_name")?.toString() ?: "User",
                        role = "UNKNOWN" 
                    )
                )
            }
            else -> null
        }
    }

    override suspend fun verifySession(): Result<AuthSession> {
        return try {
            val session = supabase.auth.currentSessionOrNull()
            if (session != null) {
                val response = api.getMe()
                if (response.isSuccessful && response.body() != null) {
                    val userInfo = response.body()!!
                    
                    // DEBUG LOG: Verify full info received from Backend
                    Log.d("AuthRepository", "--- Backend Data Received ---")
                    Log.d("AuthRepository", "ID: ${userInfo.id}")
                    Log.d("AuthRepository", "Email: ${userInfo.email}")
                    Log.d("AuthRepository", "Full Name: ${userInfo.fullName}")
                    Log.d("AuthRepository", "Role: ${userInfo.role}")
                    Log.d("AuthRepository", "-----------------------------")

                    Result.success(
                        AuthSession(
                            accessToken = session.accessToken,
                            user = AuthUser(
                                id = userInfo.id,
                                email = userInfo.email,
                                fullName = userInfo.fullName,
                                role = userInfo.role
                            )
                        )
                    )
                } else {
                    Log.e("AuthRepository", "Verify session failed with code: ${response.code()}")
                    if (response.code() == 401) {
                        Result.failure(Exception("Unauthorized: Token rejected by backend"))
                    } else {
                        Result.failure(Exception("Backend verification failed"))
                    }
                }
            } else {
                Result.failure(Exception("No active session"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Network error in verifySession: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<AuthSession> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                Result.success(
                    AuthSession(
                        accessToken = data.accessToken,
                        user = AuthUser(
                            id = data.user.id,
                            email = data.user.email,
                            fullName = data.user.fullName,
                            role = data.user.role,
                        ),
                    )
                )
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Unable to connect to server"))
        }
    }

    override suspend fun register(fullName: String, email: String, password: String): Result<String> {
        try {
            val response = api.register(RegisterRequest(email, password, fullName))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                return Result.success(data.message)
            } else {
                val errorBody = response.errorBody()?.string() ?: "No details"
                return Result.failure(Exception("Server rejected request: Code ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            return Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun loginWithGoogle() {
        supabase.auth.signInWith(Google)
    }

    private fun parseErrorMessage(rawError: String?): String {
        if (rawError.isNullOrBlank()) return "Login failed"
        return runCatching {
            val json = Gson().fromJson(rawError, JsonObject::class.java)
            json.get("message")?.asString ?: "Login failed"
        }.getOrElse { "Login failed" }
    }

    override suspend fun getMe(): Result<UserInfo> {
        return try {
            val response = api.getMe()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load account details: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.example.skillforge.data.repository

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

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val supabase: SupabaseClient
) : AuthRepository {

    override val sessionFlow: Flow<AuthSession?> = supabase.auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> {
                val session = status.session
                AuthSession(
                    accessToken = session.accessToken,
                    user = AuthUser(
                        id = session.user?.id ?: "",
                        email = session.user?.email ?: "",
                        fullName = session.user?.userMetadata?.get("full_name")?.toString() ?: "User",
                        role = "STUDENT" // translated comment
                    )
                )
            }
            else -> null
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
            return Result.failure(Exception("Network/Crash error: ${e.message}"))
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
}

package com.example.skillforge.core.network

import com.example.skillforge.data.local.AuthPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject

/**
 * Global OkHttp Authenticator handling 401 Unauthorized errors automatically.
 * It intercepts the failed request, attempts to refresh the access token using the stored refresh token,
 * explicitly making a synchronous call to the server, and then retries the failed request.
 */
class TokenAuthenticator(
    private val authPreferences: AuthPreferences
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loops if the refresh endpoint itself throws 401
        if (response.request.url.encodedPath.contains("auth/refresh")) {
            return null
        }

        // Fetch refresh token from DataStore (synchronously)
        val refreshToken = runBlocking {
            authPreferences.refreshToken.firstOrNull()
        }

        // If no refresh token is stored, force logoout and abort
        if (refreshToken.isNullOrEmpty()) {
            runBlocking { authPreferences.clearSession() }
            return null
        }

        // Execute synchronous API call to refresh tokens
        val newAccessToken = refreshTokens(refreshToken)

        return if (newAccessToken != null) {
            // Success: Retry the original request with the fresh token
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        } else {
            // Failure: Token expired or invalid, destroy session
            runBlocking { authPreferences.clearSession() }
            null
        }
    }

    /**
     * Executes an isolated network request to refresh the tokens.
     */
    private fun refreshTokens(refreshToken: String): String? {
        return try {
            val client = OkHttpClient()
            val url = "http://10.0.2.2:3000/auth/refresh"
            
            // The refresh token is transmitted via the Authorization header using Bearer scheme
            val request = Request.Builder()
                .url(url)
                // POST method essentially requires an empty body
                .post("".toRequestBody("application/json".toMediaTypeOrNull())) 
                .header("Authorization", "Bearer $refreshToken")
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    val newAccessToken = jsonObject.getString("accessToken")
                    val newRefreshToken = jsonObject.getString("refreshToken")

                    // Store new tokens persistently
                    runBlocking {
                        authPreferences.saveTokens(newAccessToken, newRefreshToken)
                    }
                    newAccessToken
                } else null
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

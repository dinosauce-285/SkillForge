package com.example.skillforge.core.network

import com.example.skillforge.data.local.AuthPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor responsible for attaching the Authorization Bearer token to all outgoing requests.
 */
class AuthInterceptor(
    private val authPreferences: AuthPreferences
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Synchronously retrieve the current access token
        // runBlocking is necessary here because intercept runs on a background OkHttp thread
        val accessToken = runBlocking {
            authPreferences.accessToken.firstOrNull()
        }

        if (!accessToken.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}

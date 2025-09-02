package com.example.talkeys_new.screens.events

import com.example.talkeys_new.screens.authentication.TokenManager
import com.example.talkeys_new.utils.Result
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import android.util.Log

/**
 * Interceptor that adds authentication headers to outgoing requests
 * Handles token retrieval errors gracefully
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    
    private val TAG = "AuthInterceptor"
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip authentication for certain endpoints if needed
        if (shouldSkipAuth(originalRequest)) {
            return chain.proceed(originalRequest)
        }
        
        // Get token and add to request
        val authenticatedRequest = runBlocking {
            val tokenResult = tokenManager.getToken()
            
            when (tokenResult) {
                is Result.Success -> {
                    val token = tokenResult.data
                    if (!token.isNullOrEmpty()) {
                        Log.d(TAG, "Adding auth header with token")
                        originalRequest.newBuilder()
                            .addHeader("Authorization", "Bearer $token")
                            .build()
                    } else {
                        Log.w(TAG, "No token available, proceeding without authentication")
                        originalRequest
                    }
                }
                is Result.Error -> {
                    Log.e(TAG, "Error retrieving token: ${tokenResult.message}")
                    // Proceed with the original request without the auth header
                    // The server will reject it if authentication is required
                    originalRequest
                }
                else -> {
                    Log.w(TAG, "Unexpected token result state, proceeding without authentication")
                    originalRequest
                }
            }
        }
        
        return try {
            chain.proceed(authenticatedRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error during request execution", e)
            throw e
        }
    }
    
    /**
     * Determines if authentication should be skipped for certain requests
     * @param request The original request
     * @return true if auth should be skipped, false otherwise
     */
    private fun shouldSkipAuth(request: Request): Boolean {
        val path = request.url.encodedPath
        
        // Skip authentication for login, signup, and public endpoints
        return path.contains("/login") || 
               path.contains("/signup") || 
               path.contains("/verify") ||
               path.contains("/public/")
    }
}

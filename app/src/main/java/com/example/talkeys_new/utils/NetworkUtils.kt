package com.example.talkeys_new.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Utility class for network operations and connectivity monitoring
 */
class NetworkUtils(private val context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Checks if the device is currently connected to the internet
     * @return true if connected, false otherwise
     */
    fun isNetworkAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }

    /**
     * Provides a Flow that emits network connectivity status changes
     * @return Flow<Boolean> that emits true when connected, false when disconnected
     */
    fun observeNetworkConnectivity(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Initial value
        trySend(isNetworkAvailable())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    companion object {
        /**
         * Safely executes a network call and wraps the response in a Result
         * @param apiCall The suspend function that makes the API call
         * @return Result<T> representing success, error, or loading state
         */
        suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
            return try {
                val response = apiCall()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.Success(body)
                    } else {
                        Result.Error(
                            exception = IOException("Empty response body"),
                            message = "Server returned empty response",
                            code = response.code()
                        )
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    Result.Error(
                        exception = HttpException(response),
                        message = getErrorMessage(response.code(), errorMsg),
                        code = response.code()
                    )
                }
            } catch (e: IOException) {
                Result.Error(
                    exception = e,
                    message = "Network error: Please check your internet connection"
                )
            } catch (e: HttpException) {
                Result.Error(
                    exception = e,
                    message = getErrorMessage(e.code(), e.message()),
                    code = e.code()
                )
            } catch (e: Exception) {
                Result.Error(
                    exception = e,
                    message = "An unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }

        /**
         * Maps HTTP error codes to user-friendly error messages
         * @param code HTTP status code
         * @param fallbackMsg Fallback message if no specific message is defined for the code
         * @return User-friendly error message
         */
        private fun getErrorMessage(code: Int, fallbackMsg: String): String {
            return when (code) {
                400 -> "Bad request: The server couldn't understand the request"
                401 -> "Unauthorized: Please log in again"
                403 -> "Forbidden: You don't have permission to access this resource"
                404 -> "Not found: The requested resource doesn't exist"
                408 -> "Request timeout: Please try again"
                429 -> "Too many requests: Please wait before trying again"
                500 -> "Server error: Something went wrong on our end"
                502 -> "Bad gateway: The server is temporarily unavailable"
                503 -> "Service unavailable: The server is currently unavailable"
                504 -> "Gateway timeout: The server took too long to respond"
                else -> fallbackMsg
            }
        }
    }
}
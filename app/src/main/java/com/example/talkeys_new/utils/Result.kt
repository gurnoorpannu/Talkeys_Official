package com.example.talkeys_new.utils

import java.io.IOException

/**
 * A sealed class that represents the result of an operation that can succeed, fail, or be in progress.
 * This is used throughout the application for consistent error handling and state management.
 *
 * @param <R> The type of data returned on success
 */
sealed class Result<out R> {

    /**
     * Represents a successful operation with data
     * @param data The successful result data
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation with error information
     * @param exception The underlying exception that caused the error
     * @param message User-friendly error message
     * @param code Optional error code (HTTP status code, etc.)
     */
    data class Error(
        val exception: Exception,
        val message: String = exception.localizedMessage ?: "Unknown error",
        val code: Int? = null
    ) : Result<Nothing>()

    /**
     * Represents an ongoing operation
     */
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception, message=$message, code=$code]"
            Loading -> "Loading"
        }
    }

    /**
     * Returns true if this is a Success
     */
    val isSuccess: Boolean get() = this is Success<*>

    /**
     * Returns true if this is an Error
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns true if this is Loading
     */
    val isLoading: Boolean get() = this == Loading

    /**
     * Returns the data if this is a Success or null otherwise
     */
    fun getOrNull(): R? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Returns the error message if this is an Error or null otherwise
     */
    fun errorMessageOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }

    /**
     * Maps the success value using the given transform function
     */
    inline fun <T> map(transform: (R) -> T): Result<T> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
            Loading -> Loading
        }
    }

    /**
     * Executes the given block if this is a Success
     */
    inline fun onSuccess(action: (R) -> Unit): Result<R> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Executes the given block if this is an Error
     */
    inline fun onError(action: (Error) -> Unit): Result<R> {
        if (this is Error) action(this)
        return this
    }

    /**
     * Executes the given block if this is Loading
     */
    inline fun onLoading(action: () -> Unit): Result<R> {
        if (this is Loading) action()
        return this
    }

    companion object {
        /**
         * Creates an Error result with a generic exception
         * @param message The error message
         * @param code Optional error code
         */
        fun <T> error(message: String, code: Int? = null): Result<T> {
            return Error(IOException(message), message, code)
        }

        /**
         * Creates an Error result for authentication failures
         * @param message The error message
         */
        fun <T> authError(message: String = "Authentication failed"): Result<T> {
            return Error(SecurityException(message), message, 401)
        }

        /**
         * Creates an Error result for network failures
         * @param message The error message
         */
        fun <T> networkError(message: String = "Network error occurred"): Result<T> {
            return Error(IOException(message), message)
        }

        /**
         * Creates an Error result for validation failures
         * @param message The error message
         */
        fun <T> validationError(message: String): Result<T> {
            return Error(IllegalArgumentException(message), message, 400)
        }
    }
}
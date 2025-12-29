package com.talkeys.shared.network

import com.talkeys.shared.config.ProductionConfig
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = !ProductionConfig.IS_PRODUCTION
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = if (ProductionConfig.IS_DEBUG_LOGGING_ENABLED) LogLevel.INFO else LogLevel.NONE
        }
        
        // Note: Timeout configuration removed due to compatibility issues
        // Timeouts can be configured per request if needed
    }
    
    companion object {
        // PRODUCTION CONFIGURATION - Uses centralized config
        val BASE_URL = ProductionConfig.getApiBaseUrl()
        
        // Validation
        init {
            require(BASE_URL.isNotBlank()) { "API Base URL cannot be blank" }
            // Allow HTTP for local development, require HTTPS for production
            if (ProductionConfig.IS_PRODUCTION) {
                require(BASE_URL.startsWith("https://")) { "API Base URL must use HTTPS in production" }
            } else {
                require(BASE_URL.startsWith("http://") || BASE_URL.startsWith("https://")) { 
                    "API Base URL must use HTTP or HTTPS" 
                }
            }
        }
    }
}

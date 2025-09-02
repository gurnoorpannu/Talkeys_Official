package com.example.talkeys_new.utils

/**
 * Application-wide constants for Talkeys
 * This file contains all the constant values used throughout the application
 * for consistency and easy maintenance.
 */
object Constants {

    // API Configuration
    object Api {
        const val BASE_URL = "https://your-api-base-url.com/api/"
        const val TIMEOUT_CONNECT = 30L
        const val TIMEOUT_READ = 30L
        const val TIMEOUT_WRITE = 30L
    }

    // Authentication
    object Auth {
        const val TOKEN_KEY = "auth_token"
        const val TOKEN_EXPIRY_KEY = "auth_token_expiry"
        const val TOKEN_EXPIRY_HOURS = 24L
    }

    // Event Constants
    object Events {
        const val DEFAULT_PAGE_SIZE = 20
        const val MAX_EVENT_NAME_LENGTH = 100
        const val MAX_DESCRIPTION_LENGTH = 1000
        const val MIN_TICKET_PRICE = 0
        const val MAX_TICKET_PRICE = 100000

        // Event Types
        const val TYPE_REGISTERED = "registered"
        const val TYPE_BOOKMARKED = "bookmarked"
        const val TYPE_HOSTED = "hosted"

        // Event Status
        const val STATUS_PAST = "past"
        const val STATUS_UPCOMING = "upcoming"

        // Time Periods
        const val PERIOD_1_MONTH = "1m"
        const val PERIOD_6_MONTHS = "6m"
        const val PERIOD_1_YEAR = "1y"
    }

    // UI Constants
    object UI {
        const val ANIMATION_DURATION_SHORT = 200
        const val ANIMATION_DURATION_MEDIUM = 500
        const val ANIMATION_DURATION_LONG = 1000
        const val DEBOUNCE_DELAY = 300L

        // Dimensions
        const val CARD_ELEVATION = 8
        const val CORNER_RADIUS = 8
        const val DIVIDER_THICKNESS = 1

        // Colors (as hex strings for documentation)
        const val PRIMARY_COLOR = "#7A2EC0"
        const val SECONDARY_COLOR = "#8A44CB"
        const val BACKGROUND_COLOR = "#000000"
        const val SURFACE_COLOR = "#171717"
        const val ERROR_COLOR = "#FF3333"
        const val SUCCESS_COLOR = "#4CAF50"
    }

    // Validation
    object Validation {
        const val MIN_PASSWORD_LENGTH = 8
        const val MAX_PASSWORD_LENGTH = 128
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 50

        // Regex patterns
        const val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
        const val PHONE_PATTERN = "^[+]?[1-9]\\d{1,14}$"
    }

    // Error Messages
    object ErrorMessages {
        const val NETWORK_ERROR = "Please check your internet connection and try again"
        const val GENERIC_ERROR = "Something went wrong. Please try again"
        const val AUTH_ERROR = "Authentication failed. Please login again"
        const val VALIDATION_ERROR = "Please check your input and try again"
        const val NOT_FOUND_ERROR = "The requested resource was not found"
        const val SERVER_ERROR = "Server error. Please try again later"
    }

    // Success Messages
    object SuccessMessages {
        const val PROFILE_UPDATED = "Profile updated successfully"
        const val EVENT_REGISTERED = "Successfully registered for the event"
        const val EVENT_CREATED = "Event created successfully"
        const val MESSAGE_SENT = "Message sent successfully"
    }

    // Navigation Routes
    object Routes {
        const val LANDING = "landing"
        const val LOGIN = "login"
        const val SIGNUP = "signup"
        const val HOME = "home"
        const val PROFILE = "profile"
        const val EVENTS = "events"
        const val EVENT_DETAIL = "event_detail/{eventId}"
        const val CREATE_EVENT = "create_event"
        const val DASHBOARD = "dashboard"
        const val CONTACT_US = "contact_us"
        const val AVATAR_CUSTOMIZER = "avatar_customizer"
        const val PAYMENT = "payment/{eventId}/{eventName}/{ticketPrice}"
    }

    // File and Storage
    object Storage {
        const val MAX_IMAGE_SIZE_MB = 5
        const val MAX_IMAGE_SIZE_BYTES = MAX_IMAGE_SIZE_MB * 1024 * 1024
        const val ALLOWED_IMAGE_TYPES = "image/jpeg,image/png,image/webp"
        const val CACHE_SIZE_MB = 50L
    }

    // Logging
    object Logging {
        const val NETWORK_TAG = "NetworkUtils"
        const val AUTH_TAG = "Authentication"
        const val EVENTS_TAG = "Events"
        const val DASHBOARD_TAG = "Dashboard"
        const val PROFILE_TAG = "Profile"
    }
}
package com.example.talkeys_new.avatar

/**
 * Constants and configuration for the Avatar system
 * Provides centralized configuration for DiceBear API integration
 */
object AvatarConstants {

    // API Configuration
    const val DICEBEAR_BASE_URL = "https://api.dicebear.com/7.x"
    const val DEFAULT_AVATAR_SIZE = 200
    const val PREVIEW_AVATAR_SIZE = 100
    const val MAX_RETRY_ATTEMPTS = 3
    const val REQUEST_TIMEOUT_MS = 10000L

    // Default fallback values
    const val DEFAULT_STYLE = "avataaars"
    const val DEFAULT_BACKGROUND_COLOR = "b6e3f4"
    const val DEFAULT_USER_NAME = "user"

    /**
     * Supported avatar styles from DiceBear API
     * Ordered by visual appeal and reliability
     */
    val AVATAR_STYLES = listOf(
        "avataaars",           // Most popular, human-like
        "open-peeps",          // Diverse, inclusive
        "personas",            // Professional looking
        "micah",              // Simple, clean
        "pixel-art",          // Retro gaming style
        "pixel-art-neutral",  // Neutral pixel art
        "croodles",           // Hand-drawn style
        "croodles-neutral",   // Neutral hand-drawn
        "bottts",             // Robot/bot style
        "identicon"           // Geometric patterns
    )

    /**
     * Data class representing a background color option
     * @param name Human-readable color name
     * @param value Hex color value (without #)
     * @param isLight Whether this is considered a light color (for contrast)
     */
    data class BackgroundColor(
        val name: String,
        val value: String,
        val isLight: Boolean = true
    ) {
        /**
         * Get the color as an Android Color int
         */
        fun toColorInt(): Int {
            return try {
                android.graphics.Color.parseColor("#$value")
            } catch (e: IllegalArgumentException) {
                android.graphics.Color.parseColor("#$DEFAULT_BACKGROUND_COLOR")
            }
        }

        /**
         * Validate if the hex color value is properly formatted
         */
        fun isValidHex(): Boolean {
            return value.matches(Regex("^[a-fA-F0-9]{6}$"))
        }
    }

    /**
     * Predefined background color options
     * Carefully selected for good contrast and visual appeal
     */
    val BACKGROUND_COLORS = listOf(
        BackgroundColor("Light Blue", "b6e3f4", true),
        BackgroundColor("Soft Pink", "ffd5dc", true),
        BackgroundColor("Lavender", "d1d4f9", true),
        BackgroundColor("Peach", "f4b6c2", true),
        BackgroundColor("Mint Green", "a0e7e5", true),
        BackgroundColor("Cream", "f5f5dc", true),
        BackgroundColor("Light Gray", "e8e8e8", true),
        BackgroundColor("Soft Yellow", "fff9c4", true),
        BackgroundColor("Light Coral", "f08080", false),
        BackgroundColor("Pale Green", "98fb98", true)
    )

    // SharedPreferences configuration
    const val AVATAR_PREFS = "avatar_preferences"
    const val KEY_AVATAR_STYLE = "avatarStyle"
    const val KEY_AVATAR_BG = "avatarBg"
    const val KEY_USER_NAME = "userName"
    const val KEY_SEED_MODIFIER = "seedModifier"
    const val KEY_LAST_UPDATE = "lastUpdate"
    const val KEY_CACHE_VERSION = "cacheVersion"

    // URL generation parameters
    private const val URL_PARAM_SEED = "seed"
    private const val URL_PARAM_BACKGROUND = "backgroundColor"
    private const val URL_PARAM_SIZE = "size"
    private const val URL_PARAM_FORMAT = "png"

    /**
     * Validates if an avatar style is supported
     * @param style The style to validate
     * @return true if the style is supported, false otherwise
     */
    fun isValidStyle(style: String?): Boolean {
        return style != null && AVATAR_STYLES.contains(style)
    }

    /**
     * Validates if a background color value is valid
     * @param colorValue The hex color value to validate
     * @return true if valid, false otherwise
     */
    fun isValidBackgroundColor(colorValue: String?): Boolean {
        return colorValue != null &&
                colorValue.matches(Regex("^[a-fA-F0-9]{6}$")) &&
                BACKGROUND_COLORS.any { it.value == colorValue }
    }

    /**
     * Sanitizes a seed string for URL generation
     * Removes special characters and ensures URL safety
     * @param input The input string to sanitize
     * @return Sanitized string suitable for URL generation
     */
    fun sanitizeSeed(input: String?): String {
        return input?.lowercase()
            ?.replace(Regex("[^a-z0-9]"), "")
            ?.take(50) // Limit length to prevent URL issues
            ?.ifBlank { DEFAULT_USER_NAME }
            ?: DEFAULT_USER_NAME
    }

    /**
     * Generates a complete avatar URL with proper error handling
     * @param style Avatar style (validated)
     * @param seed Seed for avatar generation (sanitized)
     * @param backgroundColor Background color (validated)
     * @param size Image size in pixels
     * @return Complete avatar URL
     */
    fun generateAvatarUrl(
        style: String,
        seed: String,
        backgroundColor: String,
        size: Int = DEFAULT_AVATAR_SIZE
    ): String {
        val validStyle = if (isValidStyle(style)) style else DEFAULT_STYLE
        val validBackground = if (isValidBackgroundColor(backgroundColor)) backgroundColor else DEFAULT_BACKGROUND_COLOR
        val sanitizedSeed = sanitizeSeed(seed)
        val validSize = size.coerceIn(16, 512) // Reasonable size limits

        return buildString {
            append(DICEBEAR_BASE_URL)
            append("/")
            append(validStyle)
            append("/")
            append(URL_PARAM_FORMAT)
            append("?")
            append("$URL_PARAM_SEED=$sanitizedSeed")
            append("&$URL_PARAM_BACKGROUND=$validBackground")
            append("&$URL_PARAM_SIZE=$validSize")
        }
    }

    /**
     * Gets a fallback background color if the provided one is invalid
     * @param colorValue The color value to validate
     * @return Valid BackgroundColor object
     */
    fun getValidBackgroundColor(colorValue: String?): BackgroundColor {
        return BACKGROUND_COLORS.find { it.value == colorValue }
            ?: BACKGROUND_COLORS.first()
    }

    /**
     * Cache configuration
     */
    object Cache {
        const val MAX_MEMORY_CACHE_SIZE = 50 * 1024 * 1024 // 50MB
        const val MAX_DISK_CACHE_SIZE = 100 * 1024 * 1024L // 100MB
        const val CACHE_EXPIRY_HOURS = 24
    }

    /**
     * Network configuration
     */
    object Network {
        const val CONNECT_TIMEOUT_SECONDS = 10L
        const val READ_TIMEOUT_SECONDS = 30L
        const val WRITE_TIMEOUT_SECONDS = 30L
        const val MAX_CONCURRENT_REQUESTS = 5
    }
}
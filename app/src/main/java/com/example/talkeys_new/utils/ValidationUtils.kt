package com.example.talkeys_new.utils

import java.util.regex.Pattern

/**
 * Utility class for input validation throughout the application
 * Provides consistent validation logic with detailed error messages
 */
object ValidationUtils {

    private val emailPattern = Pattern.compile(Constants.Validation.EMAIL_PATTERN)
    private val phonePattern = Pattern.compile(Constants.Validation.PHONE_PATTERN)

    /**
     * Validation result containing success status and error message
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    ) {
        companion object {
            fun success() = ValidationResult(true)
            fun error(message: String) = ValidationResult(false, message)
        }
    }

    /**
     * Validates email address format
     * @param email The email address to validate
     * @return ValidationResult with validation status and error message
     */
    fun validateEmail(email: String?): ValidationResult {
        return when {
            email.isNullOrBlank() -> ValidationResult.error("Email is required")
            !emailPattern.matcher(email)
                .matches() -> ValidationResult.error("Please enter a valid email address")

            else -> ValidationResult.success()
        }
    }

    /**
     * Validates password strength
     * @param password The password to validate
     * @return ValidationResult with validation status and error message
     */
    fun validatePassword(password: String?): ValidationResult {
        return when {
            password.isNullOrBlank() -> ValidationResult.error("Password is required")
            password.length < Constants.Validation.MIN_PASSWORD_LENGTH ->
                ValidationResult.error("Password must be at least ${Constants.Validation.MIN_PASSWORD_LENGTH} characters long")

            password.length > Constants.Validation.MAX_PASSWORD_LENGTH ->
                ValidationResult.error("Password must be less than ${Constants.Validation.MAX_PASSWORD_LENGTH} characters")

            !password.any { it.isUpperCase() } ->
                ValidationResult.error("Password must contain at least one uppercase letter")

            !password.any { it.isLowerCase() } ->
                ValidationResult.error("Password must contain at least one lowercase letter")

            !password.any { it.isDigit() } ->
                ValidationResult.error("Password must contain at least one number")

            else -> ValidationResult.success()
        }
    }

    /**
     * Validates name (first name, last name, display name, etc.)
     * @param name The name to validate
     * @param fieldName The field name for error messages (e.g., "First name", "Display name")
     * @return ValidationResult with validation status and error message
     */
    fun validateName(name: String?, fieldName: String = "Name"): ValidationResult {
        return when {
            name.isNullOrBlank() -> ValidationResult.error("$fieldName is required")
            name.length < Constants.Validation.MIN_NAME_LENGTH ->
                ValidationResult.error("$fieldName must be at least ${Constants.Validation.MIN_NAME_LENGTH} characters long")

            name.length > Constants.Validation.MAX_NAME_LENGTH ->
                ValidationResult.error("$fieldName must be less than ${Constants.Validation.MAX_NAME_LENGTH} characters")

            !name.all { it.isLetter() || it.isWhitespace() || it == '\'' || it == '-' } ->
                ValidationResult.error("$fieldName can only contain letters, spaces, hyphens, and apostrophes")

            else -> ValidationResult.success()
        }
    }

    /**
     * Validates phone number format
     * @param phone The phone number to validate
     * @param isRequired Whether the phone number is required
     * @return ValidationResult with validation status and error message
     */
    fun validatePhone(phone: String?, isRequired: Boolean = false): ValidationResult {
        return when {
            phone.isNullOrBlank() && isRequired -> ValidationResult.error("Phone number is required")
            phone.isNullOrBlank() && !isRequired -> ValidationResult.success()
            !phonePattern.matcher(phone!!)
                .matches() -> ValidationResult.error("Please enter a valid phone number")

            else -> ValidationResult.success()
        }
    }

    /**
     * Validates event name
     * @param eventName The event name to validate
     * @return ValidationResult with validation status and error message
     */
    fun validateEventName(eventName: String?): ValidationResult {
        return when {
            eventName.isNullOrBlank() -> ValidationResult.error("Event name is required")
            eventName.length > Constants.Events.MAX_EVENT_NAME_LENGTH ->
                ValidationResult.error("Event name must be less than ${Constants.Events.MAX_EVENT_NAME_LENGTH} characters")

            eventName.trim().length < 3 ->
                ValidationResult.error("Event name must be at least 3 characters long")

            else -> ValidationResult.success()
        }
    }

    /**
     * Validates event description
     * @param description The description to validate
     * @param isRequired Whether the description is required
     * @return ValidationResult with validation status and error message
     */
    fun validateEventDescription(
        description: String?,
        isRequired: Boolean = false
    ): ValidationResult {
        return when {
            description.isNullOrBlank() && isRequired -> ValidationResult.error("Event description is required")
            description.isNullOrBlank() && !isRequired -> ValidationResult.success()
            description!!.length > Constants.Events.MAX_DESCRIPTION_LENGTH ->
                ValidationResult.error("Description must be less than ${Constants.Events.MAX_DESCRIPTION_LENGTH} characters")

            else -> ValidationResult.success()
        }
    }

    /**
     * Validates ticket price
     * @param price The price to validate
     * @return ValidationResult with validation status and error message
     */
    fun validateTicketPrice(price: String?): ValidationResult {
        return when {
            price.isNullOrBlank() -> ValidationResult.error("Ticket price is required")
            else -> {
                try {
                    val numericPrice = price.toDouble()
                    when {
                        numericPrice < Constants.Events.MIN_TICKET_PRICE ->
                            ValidationResult.error("Price cannot be negative")

                        numericPrice > Constants.Events.MAX_TICKET_PRICE ->
                            ValidationResult.error("Price cannot exceed ₹${Constants.Events.MAX_TICKET_PRICE}")

                        else -> ValidationResult.success()
                    }
                } catch (e: NumberFormatException) {
                    ValidationResult.error("Please enter a valid price")
                }
            }
        }
    }

    /**
     * Validates that passwords match
     * @param password The original password
     * @param confirmPassword The confirmation password
     * @return ValidationResult with validation status and error message
     */
    fun validatePasswordMatch(password: String?, confirmPassword: String?): ValidationResult {
        return when {
            password != confirmPassword -> ValidationResult.error("Passwords do not match")
            else -> ValidationResult.success()
        }
    }

    /**
     * Validates a generic required field
     * @param value The value to validate
     * @param fieldName The field name for error messages
     * @return ValidationResult with validation status and error message
     */
    fun validateRequired(value: String?, fieldName: String): ValidationResult {
        return when {
            value.isNullOrBlank() -> ValidationResult.error("$fieldName is required")
            else -> ValidationResult.success()
        }
    }

    /**
     * Validates multiple fields and returns the first error found
     * @param validations List of validation functions to execute
     * @return ValidationResult with the first error found, or success if all pass
     */
    fun validateAll(vararg validations: () -> ValidationResult): ValidationResult {
        validations.forEach { validation ->
            val result = validation()
            if (!result.isValid) {
                return result
            }
        }
        return ValidationResult.success()
    }
}
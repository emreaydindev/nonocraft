package com.neilb.nonocraft.common

/**
 * What went wrong, as a stable code the native UI turns into localized text.
 * Shared code never carries user-facing error text (DECISIONS.md §14).
 */
enum class ErrorCode {
    // Generic, derived from the transport or the HTTP status.
    NETWORK,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    CONFLICT,
    VALIDATION,
    PAYLOAD_TOO_LARGE,
    RATE_LIMITED,
    SERVER,
    UNKNOWN,

    // Specific, sent by the backend in the error body's `code` or raised by the app itself.
    SESSION_EXPIRED,
    INVALID_CREDENTIALS,
    EMAIL_TAKEN,
    USERNAME_TAKEN,
    WRONG_PASSWORD,
    INVALID_CODE,
    EMAIL_NOT_VERIFIED,
    INSUFFICIENT_CRYSTALS,
    DAILY_LIMIT_REACHED,
    PUZZLE_NOT_FOUND,
    GOOGLE_SIGN_IN_FAILED;

    companion object {
        /** A backend `code` with the same name, or null when the app does not know it. */
        fun fromBackendCode(code: String?): ErrorCode? =
            code?.let { name -> entries.firstOrNull { it.name == name } }

        fun fromHttpStatus(status: Int): ErrorCode = when (status) {
            400, 422 -> VALIDATION
            401 -> UNAUTHORIZED
            403 -> FORBIDDEN
            404 -> NOT_FOUND
            409 -> CONFLICT
            413 -> PAYLOAD_TOO_LARGE
            429 -> RATE_LIMITED
            in 500..599 -> SERVER
            else -> UNKNOWN
        }
    }
}

/**
 * An error with its [code]. [detail] is the raw server or exception text, kept for logs and
 * debugging only; it is never shown to the user.
 */
data class AppError(
    val code: ErrorCode,
    val httpStatus: Int? = null,
    val detail: String? = null,
)

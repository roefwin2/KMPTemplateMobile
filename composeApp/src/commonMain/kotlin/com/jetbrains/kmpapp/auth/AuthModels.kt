package com.jetbrains.kmpapp.auth

/**
 * Simple credentials container passed to the authentication plug-ins.
 */
data class AuthCredentials(
    val username: String,
    val password: String,
)

/**
 * Token returned by a successful authentication.
 */
data class AuthToken(
    val value: String,
    val displayName: String,
)

/**
 * Result of a login attempt.
 */
sealed interface AuthResult {
    data class Success(val token: AuthToken) : AuthResult
    data class Failure(val message: String) : AuthResult
}

/**
 * Lifecycle state of the authentication flow, exposed to the UI.
 */
sealed interface AuthState {
    object LoggedOut : AuthState
    object Loading : AuthState
    data class LoggedIn(val token: AuthToken) : AuthState
    data class Error(val message: String) : AuthState
}

/**
 * Metadata describing an available authentication plug-in.
 */
data class AuthOption(
    val id: String,
    val label: String,
)

package com.jetbrains.kmpapp.auth

/**
 * Contract that mirrors the plug-in based authentication flow used across the backend codebase.
 */
interface AuthPlugin {
    val id: String
    val label: String

    suspend fun authenticate(credentials: AuthCredentials): AuthResult
}

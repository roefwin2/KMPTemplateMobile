package com.jetbrains.kmpapp.auth

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simple in-memory authentication repository used across platforms.
 */
class AuthRepository {
    private val _currentUser = MutableStateFlow<UserSession?>(null)
    val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    suspend fun signIn(email: String, password: String): AuthenticationResult {
        if (email.isBlank() || password.isBlank()) {
            return AuthenticationResult.Error("Email et mot de passe sont requis")
        }
        if (!email.contains('@')) {
            return AuthenticationResult.Error("Veuillez entrer un email valide")
        }
        if (password.length < 6) {
            return AuthenticationResult.Error("Le mot de passe doit contenir au moins 6 caractères")
        }

        // Simulate work such as a network request.
        delay(500)
        val user = UserSession(
            email = email.trim(),
            displayName = email.substringBefore('@').ifBlank { "Utilisateur" }
        )
        _currentUser.value = user
        return AuthenticationResult.Success(user)
    }

    fun signOut() {
        _currentUser.value = null
    }
}

sealed interface AuthenticationResult {
    data class Success(val user: UserSession) : AuthenticationResult
    data class Error(val message: String) : AuthenticationResult
}

data class UserSession(
    val email: String,
    val displayName: String,
)

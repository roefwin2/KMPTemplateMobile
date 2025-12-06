package com.jetbrains.kmpapp.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class User(val email: String)

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthRepository {
    val currentUser: StateFlow<User?>

    suspend fun signIn(email: String, password: String): AuthResult

    suspend fun signUp(email: String, password: String): AuthResult
}

class InMemoryAuthRepository : AuthRepository {
    private val credentials = mutableMapOf<String, String>()
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun signIn(email: String, password: String): AuthResult {
        val storedPassword = credentials[email]
        return if (storedPassword == password) {
            val user = User(email)
            _currentUser.value = user
            AuthResult.Success(user)
        } else {
            AuthResult.Error("Identifiants invalides. Vérifiez votre email et mot de passe.")
        }
    }

    override suspend fun signUp(email: String, password: String): AuthResult {
        if (credentials.containsKey(email)) {
            return AuthResult.Error("Un compte existe déjà avec cet email.")
        }
        credentials[email] = password
        val user = User(email)
        _currentUser.value = user
        return AuthResult.Success(user)
    }
}

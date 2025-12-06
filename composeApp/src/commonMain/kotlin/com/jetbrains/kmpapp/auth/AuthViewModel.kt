package com.jetbrains.kmpapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private fun isValidEmail(email: String): Boolean =
    email.contains('@') && email.substringAfter('@').contains('.')

private fun validateCredentials(email: String, password: String, confirmPassword: String?, isLogin: Boolean): String? {
    if (email.isBlank() || password.isBlank()) return "Tous les champs sont obligatoires."
    if (!isValidEmail(email)) return "Format d'email invalide."
    if (password.length < 6) return "Le mot de passe doit contenir au moins 6 caractères."
    if (!isLogin && password != confirmPassword) return "Les mots de passe ne correspondent pas."
    return null
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val authenticatedUser: User? = null,
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.update { state ->
                    state.copy(authenticatedUser = user)
                }
            }
        }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value.trim(), errorMessage = null, successMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null, successMessage = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null, successMessage = null) }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                isLoginMode = !it.isLoginMode,
                password = "",
                confirmPassword = "",
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    fun submit() {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password
        val confirmPassword = state.confirmPassword
        val validationError = validateCredentials(email, password, confirmPassword, state.isLoginMode)

        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, successMessage = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = if (state.isLoginMode) {
                repository.signIn(email, password)
            } else {
                repository.signUp(email, password)
            }

            _uiState.update {
                when (result) {
                    is AuthResult.Success -> it.copy(
                        isLoading = false,
                        successMessage = "Bienvenue ${'$'}{result.user.email} !",
                        errorMessage = null,
                        password = "",
                        confirmPassword = "",
                    )

                    is AuthResult.Error -> it.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        successMessage = null,
                    )
                }
            }
        }
    }
}

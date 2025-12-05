package com.jetbrains.kmpapp.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.auth.AuthRepository
import com.jetbrains.kmpapp.auth.AuthenticationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val userFlow = authRepository.currentUser

    private val internalState = MutableStateFlow(AuthUiState())

    val uiState: StateFlow<AuthUiState> = combine(internalState, userFlow) { state, user ->
        state.copy(isAuthenticated = user != null)
    }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = AuthUiState())

    fun onEmailChanged(email: String) {
        internalState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        internalState.update { it.copy(password = password, errorMessage = null) }
    }

    fun signIn() {
        val email = internalState.value.email
        val password = internalState.value.password
        viewModelScope.launch {
            internalState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.signIn(email, password)) {
                is AuthenticationResult.Error -> internalState.update {
                    it.copy(errorMessage = result.message, isLoading = false)
                }

                is AuthenticationResult.Success -> internalState.update {
                    it.copy(isLoading = false, password = "", errorMessage = null, isAuthenticated = true)
                }
            }
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
)

package com.jetbrains.kmpapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthFormState(
    val username: String = "",
    val password: String = "",
    val selectedPluginId: String,
)

data class AuthUiState(
    val username: String,
    val password: String,
    val availablePlugins: List<AuthOption>,
    val selectedPluginId: String,
    val isLoading: Boolean,
    val isAuthenticated: Boolean,
    val userDisplayName: String?,
    val error: String?,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val formState = MutableStateFlow(
        AuthFormState(selectedPluginId = authRepository.availablePlugins.firstOrNull()?.id.orEmpty())
    )

    val uiState: StateFlow<AuthUiState> = combine(formState, authRepository.state) { form, state ->
        AuthUiState(
            username = form.username,
            password = form.password,
            availablePlugins = authRepository.availablePlugins,
            selectedPluginId = form.selectedPluginId,
            isLoading = state is AuthState.Loading,
            isAuthenticated = state is AuthState.LoggedIn,
            userDisplayName = (state as? AuthState.LoggedIn)?.token?.displayName,
            error = (state as? AuthState.Error)?.message,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AuthUiState(
            username = "",
            password = "",
            availablePlugins = authRepository.availablePlugins,
            selectedPluginId = authRepository.availablePlugins.firstOrNull()?.id.orEmpty(),
            isLoading = false,
            isAuthenticated = false,
            userDisplayName = null,
            error = null,
        )
    )

    fun onUsernameChanged(username: String) {
        formState.update { current -> current.copy(username = username) }
    }

    fun onPasswordChanged(password: String) {
        formState.update { current -> current.copy(password = password) }
    }

    fun onPluginSelected(pluginId: String) {
        formState.update { current -> current.copy(selectedPluginId = pluginId) }
    }

    fun login() {
        val credentials = AuthCredentials(
            username = formState.value.username,
            password = formState.value.password,
        )

        viewModelScope.launch {
            authRepository.login(formState.value.selectedPluginId, credentials)
        }
    }

    fun logout() {
        authRepository.logout()
        formState.update { it.copy(password = "") }
    }
}

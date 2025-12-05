package com.jetbrains.kmpapp.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    plugins: List<AuthPlugin>,
) {
    private val pluginsById = plugins.associateBy { it.id }
    private val _state = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    val availablePlugins: List<AuthOption> = plugins.map { AuthOption(it.id, it.label) }

    suspend fun login(pluginId: String, credentials: AuthCredentials) {
        val plugin = pluginsById[pluginId]
        if (plugin == null) {
            _state.value = AuthState.Error("Le plug-in sélectionné est introuvable.")
            return
        }

        _state.value = AuthState.Loading
        _state.value = when (val result = plugin.authenticate(credentials)) {
            is AuthResult.Failure -> AuthState.Error(result.message)
            is AuthResult.Success -> AuthState.LoggedIn(result.token)
        }
    }

    fun logout() {
        _state.value = AuthState.LoggedOut
    }
}

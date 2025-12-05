package com.jetbrains.kmpapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data class SignedOut(val errorMessage: String? = null) : AuthUiState
    data class SignedIn(val user: SessionUser) : AuthUiState
}

data class SessionUser(val label: String, val email: String?)

class AuthViewModel : ViewModel() {
    private val auth = dev.gitlive.firebase.Firebase.auth

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        _uiState.value = auth.currentUser?.toSessionUser()?.let { AuthUiState.SignedIn(it) }
            ?: AuthUiState.SignedOut()
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = runCatching {
                auth.signInWithEmailAndPassword(email, password)
            }.recoverCatching {
                auth.createUserWithEmailAndPassword(email, password)
            }

            result.onSuccess {
                _uiState.value = auth.currentUser?.toSessionUser()?.let { AuthUiState.SignedIn(it) }
                    ?: AuthUiState.SignedOut("Impossible de récupérer l'utilisateur après la connexion")
            }.onFailure { throwable ->
                _uiState.value = AuthUiState.SignedOut(throwable.message)
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { auth.signInAnonymously() }
                .onSuccess {
                    _uiState.value = auth.currentUser?.toSessionUser()?.let { AuthUiState.SignedIn(it) }
                        ?: AuthUiState.SignedOut("Connexion anonyme échouée")
                }
                .onFailure { throwable ->
                    _uiState.value = AuthUiState.SignedOut(throwable.message)
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            _uiState.value = AuthUiState.SignedOut()
        }
    }

    private fun dev.gitlive.firebase.auth.FirebaseUser?.toSessionUser(): SessionUser? = this?.let {
        SessionUser(
            label = displayName.takeUnless { it.isNullOrBlank() } ?: email ?: "Utilisateur",
            email = email,
        )
    }
}

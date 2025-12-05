package com.jetbrains.kmpapp.auth

/**
 * Lightweight demonstration plug-in that validates the credentials locally.
 */
class DemoAuthPlugin : AuthPlugin {
    override val id: String = "demo"
    override val label: String = "Demo plug-in"

    override suspend fun authenticate(credentials: AuthCredentials): AuthResult {
        if (credentials.username.isBlank() || credentials.password.isBlank()) {
            return AuthResult.Failure("Renseignez votre identifiant et votre mot de passe.")
        }

        if (credentials.password.length < 6) {
            return AuthResult.Failure("Le mot de passe doit contenir au moins 6 caractères.")
        }

        if (credentials.password != "letmein") {
            return AuthResult.Failure("Identifiants invalides pour le plug-in de démonstration.")
        }

        return AuthResult.Success(
            AuthToken(
                value = "demo-token-${credentials.username}",
                displayName = credentials.username,
            )
        )
    }
}

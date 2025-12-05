package com.jetbrains.kmpapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jetbrains.kmpapp.auth.AuthUiState
import com.jetbrains.kmpapp.auth.SessionUser
import com.jetbrains.kmpapp.auth.AuthViewModel
import com.jetbrains.kmpapp.screens.auth.AuthScreen
import com.jetbrains.kmpapp.screens.detail.DetailScreen
import com.jetbrains.kmpapp.screens.list.ListScreen
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object ListDestination

@Serializable
data class DetailDestination(val objectId: Int)

@Composable
fun App() {
    val authViewModel = koinViewModel<AuthViewModel>()
    val authState = collectAsStateWithLifecycle(authViewModel.uiState).value

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            when (val state = authState) {
                AuthUiState.Loading -> EmptyStatePlaceholder()
                is AuthUiState.SignedOut -> AuthScreen(
                    state = state,
                    onSignIn = authViewModel::signIn,
                    onAnonymousSignIn = authViewModel::signInAnonymously,
                )
                is AuthUiState.SignedIn -> HomeNavigation(
                    user = state.user,
                    onSignOut = authViewModel::signOut,
                )
            }
        }
    }
}

@Composable
private fun HomeNavigation(
    user: SessionUser,
    onSignOut: () -> Unit,
) {
    val navController: NavHostController = rememberNavController()
    NavHost(navController = navController, startDestination = ListDestination) {
        composable<ListDestination> {
            ListScreen(
                user = user,
                onSignOut = onSignOut,
                navigateToDetails = { objectId ->
                    navController.navigate(DetailDestination(objectId))
                },
            )
        }
        composable<DetailDestination> { backStackEntry ->
            DetailScreen(
                objectId = backStackEntry.toRoute<DetailDestination>().objectId,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun EmptyStatePlaceholder() {
    Surface {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

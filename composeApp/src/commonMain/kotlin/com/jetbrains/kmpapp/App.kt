package com.jetbrains.kmpapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jetbrains.kmpapp.auth.AuthRepository
import com.jetbrains.kmpapp.screens.auth.AuthScreen
import com.jetbrains.kmpapp.screens.detail.DetailScreen
import com.jetbrains.kmpapp.screens.home.HomeScreen
import com.jetbrains.kmpapp.screens.list.ListScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
object AuthDestination

@Serializable
object HomeDestination

@Serializable
object ListDestination

@Serializable
data class DetailDestination(val objectId: Int)

@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            val authRepository: AuthRepository = koinInject()
            val currentUser by authRepository.currentUser.collectAsState()

            if (currentUser == null) {
                AuthScreen(onAuthenticated = {})
            } else {
                val navController: NavHostController = rememberNavController()
                NavHost(navController = navController, startDestination = HomeDestination) {
                    composable<HomeDestination> {
                        HomeScreen(
                            user = currentUser!!,
                            onNavigateToCollection = {
                                navController.navigate(ListDestination)
                            },
                            onLogout = {
                                authRepository.signOut()
                            },
                        )
                    }
                    composable<ListDestination> {
                        ListScreen(navigateToDetails = { objectId ->
                            navController.navigate(DetailDestination(objectId))
                        })
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
        }
    }
}

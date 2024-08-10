package com.example.prestapp.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.prestapp.presentation.MainScreen
import com.example.prestapp.presentation.cliente.ClienteDetalleScreen
import com.example.prestapp.presentation.cliente.ClienteScreen
import com.example.prestapp.presentation.cliente.ClientesScreen
import com.example.prestapp.presentation.prestamo.PrestamoListScreen
import com.example.prestapp.presentation.prestamo.PrestamoScreen
import com.example.prestapp.presentation.ruta.RutaScreen
import com.example.prestapp.presentation.ruta.RutasScreen


@Composable
fun PrestAppNavHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.MainScreen.toString()
    ) {
        composable(
            route = Screen.MainScreen.toString(),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) {
            MainScreen(navController = navHostController)
        }
        composable(
            route = Screen.RutaListScreen.toString(),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) {
            RutasScreen(navController = navHostController)
        }
        composable(
            route = "${Screen.RutaScreen}/{rutaId}",
            arguments = listOf(navArgument("rutaId") { type = NavType.IntType }),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) { backStackEntry ->
            val rutaId = backStackEntry.arguments?.getInt("rutaId") ?: 0
            RutaScreen(
                navController = navHostController,
                rutaId = rutaId
            )
        }
        composable(
            route = Screen.ClienteList.toString(),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) {
            ClientesScreen(navController = navHostController)
        }
        composable(
            route = "${Screen.ClienteRegistro}/{clienteId}",
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType }),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            ClienteScreen(
                navController = navHostController,
                viewModel = hiltViewModel(),
                clienteId = clienteId
            )
        }
        composable(
            route = "${Screen.ClienteDetalle}/{clienteId}",
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType }),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            ClienteDetalleScreen(
                navController = navHostController,
                clienteId = clienteId
            )
        }
        composable(
            route = Screen.PrestamoRegistro.toString(),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) {
            PrestamoScreen(navController = navHostController)
        }

        composable(
            route = Screen.PrestamoPorRuta.toString(),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) {
            PrestamoListScreen(navController = navHostController)
        }
    }
}
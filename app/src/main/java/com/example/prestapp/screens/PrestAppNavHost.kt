package com.example.prestapp.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.example.prestapp.presentation.MainScreen
import com.example.prestapp.presentation.ruta.RutaScreen

@OptIn(ExperimentalAnimationApi::class)
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
            route = Screen.ClientesList.toString(),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) {
            // Aquí debes llamar a tu pantalla de clientes
        }

        composable(
            route = Screen.PrestamoForm.toString(),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) {
            // Aquí debes llamar a tu pantalla de formulario de préstamo
        }

        composable(
            route = Screen.HistorialCobros.toString(),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) {
            // Aquí debes llamar a tu pantalla de historial de cobros
        }

        composable(
            route = Screen.UsuariosList.toString(),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) {
            // Aquí debes llamar a tu pantalla de lista de usuarios
        }

        composable(
            route = Screen.RutaScreen.toString(),
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(700)) + slideOutHorizontally(targetOffsetX = { -it })
            }
        ) {
            RutaScreen(navController = navHostController)
        }
    }
}

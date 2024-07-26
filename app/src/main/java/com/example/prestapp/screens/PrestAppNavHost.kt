package com.example.prestapp.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.prestapp.presentation.MainScreen

@Composable
fun PrestAppNavHost(
    navHostController: NavHostController,)
{
    NavHost(
        navController = navHostController,
        startDestination = Screen.MainScreen
    ) {
        composable<Screen.MainScreen> {
            MainScreen(navController = navHostController)
        }

    }
}
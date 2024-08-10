package com.example.prestapp.presentation.cliente

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.prestapp.presentation.componentes.TabItem
import com.example.prestapp.presentation.componentes.TextAndIconTabs
import com.example.prestapp.screens.Screen


@Composable
fun ClientesScreen(navController: NavHostController) {
    val tabs = listOf(
        TabItem(
            title = "Lista",
            icon = Icons.Default.List,
            content = {
                ClienteListScreen(
                    navController = navController,
                    viewModel = hiltViewModel()
                )
            }
        ),
        TabItem(
            title = "Registro",
            icon = Icons.Default.Add,
            content = {
                ClienteScreen(
                    navController = navController,
                    viewModel = hiltViewModel(),
                    clienteId = 0
                )
            }
        )
    )

    TextAndIconTabs(
        title = "Clientes",
        tabs = tabs,
        onHome = { navController.navigate(Screen.MainScreen.toString()) }
    )
}
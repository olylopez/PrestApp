package com.example.prestapp.presentation.ruta

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.prestapp.presentation.componentes.TabItem
import com.example.prestapp.presentation.componentes.TextAndIconTabs
import com.example.prestapp.screens.Screen

@Composable
fun RutasScreen(navController: NavHostController) {
    val tabs = listOf(
        TabItem(
            title = "Lista",
            icon = Icons.Default.List,
            content = {
                RutaListScreen(
                    navController = navController,
                    onEditRuta = { ruta ->
                        navController.navigate(Screen.RutaScreen(rutaId = ruta.rutaID).toString())
                    }
                )
            }
        ),
        TabItem(
            title = "Registro",
            icon = Icons.Default.Add,
            content = {
                RutaScreen(
                    navController = navController,
                    rutaId = null
                )
            }
        )
    )

    TextAndIconTabs(
        title = "Rutas",
        tabs = tabs,
        onHome = { navController.navigate(Screen.MainScreen.toString()) }
    )
}

package com.example.prestapp.presentation.Components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prestapp.screens.Screen
import kotlinx.coroutines.launch

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    var badgeCount: Double? = null
)

@Composable
fun NavigationDrawer(
    navController: NavHostController,
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val items = listOf(
        NavigationItem(
            title = "Lista Clientes",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Default.AccountCircle
        ),
        NavigationItem(
            title = "Registro de Clientes",
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Default.Info
        ),
        NavigationItem(
            title = "Lista Préstamos",
            selectedIcon = Icons.AutoMirrored.Filled.List,
            unselectedIcon = Icons.AutoMirrored.Filled.List
        ),
        NavigationItem(
            title = "Historial de Cobros",
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Default.History
        ),
        NavigationItem(
            title = "Usuarios",
            selectedIcon = Icons.Filled.PersonAdd,
            unselectedIcon = Icons.Default.PersonAdd
        )
    )
    val selectedItem = remember { mutableStateOf(items[0]) }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (item == selectedItem.value) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(text = item.title)
                        },
                        selected = item == selectedItem.value,
                        onClick = {
                            selectedItem.value = item
                            scope.launch { drawerState.close() }
                            when (item.title) {
                                "Lista Clientes" -> navController.navigate(Screen.ClientesList)
                                "Registro de Clientes" -> navController.navigate(Screen.ClienteForm)
                                "Lista Préstamos" -> navController.navigate(Screen.PrestamosPorRuta)
                                "Historial de Cobros" -> navController.navigate(Screen.HistorialCobros)
                                "Usuarios" -> navController.navigate(Screen.UsuariosList)
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        content()
    }
}

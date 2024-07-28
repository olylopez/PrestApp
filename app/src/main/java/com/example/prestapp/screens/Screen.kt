package com.example.prestapp.screens
import kotlinx.serialization.Serializable

sealed class Screen {

    // Pantalla principal
    @Serializable
    object MainScreen : Screen()

    // Pantalla para listar clientes
    @Serializable
    object ClientesList : Screen()

    // Pantalla para agregar o editar un cliente
    @Serializable
    data class ClienteForm(val clienteId: Int? = null) : Screen()

    // Pantalla para crear un nuevo préstamo
    @Serializable
    object PrestamoForm : Screen()

    // Pantalla para ver el historial de cobros
    @Serializable
    object HistorialCobros : Screen()

    // Pantalla para listar préstamos por ruta
    @Serializable
    data class PrestamosPorRuta(val rutaId: Int) : Screen()

    // Pantalla para registrar y listar usuarios
    @Serializable
    object UsuariosList : Screen()

    // Nueva pantalla de configuración
    @Serializable
    object RutaScreen : Screen()
}

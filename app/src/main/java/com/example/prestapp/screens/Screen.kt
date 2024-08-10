package com.example.prestapp.screens
import kotlinx.serialization.Serializable

sealed class Screen {

    // Pantalla principal
    @Serializable
    object MainScreen : Screen()

    // Pantalla para listar clientes
    @Serializable
    object ClienteList : Screen()

    // Pantalla para agregar o editar un cliente
    @Serializable
    data class ClienteRegistro(val clienteId: Int) : Screen()

    // Pantalla para mostrar detalles del cliente
    @Serializable
    data class ClienteDetalle(val clienteId: Int) : Screen()

    // Pantalla para crear un nuevo préstamo
    @Serializable
    object PrestamoRegistro : Screen()

    @Serializable
    object RutaListScreen : Screen()

    @Serializable
    data class RutaScreen(val rutaId: Int) : Screen()

    @Serializable
    object PrestamoPorRuta : Screen()


    // Pantalla para listar préstamos por ruta
    @Serializable
    data class PrestamosPorRuta(val rutaId: Int) : Screen()

    // Pantalla para ver el historial de cobros
    @Serializable
    object HistorialCobros : Screen()

    // Pantalla para registrar y listar usuarios
    @Serializable
    object UsuariosList : Screen()
}

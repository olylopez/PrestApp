package com.example.prestapp.data.remote.dtos

class HistorialCobrosDto(
    val historialID: Int,
    val fecha: String,
    val rutaID: Int,
    val balanceInicial: Double,
    val totalCobrado: Double,
    val totalPrestamos: Double,
    val gastos: Double,
    val regresoDinero: Double,
    val rutaNombre: String?
)
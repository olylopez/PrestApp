package com.example.prestapp.data.remote.dtos

class PrestamoDto(
    val prestamoID: Int,
    val clienteID: Int,
    val capital: Double,
    val cuotas: Int,
    val interes: Double,
    val montoCuota: Double,
    val montoPagado: Double,
    val estaPagado: Boolean,
    val fechaPrestamo: String,
    val formaPago: String,
    val rutaID: Int
)
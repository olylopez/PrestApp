package com.example.prestapp.data.remote.dtos

import com.example.prestapp.data.local.entities.PrestamoEntity
import java.math.BigDecimal
import java.util.Date

data class PrestamoDto(
    val prestamoID: Int,
    val capital: BigDecimal,
    val cuotas: Int,
    val interes: BigDecimal,
    val montoPagado: BigDecimal,
    val fechaPrestamo: String,
    val formaPago: String,
    val rutaID: Int,
    val cedula: String // Solo necesitas la cedula para identificar al cliente en el servicio
)

fun PrestamoDto.toEntity(clienteID: Int?): PrestamoEntity {
    return PrestamoEntity(
        prestamoID = this.prestamoID,
        clienteID = clienteID ?: 0, // Mantén el clienteID internamente
        capital = this.capital,
        cuotas = this.cuotas,
        interes = this.interes,
        montoPagado = this.montoPagado,
        fechaPrestamo = this.fechaPrestamo,
        formaPago = this.formaPago,
        rutaID = this.rutaID,
        cedula = this.cedula
    )
}

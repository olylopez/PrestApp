package com.example.prestapp.data.remote.dtos

import com.example.prestapp.data.local.entities.PagoEntity
import java.math.BigDecimal

data class PagoDto(
    val pagoID: Int,
    val prestamoID: Int,
    val monto: BigDecimal,
    val fechaPago: String
)

fun PagoDto.toEntity(): PagoEntity {
    return PagoEntity(
        pagoID = this.pagoID,
        prestamoID = this.prestamoID,
        monto = this.monto,
        fechaPago = this.fechaPago
    )
}

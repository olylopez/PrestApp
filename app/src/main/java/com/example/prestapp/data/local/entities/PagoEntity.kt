package com.example.prestapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prestapp.data.remote.dtos.ClienteDto
import com.example.prestapp.data.remote.dtos.PagoDto
import java.math.BigDecimal

@Entity(tableName = "pagos")
data class PagoEntity(
    @PrimaryKey(autoGenerate = true) val pagoID: Int,
    val prestamoID: Int,
    val monto: BigDecimal,
    val fechaPago: String,
    val isPending: Boolean = true,
    val isDeleted: Boolean = false
)

fun PagoEntity.toDto(): PagoDto {
    return PagoDto(
        pagoID = this.pagoID,
        prestamoID = this.prestamoID,
        monto = this.monto,
        fechaPago = this.fechaPago
    )
}
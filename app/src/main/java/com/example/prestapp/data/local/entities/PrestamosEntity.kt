package com.example.prestapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prestapp.data.remote.dtos.PrestamoDto
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "prestamos")
data class PrestamoEntity(
    @PrimaryKey(autoGenerate = true) val prestamoID: Int,
    val clienteID: Int,
    val capital: BigDecimal,
    val cuotas: Int,
    val interes: BigDecimal,
    val montoCuota: BigDecimal = (capital + capital * interes) / BigDecimal(cuotas),
    val montoPagado: BigDecimal,
    val estaPagado: Boolean = montoPagado >= (capital + capital * interes),
    val fechaPrestamo: String,
    val formaPago: String,
    val rutaID: Int,
    val cedula: String, // La cedula se utiliza para identificar al cliente en el servicio
    val isPending: Boolean = true,
    val isDeleted: Boolean = false
)

fun PrestamoEntity.toDto(): PrestamoDto {
    return PrestamoDto(
        prestamoID = this.prestamoID,
        cedula = this.cedula, // Solo la cedula es necesaria en el DTO
        capital = this.capital,
        cuotas = this.cuotas,
        interes = this.interes,
        montoPagado = this.montoPagado,
        fechaPrestamo = this.fechaPrestamo,
        formaPago = this.formaPago,
        rutaID = this.rutaID
    )
}
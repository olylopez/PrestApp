package com.example.prestapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prestapp.data.remote.dtos.ClienteDto
import java.math.BigDecimal

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true)
    val clienteID: Int,
    val nombre: String,
    val apodo: String?,
    val negocioReferencia: String?,
    val direccion: String,
    val telefono: String?,
    val celular: String,
    val cedula: String,
    val foto: String?,
    val balance: Double,
    val estaAlDia: Boolean,
    val isPending: Boolean = true,
    val isDeleted: Boolean = false
)

fun ClienteEntity.toDto() = ClienteDto(
    clienteID = clienteID,
    nombre = nombre,
    apodo = apodo,
    negocioReferencia = negocioReferencia,
    direccion = direccion,
    telefono = telefono,
    celular = celular,
    cedula = cedula,
    foto = foto,
    balance = balance.toBigDecimal(),
    estaAlDia = estaAlDia
)

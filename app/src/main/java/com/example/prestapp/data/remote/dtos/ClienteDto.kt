package com.example.prestapp.data.remote.dtos

import com.example.prestapp.data.local.entities.ClienteEntity
import com.example.prestapp.data.local.entities.RutaEntity
import java.math.BigDecimal

data class ClienteDto(
    val clienteID: Int,
    val nombre: String,
    val apodo: String?,
    val negocioReferencia: String?,
    val direccion: String,
    val telefono: String?,
    val celular: String,
    val cedula: String,
    val foto: String?,
    val balance: BigDecimal,
    val estaAlDia: Boolean
)

fun ClienteDto.toEntity() = ClienteEntity(
    clienteID = clienteID,
    nombre = nombre,
    apodo = apodo,
    negocioReferencia = negocioReferencia,
    direccion = direccion,
    telefono = telefono,
    celular = celular,
    cedula = cedula,
    foto = foto,
    balance = balance.toDouble(),
    estaAlDia = estaAlDia,
    isPending = true
)



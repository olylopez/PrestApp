package com.example.prestapp.data.remote.dtos

import com.example.prestapp.data.local.entities.RutaEntity

data class RutaDto(
    val rutaID: Int,
    val nombre: String,
    val descripcion: String?
)

fun RutaDto.toEntity() = RutaEntity(
    rutaID = rutaID,
    nombre = nombre,
    descripcion = descripcion,
    isPending = true
)
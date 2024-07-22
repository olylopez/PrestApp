package com.example.prestapp.data.remote.dtos

class ClienteDto(
    val clienteID: Int,
    val nombre: String,
    val apodo: String?,
    val negocioReferencia: String?,
    val direccion: String,
    val telefono: String?,
    val celular: String,
    val cedula: String,
    val foto: ByteArray?,
    val balance: Double,
    val estaAlDia: Boolean
)
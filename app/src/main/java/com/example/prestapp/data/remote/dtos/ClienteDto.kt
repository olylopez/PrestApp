package com.example.prestapp.data.remote.dtos

import com.example.prestapp.data.local.entities.RutaEntity

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

/*fun RutaDto.toEntity(): RutaEntity {
    return RutaEntity(
        rutaID = this.rutaID,
        nombre = this.nombre,
        descripcion = this.descripcion
    )
}
fun RutaEntity.toDto(): RutaDto {
    return RutaDto(
        rutaID = this.rutaID,
        nombre = this.nombre,
        descripcion = this.descripcion
    )
}*/
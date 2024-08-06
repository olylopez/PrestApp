package com.example.prestapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prestapp.data.remote.dtos.RutaDto

@Entity(tableName = "rutas")
data class RutaEntity(
    @PrimaryKey(autoGenerate = true)
    val rutaID: Int,
    val nombre: String,
    val descripcion: String?,
    val isPending: Boolean = true,
    val isDeleted: Boolean = false
)

fun RutaEntity.toDto() = RutaDto(
    rutaID = rutaID,
    nombre = nombre,
    descripcion = descripcion
)
package com.example.prestapp.data.local.dao

import androidx.room.*
import com.example.prestapp.data.local.entities.RutaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RutaDao {
    @Query("SELECT * FROM rutas WHERE isDeleted = 0")
    fun getRutas(): Flow<List<RutaEntity>>

    @Query("SELECT * FROM rutas WHERE rutaID = :rutaId")
    fun getRutaById(rutaId: Int): Flow<RutaEntity>

    @Query("SELECT * FROM rutas WHERE isSynced = 0 OR isDeleted = 1")
    fun getUnsyncedRutas(): List<RutaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRuta(ruta: RutaEntity): Long

    @Update
    suspend fun updateRuta(ruta: RutaEntity)

    @Delete
    suspend fun deleteRuta(ruta: RutaEntity)

    @Query("DELETE FROM rutas WHERE rutaID = :rutaId")
    suspend fun deleteRutaById(rutaId: Int)
}

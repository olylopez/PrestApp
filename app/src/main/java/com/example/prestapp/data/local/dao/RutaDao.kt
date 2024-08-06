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

    @Query("SELECT * FROM rutas WHERE isPending = 1 OR isDeleted = 1")
    fun getPendingRutas(): List<RutaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRuta(ruta: RutaEntity): Long

    @Update
    suspend fun updateRuta(ruta: RutaEntity)

    @Query("UPDATE rutas SET rutaID = :newRutaId WHERE rutaID = :oldRutaId")
    suspend fun updateRutaId(oldRutaId: Int, newRutaId: Int)

    @Delete
    suspend fun deleteRuta(ruta: RutaEntity)

    @Query("DELETE FROM rutas WHERE rutaID = :rutaId")
    suspend fun deleteRutaById(rutaId: Int)
}

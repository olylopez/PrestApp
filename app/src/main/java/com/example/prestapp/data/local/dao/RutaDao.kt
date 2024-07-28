package com.example.prestapp.data.local.dao

import androidx.room.*
import com.example.prestapp.data.local.entities.RutaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RutaDao {
    @Query("SELECT * FROM rutas")
    fun getRutas(): Flow<List<RutaEntity>>

    @Query("SELECT * FROM rutas WHERE rutaID = :id")
    fun getRutaById(id: Int): Flow<RutaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRuta(ruta: RutaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutas(rutas: List<RutaEntity>)

    @Query("DELETE FROM rutas")
    suspend fun deleteAllRutas()

    @Update
    suspend fun updateRuta(ruta: RutaEntity)

    @Delete
    suspend fun deleteRuta(ruta: RutaEntity)

    @Query("DELETE FROM rutas WHERE rutaID = :id")
    suspend fun deleteRutaById(id: Int)
}

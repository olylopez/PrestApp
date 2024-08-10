package com.example.prestapp.data.local.dao

import androidx.room.*
import com.example.prestapp.data.local.entities.PrestamoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrestamoDao {

    @Query("SELECT * FROM prestamos WHERE clienteID = :clienteId AND isDeleted = 0")
    fun getPrestamosByClienteId(clienteId: Int): Flow<List<PrestamoEntity>>

    @Query("SELECT * FROM prestamos WHERE isDeleted = 0")
    fun getPrestamos(): Flow<List<PrestamoEntity>>

    @Query("SELECT * FROM prestamos WHERE prestamoID = :prestamoId AND isDeleted = 0")
    fun getPrestamoById(prestamoId: Int): Flow<PrestamoEntity>

    @Query("SELECT * FROM prestamos WHERE cedula = :cedula")
    fun getPrestamosByCedula(cedula: String): Flow<List<PrestamoEntity>>

    @Query("SELECT * FROM prestamos WHERE isPending = 1 OR isDeleted = 1")
    fun getPendingPrestamos(): List<PrestamoEntity>

    @Query("SELECT * FROM prestamos WHERE rutaID = :rutaId")
    fun getPrestamosByRutaId(rutaId: Int): Flow<List<PrestamoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrestamo(prestamo: PrestamoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrestamos(prestamos: List<PrestamoEntity>)

    @Query("DELETE FROM prestamos")
    suspend fun deleteAllPrestamos()

    @Update
    suspend fun updatePrestamo(prestamo: PrestamoEntity)

    @Query("UPDATE prestamos SET prestamoID = :newPrestamoId WHERE prestamoID = :oldPrestamoId")
    suspend fun updatePrestamoId(oldPrestamoId: Int, newPrestamoId: Int)

    @Delete
    suspend fun deletePrestamo(prestamo: PrestamoEntity)

    @Query("DELETE FROM prestamos WHERE prestamoID = :prestamoId")
    suspend fun deletePrestamoById(prestamoId: Int)
}
package com.example.prestapp.data.local.dao

import androidx.room.*
import com.example.prestapp.data.local.entities.PagoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PagoDao {

    @Query("SELECT * FROM pagos")
    fun getPagos(): Flow<List<PagoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPago(pago: PagoEntity)

    @Query("SELECT * FROM pagos WHERE isPending = 1")
    fun getPendingPagos(): List<PagoEntity>

    @Query("DELETE FROM pagos WHERE pagoID = :pagoId")
    suspend fun deletePagoById(pagoId: Int)

    @Update
    suspend fun updatePago(pago: PagoEntity)

    @Delete
    suspend fun deletePago(pago: PagoEntity)

    @Query("SELECT * FROM pagos WHERE prestamoID = :prestamoId")
    fun getPagosByPrestamoId(prestamoId: Int): Flow<List<PagoEntity>>

    @Query("SELECT * FROM pagos WHERE pagoID = :pagoId LIMIT 1")
    suspend fun getPagoById(pagoId: Int): PagoEntity?
}

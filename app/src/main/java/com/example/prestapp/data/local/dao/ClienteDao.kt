package com.example.prestapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.prestapp.data.local.entities.ClienteEntity

@Dao
interface ClienteDao {
    @Query("SELECT clienteID FROM clientes WHERE cedula = :cedula LIMIT 1")
    suspend fun getClienteIdByCedula(cedula: String): Int?

    @Query("SELECT * FROM clientes WHERE isDeleted = 0")
    fun getClientes(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes WHERE clienteID = :clienteId")
    fun getClienteById(clienteId: Int): Flow<ClienteEntity>

    @Query("SELECT * FROM clientes WHERE isPending = 1 OR isDeleted = 1")
    fun getPendingClientes(): List<ClienteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: ClienteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateClientes(clientes: List<ClienteEntity>)

    @Query("DELETE FROM clientes")
    suspend fun deleteAllClients()

    @Update
    suspend fun updateCliente(cliente: ClienteEntity)

    @Query("UPDATE clientes SET clienteID = :newClienteId WHERE clienteID = :oldClienteId")
    suspend fun updateClienteId(oldClienteId: Int, newClienteId: Int)

    @Delete
    suspend fun deleteCliente(cliente: ClienteEntity)

    @Query("DELETE FROM clientes WHERE clienteID = :clienteId")
    suspend fun deleteClienteById(clienteId: Int)
}


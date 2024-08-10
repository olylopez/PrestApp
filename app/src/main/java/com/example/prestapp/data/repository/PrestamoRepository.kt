package com.example.prestapp.data.repository

import android.util.Log
import com.example.prestapp.ConnectivityReceiver
import com.example.prestapp.data.local.dao.ClienteDao
import com.example.prestapp.data.local.dao.PrestamoDao
import com.example.prestapp.data.local.entities.PrestamoEntity
import com.example.prestapp.data.local.entities.toDto
import com.example.prestapp.data.remote.PrestAppApi
import com.example.prestapp.data.remote.dtos.PrestamoDto
import com.example.prestapp.data.remote.dtos.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PrestamoRepository @Inject constructor(
    private val prestamoDao: PrestamoDao,
    private val clienteDao: ClienteDao, // Se inyecta el Dao de Cliente
    private val prestAppApi: PrestAppApi,
    private val connectivityReceiver: ConnectivityReceiver
) {
    private var tempIdCounter = -1

    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        connectivityReceiver.isConnectedFlow.onEach { isConnected ->
            if (isConnected) {
                CoroutineScope(Dispatchers.IO).launch {
                    syncPendingPrestamos()
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun getPrestamos(): Flow<List<PrestamoEntity>> {
        Log.d("PrestamoRepository", "Fetching prestamos from local database")
        return prestamoDao.getPrestamos()
    }

    fun getPrestamosByClienteId(clienteId: Int): Flow<List<PrestamoEntity>> {
        return prestamoDao.getPrestamosByClienteId(clienteId)
    }
    fun getPrestamosByCedula(cedula: String): Flow<List<PrestamoEntity>> {
        return prestamoDao.getPrestamosByCedula(cedula)
    }
    fun getPrestamosByRutaId(rutaId: Int): Flow<List<PrestamoEntity>> {
        return prestamoDao.getPrestamosByRutaId(rutaId)
    }

    fun getPrestamoById(prestamoId: Int): Flow<PrestamoEntity> {
        Log.d("PrestamoRepository", "Fetching prestamo by ID: $prestamoId")
        return prestamoDao.getPrestamoById(prestamoId)
    }

    fun isConnectedFlow(): StateFlow<Boolean> {
        return connectivityReceiver.isConnectedFlow
    }

    suspend fun syncPendingPrestamos() {
        val pendingPrestamos = prestamoDao.getPendingPrestamos()
        Log.d("PrestamoRepository", "Pending prestamos to sync: ${pendingPrestamos.size}")
        pendingPrestamos.forEach { prestamo ->
            try {
                if (prestamo.isDeleted) {
                    Log.d("PrestamoRepository", "Deleting prestamo: ${prestamo.prestamoID}")
                    val response = prestAppApi.deletePrestamo(prestamo.prestamoID)
                    if (response.isSuccessful || response.code() == 404) {
                        prestamoDao.deletePrestamoById(prestamo.prestamoID)
                        Log.d("PrestamoRepository", "Deleted prestamo locally: ${prestamo.prestamoID}")
                    } else {
                        Log.e("PrestamoRepository", "Failed to delete prestamo from server: ${prestamo.prestamoID}, response code: ${response.code()}")
                    }
                } else if (prestamo.isPending) {
                    if (prestamo.prestamoID < 0) {
                        Log.d("PrestamoRepository", "Posting new prestamo")
                        val response = prestAppApi.postPrestamo(prestamo.toDto())
                        if (response.isSuccessful) {
                            val remotePrestamo = response.body()!!
                            prestamoDao.updatePrestamoId(prestamo.prestamoID, remotePrestamo.prestamoID)
                            prestamoDao.updatePrestamo(prestamo.copy(isPending = false, prestamoID = remotePrestamo.prestamoID))
                            Log.d("PrestamoRepository", "Synced new prestamo with server: ${remotePrestamo.prestamoID}")
                        } else {
                            Log.e("PrestamoRepository", "Failed to post new prestamo to server: ${response.code()} - ${response.message()}")
                        }
                    } else {
                        Log.d("PrestamoRepository", "Updating prestamo: ${prestamo.prestamoID}")
                        val response = prestAppApi.putPrestamo(prestamo.prestamoID, prestamo.toDto())
                        if (response.isSuccessful) {
                            prestamoDao.updatePrestamo(prestamo.copy(isPending = false))
                            Log.d("PrestamoRepository", "Synced updated prestamo with server: ${prestamo.prestamoID}")
                        } else {
                            Log.e("PrestamoRepository", "Failed to update prestamo on server: ${prestamo.prestamoID}, response code: ${response.code()} - ${response.message()}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("PrestamoRepository", "Error syncing prestamo ${prestamo.prestamoID}: ${e.localizedMessage}", e)
            }
        }
    }

    suspend fun addPrestamo(prestamoDto: PrestamoDto, clienteID: Int) {
        val entity = prestamoDto.toEntity(clienteID).copy(isPending = true, prestamoID = generateTempId())
        val localId = prestamoDao.insertPrestamo(entity)
        val localEntity = prestamoDao.getPrestamoById(localId.toInt()).first()
        Log.d("PrestamoRepository", "Inserted prestamo locally: $localEntity")

        if (connectivityReceiver.isConnectedFlow.value) {
            try {
                val response = prestAppApi.postPrestamo(prestamoDto)
                if (response.isSuccessful) {
                    val remotePrestamo = response.body()!!
                    prestamoDao.updatePrestamoId(localEntity.prestamoID, remotePrestamo.prestamoID)
                    prestamoDao.updatePrestamo(localEntity.copy(isPending = false, prestamoID = remotePrestamo.prestamoID))
                    Log.d("PrestamoRepository", "Synced new prestamo with server: $remotePrestamo")
                } else {
                    Log.e("PrestamoRepository", "Failed to post new prestamo to server")
                }
            } catch (e: Exception) {
                Log.e("PrestamoRepository", "Error syncing new prestamo: ${e.localizedMessage}", e)
            }
        }
    }

    suspend fun updatePrestamo(prestamoDto: PrestamoDto) {
        val entity = prestamoDto.toEntity(null).copy(isPending = true)
        prestamoDao.updatePrestamo(entity)
        Log.d("PrestamoRepository", "Updated prestamo locally: $entity")

        if (connectivityReceiver.isConnectedFlow.value) {
            try {
                val response = prestAppApi.putPrestamo(entity.prestamoID, prestamoDto)
                if (response.isSuccessful) {
                    prestamoDao.updatePrestamo(entity.copy(isPending = false))
                    Log.d("PrestamoRepository", "Synced updated prestamo with server: ${entity.prestamoID}")
                } else {
                    Log.e("PrestamoRepository", "Failed to update prestamo on server: ${entity.prestamoID}, response code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PrestamoRepository", "Error syncing updated prestamo: ${e.localizedMessage}", e)
            }
        }
    }

    suspend fun deletePrestamo(prestamoId: Int) {
        val prestamoToDelete = prestamoDao.getPrestamoById(prestamoId).firstOrNull()?.copy(isDeleted = true)
        if (prestamoToDelete != null) {
            try {
                prestamoDao.updatePrestamo(prestamoToDelete)
                Log.d("PrestamoRepository", "Marked prestamo as deleted locally: $prestamoId")
            } catch (dbEx: Exception) {
                Log.e("PrestamoRepository", "Failed to mark prestamo as deleted locally: ${dbEx.localizedMessage}", dbEx)
            }

            if (connectivityReceiver.isConnectedFlow.value) {
                try {
                    val response = prestAppApi.deletePrestamo(prestamoId)
                    if (response.isSuccessful || response.code() == 404) {
                        try {
                            prestamoDao.deletePrestamoById(prestamoId)
                            Log.d("PrestamoRepository", "Deleted prestamo from server: $prestamoId")
                        } catch (dbEx: Exception) {
                            Log.e("PrestamoRepository", "Failed to delete prestamo locally: ${dbEx.localizedMessage}", dbEx)
                        }
                    } else {
                        Log.e("PrestamoRepository", "Failed to delete prestamo from server: $prestamoId, response code: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("PrestamoRepository", "Error syncing deleted prestamo: ${e.localizedMessage}", e)
                }
            }
        }
    }

    suspend fun triggerManualSync() {
        syncPendingPrestamos()
    }

    private fun generateTempId(): Int {
        return tempIdCounter--
    }
}
package com.example.prestapp.data.repository

import android.util.Log
import com.example.prestapp.ConnectivityReceiver
import com.example.prestapp.data.local.dao.PagoDao
import com.example.prestapp.data.local.dao.PrestamoDao
import com.example.prestapp.data.local.entities.PagoEntity
import com.example.prestapp.data.remote.PrestAppApi
import com.example.prestapp.data.remote.dtos.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton
import com.example.prestapp.data.local.entities.toDto
import com.example.prestapp.data.remote.dtos.PagoDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Singleton
class PagoRepository @Inject constructor(
    private val pagoDao: PagoDao,
    private val prestamoDao: PrestamoDao,
    private val prestAppApi: PrestAppApi,
    private val connectivityReceiver: ConnectivityReceiver
) {
    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        connectivityReceiver.isConnectedFlow.onEach { isConnected ->
            if (isConnected) {
                CoroutineScope(Dispatchers.IO).launch {
                    syncPendingPagos()
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun getPagosByPrestamoId(prestamoId: Int): Flow<List<PagoEntity>> {
        return pagoDao.getPagosByPrestamoId(prestamoId)
    }

    suspend fun addPago(pagoDto: PagoDto) {
        val entity = pagoDto.toEntity().copy(isPending = true)
        pagoDao.insertPago(entity)
        Log.d("PagoRepository", "Inserted pago locally: $entity")

        // Update the corresponding loan's paid amount
        updatePrestamoMontoPagado(entity.prestamoID, entity.monto)

        if (connectivityReceiver.isConnectedFlow.value) {
            try {
                val response = prestAppApi.postPago(pagoDto)
                if (response.isSuccessful) {
                    pagoDao.updatePago(entity.copy(isPending = false))
                    Log.d("PagoRepository", "Synced pago with server: ${response.body()}")
                } else {
                    Log.e("PagoRepository", "Failed to post new pago to server")
                }
            } catch (e: Exception) {
                Log.e("PagoRepository", "Error syncing new pago: ${e.localizedMessage}", e)
            }
        }
    }

    private suspend fun updatePrestamoMontoPagado(prestamoId: Int, monto: BigDecimal) {
        val prestamo = prestamoDao.getPrestamoById(prestamoId).firstOrNull()
        if (prestamo != null) {
            val nuevoMontoPagado = prestamo.montoPagado.add(monto)
            val estaPagado = nuevoMontoPagado >= prestamo.capital.add(prestamo.interes.multiply(prestamo.capital))
            val updatedPrestamo = prestamo.copy(montoPagado = nuevoMontoPagado, estaPagado = estaPagado)
            prestamoDao.updatePrestamo(updatedPrestamo)
            Log.d("PagoRepository", "Updated prestamo montoPagado: $nuevoMontoPagado, estaPagado: $estaPagado")
        }
    }

    suspend fun syncPendingPagos() {
        val pendingPagos = pagoDao.getPendingPagos()
        Log.d("PagoRepository", "Pending pagos to sync: ${pendingPagos.size}")
        pendingPagos.forEach { pago ->
            try {
                if (pago.isPending) {
                    val response = prestAppApi.postPago(pago.toDto())
                    if (response.isSuccessful) {
                        pagoDao.updatePago(pago.copy(isPending = false))
                        Log.d("PagoRepository", "Synced pago with server: ${pago.pagoID}")
                    } else {
                        Log.e("PagoRepository", "Failed to post pago to server: ${pago.pagoID}")
                    }
                }
            } catch (e: Exception) {
                Log.e("PagoRepository", "Error syncing pago ${pago.pagoID}: ${e.localizedMessage}", e)
            }
        }
    }

    fun getPagos(): Flow<List<PagoEntity>> {
        return pagoDao.getPagos()
    }

    suspend fun updatePago(pagoDto: PagoDto) {
        val pagoEntity = pagoDto.toEntity() // Convert PagoDto to PagoEntity
        pagoDao.updatePago(pagoEntity)
    }

    suspend fun deletePago(pagoId: Int) {
        val pagoToDelete = pagoDao.getPagoById(pagoId)?.copy(isDeleted = true)
        if (pagoToDelete != null) {
            try {
                pagoDao.updatePago(pagoToDelete)
                Log.d("PagoRepository", "Marked pago as deleted locally: $pagoId")
            } catch (dbEx: Exception) {
                Log.e("PagoRepository", "Failed to mark pago as deleted locally: ${dbEx.localizedMessage}", dbEx)
            }

            if (connectivityReceiver.isConnectedFlow.value) {
                try {
                    val response = prestAppApi.deletePago(pagoId)
                    if (response.isSuccessful || response.code() == 404) {
                        try {
                            pagoDao.deletePagoById(pagoId)
                            Log.d("PagoRepository", "Deleted pago from server: $pagoId")
                        } catch (dbEx: Exception) {
                            Log.e("PagoRepository", "Failed to delete pago locally: ${dbEx.localizedMessage}", dbEx)
                        }
                    } else {
                        Log.e("PagoRepository", "Failed to delete pago from server: $pagoId, response code: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("PagoRepository", "Error syncing deleted pago: ${e.localizedMessage}", e)
                }
            }
        }
    }

    suspend fun triggerManualSync() {
        syncPendingPagos()
    }
}

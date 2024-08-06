package com.example.prestapp.data.repository

import android.util.Log
import com.example.prestapp.ConnectivityReceiver
import com.example.prestapp.data.local.dao.RutaDao
import com.example.prestapp.data.local.entities.RutaEntity
import com.example.prestapp.data.remote.PrestAppApi
import com.example.prestapp.data.remote.dtos.RutaDto
import com.example.prestapp.data.remote.dtos.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton
import com.example.prestapp.data.local.entities.toDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Singleton
class RutaRepository @Inject constructor(
    private val rutaDao: RutaDao,
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
                    syncPendingRutas()
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun getRutas(): Flow<List<RutaEntity>> {
        Log.d("RutaRepository", "Fetching rutas from local database")
        return rutaDao.getRutas()
    }

    fun getRutaById(rutaId: Int): Flow<RutaEntity> {
        Log.d("RutaRepository", "Fetching ruta by ID: $rutaId")
        return rutaDao.getRutaById(rutaId)
    }

    fun isConnectedFlow(): StateFlow<Boolean> {
        return connectivityReceiver.isConnectedFlow
    }

    suspend fun syncPendingRutas() {
        val pendingRutas = rutaDao.getPendingRutas()
        Log.d("RutaRepository", "Pending rutas to sync: ${pendingRutas.size}")
        pendingRutas.forEach { ruta ->
            try {
                if (ruta.isDeleted) {
                    Log.d("RutaRepository", "Deleting ruta: ${ruta.rutaID}")
                    val response = prestAppApi.deleteRuta(ruta.rutaID)
                    if (response.isSuccessful || response.code() == 404) {
                        rutaDao.deleteRutaById(ruta.rutaID)
                        Log.d("RutaRepository", "Deleted ruta locally: ${ruta.rutaID}")
                    } else {
                        Log.e("RutaRepository", "Failed to delete ruta from server: ${ruta.rutaID}, response code: ${response.code()}")
                    }
                } else if (ruta.isPending) {
                    if (ruta.rutaID < 0) {
                        Log.d("RutaRepository", "Posting new ruta")
                        val response = prestAppApi.postRuta(ruta.toDto())
                        if (response.isSuccessful) {
                            val remoteRuta = response.body()!!
                            rutaDao.updateRutaId(ruta.rutaID, remoteRuta.rutaID)
                            rutaDao.updateRuta(ruta.copy(isPending = false, rutaID = remoteRuta.rutaID))
                            Log.d("RutaRepository", "Synced new ruta with server: ${remoteRuta.rutaID}")
                        } else {
                            Log.e("RutaRepository", "Failed to post new ruta to server: ${response.code()} - ${response.message()}")
                        }
                    } else {
                        Log.d("RutaRepository", "Updating ruta: ${ruta.rutaID}")
                        val response = prestAppApi.putRuta(ruta.rutaID, ruta.toDto())
                        if (response.isSuccessful) {
                            rutaDao.updateRuta(ruta.copy(isPending = false))
                            Log.d("RutaRepository", "Synced updated ruta with server: ${ruta.rutaID}")
                        } else if (response.code() == 404) {
                            val remoteRuta = findRutaOnServer(ruta.nombre, ruta.descripcion)
                            if (remoteRuta != null) {
                                rutaDao.updateRutaId(ruta.rutaID, remoteRuta.rutaID)
                                rutaDao.updateRuta(ruta.copy(isPending = false, rutaID = remoteRuta.rutaID))
                                Log.d("RutaRepository", "Updated local ID to server ID: ${remoteRuta.rutaID}")
                            } else {
                                Log.e("RutaRepository", "Ruta not found on server, not deleting locally: ${ruta.rutaID}")
                            }
                        } else {
                            Log.e("RutaRepository", "Failed to update ruta on server: ${ruta.rutaID}, response code: ${response.code()} - ${response.message()}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("RutaRepository", "Error syncing route ${ruta.rutaID}: ${e.localizedMessage}", e)
            }
        }
    }

    private suspend fun findRutaOnServer(nombre: String, descripcion: String?): RutaDto? {
        return try {
            val response = prestAppApi.getRutas()
            if (response.isSuccessful) {
                response.body()?.find { it.nombre == nombre && it.descripcion == descripcion }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("RutaRepository", "Error finding ruta on server: ${e.localizedMessage}", e)
            null
        }
    }

    suspend fun addRuta(rutaDto: RutaDto) {
        val entity = rutaDto.toEntity().copy(isPending = true, rutaID = generateTempId())
        val localId = rutaDao.insertRuta(entity)
        val localEntity = rutaDao.getRutaById(localId.toInt()).first()
        Log.d("RutaRepository", "Inserted route locally: $localEntity")

        if (connectivityReceiver.isConnectedFlow.value) {
            try {
                val response = prestAppApi.postRuta(rutaDto)
                if (response.isSuccessful) {
                    val remoteRuta = response.body()!!
                    rutaDao.updateRutaId(localEntity.rutaID, remoteRuta.rutaID)
                    rutaDao.updateRuta(localEntity.copy(isPending = false, rutaID = remoteRuta.rutaID))
                    Log.d("RutaRepository", "Synced new route with server: $remoteRuta")
                } else {
                    Log.e("RutaRepository", "Failed to post new route to server")
                }
            } catch (e: Exception) {
                Log.e("RutaRepository", "Error syncing new route: ${e.localizedMessage}", e)
            }
        }
    }

    suspend fun updateRuta(rutaDto: RutaDto) {
        val entity = rutaDto.toEntity().copy(isPending = true)
        rutaDao.updateRuta(entity)
        Log.d("RutaRepository", "Updated route locally: $entity")

        if (connectivityReceiver.isConnectedFlow.value) {
            try {
                val response = prestAppApi.putRuta(entity.rutaID, rutaDto)
                if (response.isSuccessful) {
                    rutaDao.updateRuta(entity.copy(isPending = false))
                    Log.d("RutaRepository", "Synced updated route with server: ${entity.rutaID}")
                } else if (response.code() == 404) {
                    val remoteRuta = findRutaOnServer(entity.nombre, entity.descripcion)
                    if (remoteRuta != null) {
                        rutaDao.updateRutaId(entity.rutaID, remoteRuta.rutaID)
                        rutaDao.updateRuta(entity.copy(isPending = false, rutaID = remoteRuta.rutaID))
                        Log.d("RutaRepository", "Updated local ID to server ID: ${remoteRuta.rutaID}")
                    } else {
                        Log.e("RutaRepository", "Ruta not found on server, not deleting locally: ${entity.rutaID}")
                    }
                } else {
                    Log.e("RutaRepository", "Failed to update route on server: ${entity.rutaID}, response code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("RutaRepository", "Error syncing updated route: ${e.localizedMessage}", e)
            }
        }
    }

    suspend fun deleteRuta(rutaId: Int) {
        val rutaToDelete = rutaDao.getRutaById(rutaId).firstOrNull()?.copy(isDeleted = true)
        if (rutaToDelete != null) {
            try {
                rutaDao.updateRuta(rutaToDelete)
                Log.d("RutaRepository", "Marked route as deleted locally: $rutaId")
            } catch (dbEx: Exception) {
                Log.e("RutaRepository", "Failed to mark route as deleted locally: ${dbEx.localizedMessage}", dbEx)
            }

            if (connectivityReceiver.isConnectedFlow.value) {
                try {
                    val response = prestAppApi.deleteRuta(rutaId)
                    if (response.isSuccessful || response.code() == 404) {
                        try {
                            rutaDao.deleteRutaById(rutaId)
                            Log.d("RutaRepository", "Deleted route from server: $rutaId")
                        } catch (dbEx: Exception) {
                            Log.e("RutaRepository", "Failed to delete ruta locally: ${dbEx.localizedMessage}", dbEx)
                        }
                    } else {
                        Log.e("RutaRepository", "Failed to delete route from server: $rutaId, response code: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("RutaRepository", "Error syncing deleted route: ${e.localizedMessage}", e)
                }
            }
        }
    }

    suspend fun triggerManualSync() {
        syncPendingRutas()
    }

    private fun generateTempId(): Int {
        return tempIdCounter--
    }
}


sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
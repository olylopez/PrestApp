package com.example.prestapp.data.repository

import android.util.Log
import com.example.prestapp.ConnectivityReceiver
import com.example.prestapp.data.local.dao.ClienteDao
import com.example.prestapp.data.local.entities.ClienteEntity
import com.example.prestapp.data.remote.PrestAppApi
import com.example.prestapp.data.remote.dtos.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton
import com.example.prestapp.data.local.entities.toDto
import com.example.prestapp.data.remote.dtos.ClienteDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Singleton
class ClienteRepository @Inject constructor(
    private val clienteDao: ClienteDao,
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
                    Log.d("ClienteRepository", "Connectivity detected, starting syncPendingClientes")
                    syncPendingClientes()
                    Log.d("ClienteRepository", "Fetching all clients from API after sync")
                    //val allClientes = fetchAllClientesFromApi()
                    Log.d("ClienteRepository", "Updating local database with API data")
                    //updateLocalDatabaseWithApiData(allClientes)
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun getClientes(): Flow<List<ClienteEntity>> {
        return clienteDao.getClientes()
    }

    fun getClienteById(clienteId: Int): Flow<ClienteEntity> {
        return clienteDao.getClienteById(clienteId)
    }


    fun isConnectedFlow(): StateFlow<Boolean> {
        return connectivityReceiver.isConnectedFlow
    }

    suspend fun syncPendingClientes() {
        val pendingClientes = clienteDao.getPendingClientes()
        Log.d("ClienteRepository", "Pending clients to sync: ${pendingClientes.size}")

        pendingClientes.forEach { cliente ->
            try {
                Log.d("ClienteRepository", "Syncing client: ${cliente.clienteID} - isDeleted: ${cliente.isDeleted}, isPending: ${cliente.isPending}")

                if (cliente.isDeleted) {
                    val response = prestAppApi.deleteCliente(cliente.clienteID)
                    if (response.isSuccessful || response.code() == 404) {
                        clienteDao.deleteClienteById(cliente.clienteID)
                        Log.d("ClienteRepository", "Deleted client ${cliente.clienteID} from local database")
                    } else {
                        Log.e("ClienteRepository", "Failed to delete client ${cliente.clienteID} from server: ${response.code()} - ${response.message()}")
                    }
                } else if (cliente.isPending) {
                    if (cliente.clienteID < 0) {
                        val response = prestAppApi.postCliente(cliente.toDto())
                        if (response.isSuccessful) {
                            val remoteCliente = response.body()!!
                            clienteDao.updateClienteId(cliente.clienteID, remoteCliente.clienteID)
                            clienteDao.updateCliente(cliente.copy(isPending = false, clienteID = remoteCliente.clienteID))
                            Log.d("ClienteRepository", "Created new client on server and updated local database with ID: ${remoteCliente.clienteID}")
                        } else {
                            Log.e("ClienteRepository", "Failed to create new client on server: ${response.code()} - ${response.message()}")
                        }
                    } else {
                        val response = prestAppApi.putCliente(cliente.clienteID, cliente.toDto())
                        if (response.isSuccessful) {
                            clienteDao.updateCliente(cliente.copy(isPending = false))
                            Log.d("ClienteRepository", "Updated client on server and marked as synced locally: ${cliente.clienteID}")
                        } else if (response.code() == 404) {
                            val remoteCliente = findClienteOnServer(cliente.cedula)
                            if (remoteCliente != null) {
                                clienteDao.updateClienteId(cliente.clienteID, remoteCliente.clienteID)
                                clienteDao.updateCliente(cliente.copy(isPending = false, clienteID = remoteCliente.clienteID))
                                Log.d("ClienteRepository", "Server could not find client, updated local ID to match server: ${remoteCliente.clienteID}")
                            } else {
                                Log.e("ClienteRepository", "Client not found on server: ${cliente.cedula}")
                            }
                        } else {
                            Log.e("ClienteRepository", "Failed to update client on server: ${response.code()} - ${response.message()}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ClienteRepository", "Error syncing client ${cliente.clienteID}: ${e.localizedMessage}", e)
            }
        }
    }

    private suspend fun findClienteOnServer(cedula: String): ClienteDto? {
        return try {
            val response = prestAppApi.getClienteByCedula(cedula)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ClienteRepository", "Failed to find client on server by cedula: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ClienteRepository", "Error finding client on server by cedula: ${e.localizedMessage}", e)
            null
        }
    }

    /*suspend fun fetchAllClientesFromApi(): List<ClienteDto> {
        return try {
            val response = prestAppApi.getClientes()
            if (response.isSuccessful && response.body() != null) {
                val clientes = response.body()!!
                Log.d("ClienteRepository", "Fetched ${clientes.size} clients from API")
                clientes
            } else {
                Log.e("ClienteRepository", "Failed to fetch clients from API: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ClienteRepository", "Error fetching clients from API: ${e.localizedMessage}", e)
            emptyList()
        }
    }

    suspend fun updateLocalDatabaseWithApiData(clientes: List<ClienteDto>) {
        val clienteEntities = clientes.map { clienteDto ->
            ClienteEntity(
                clienteID = clienteDto.clienteID,
                nombre = clienteDto.nombre,
                apodo = clienteDto.apodo,
                negocioReferencia = clienteDto.negocioReferencia,
                direccion = clienteDto.direccion,
                telefono = clienteDto.telefono,
                celular = clienteDto.celular,
                cedula = clienteDto.cedula,
                foto = clienteDto.foto,
                balance = clienteDto.balance.toDouble(),
                estaAlDia = clienteDto.estaAlDia
            )
        }
        Log.d("ClienteRepository", "Updating local database with ${clienteEntities.size} clients")
        clienteDao.deleteAllClients()
        clienteDao.insertOrUpdateClientes(clienteEntities)
        Log.d("ClienteRepository", "Local database updated")
    }*/

    suspend fun addCliente(clienteDto: ClienteDto) {
        val entity = clienteDto.toEntity().copy(isPending = true, clienteID = generateTempId())
        val localId = clienteDao.insertCliente(entity)
        val localEntity = clienteDao.getClienteById(localId.toInt()).first()

        if (connectivityReceiver.isConnectedFlow.value) {
            try {
                val response = prestAppApi.postCliente(clienteDto)
                if (response.isSuccessful) {
                    val remoteCliente = response.body()!!
                    clienteDao.updateClienteId(localEntity.clienteID, remoteCliente.clienteID)
                    clienteDao.updateCliente(localEntity.copy(isPending = false, clienteID = remoteCliente.clienteID))
                    Log.d("ClienteRepository", "New client created on server and local database updated: ${remoteCliente.clienteID}")
                } else {
                    Log.e("ClienteRepository", "Failed to create client on server: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("ClienteRepository", "Error syncing new client: ${e.localizedMessage}", e)
            }
        }
    }
    suspend fun getClienteByCedula(cedula: String): ClienteDto? {
        return try {
            val response = prestAppApi.getClienteByCedula(cedula)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ClienteRepository", "Failed to find client by cedula: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ClienteRepository", "Error finding client by cedula: ${e.localizedMessage}", e)
            null
        }
    }
    suspend fun getClienteIdByCedula(cedula: String): Int? {
        return clienteDao.getClienteIdByCedula(cedula)
    }


    suspend fun updateCliente(clienteDto: ClienteDto) {
        val entity = clienteDto.toEntity().copy(isPending = true)
        clienteDao.updateCliente(entity)

        if (connectivityReceiver.isConnectedFlow.value) {
            try {
                val response = prestAppApi.putCliente(entity.clienteID, clienteDto)
                if (response.isSuccessful) {
                    clienteDao.updateCliente(entity.copy(isPending = false))
                    Log.d("ClienteRepository", "Client updated on server and local database marked as synced: ${entity.clienteID}")
                } else if (response.code() == 404) {
                    val remoteCliente = findClienteOnServer(entity.cedula)
                    if (remoteCliente != null) {
                        clienteDao.updateClienteId(entity.clienteID, remoteCliente.clienteID)
                        clienteDao.updateCliente(entity.copy(isPending = false, clienteID = remoteCliente.clienteID))
                        Log.d("ClienteRepository", "Server could not find client, updated local ID to match server: ${remoteCliente.clienteID}")
                    } else {
                        Log.e("ClienteRepository", "Client not found on server: ${entity.cedula}")
                    }
                } else {
                    Log.e("ClienteRepository", "Failed to update client on server: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("ClienteRepository", "Error syncing updated client: ${e.localizedMessage}", e)
            }
        }
    }

    suspend fun deleteCliente(clienteId: Int) {
        val clienteToDelete = clienteDao.getClienteById(clienteId).firstOrNull()?.copy(isDeleted = true)
        if (clienteToDelete != null) {
            try {
                clienteDao.updateCliente(clienteToDelete)
                Log.d("ClienteRepository", "Marked client as deleted locally: ${clienteId}")
            } catch (dbEx: Exception) {
                Log.e("ClienteRepository", "Failed to mark client as deleted locally: ${dbEx.localizedMessage}", dbEx)
            }

            if (connectivityReceiver.isConnectedFlow.value) {
                try {
                    val response = prestAppApi.deleteCliente(clienteId)
                    if (response.isSuccessful || response.code() == 404) {
                        try {
                            clienteDao.deleteClienteById(clienteId)
                            Log.d("ClienteRepository", "Deleted client from local database: ${clienteId}")
                        } catch (dbEx: Exception) {
                            Log.e("ClienteRepository", "Failed to delete client locally: ${dbEx.localizedMessage}", dbEx)
                        }
                    } else {
                        Log.e("ClienteRepository", "Failed to delete client from server: ${response.code()} - ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.e("ClienteRepository", "Error syncing deleted client: ${e.localizedMessage}", e)
                }
            }
        }
    }

    suspend fun triggerManualSync() {
        Log.d("ClienteRepository", "Manual sync triggered")
        syncPendingClientes()
    }

    private fun generateTempId(): Int {
        return tempIdCounter--
    }
}
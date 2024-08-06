package com.example.prestapp.presentation.ruta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestapp.ConnectivityReceiver
import com.example.prestapp.data.local.entities.RutaEntity
import com.example.prestapp.data.remote.dtos.RutaDto
import com.example.prestapp.data.repository.RutaRepository
import com.example.prestapp.data.repository.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RutaViewModel @Inject constructor(
    private val repository: RutaRepository
) : ViewModel() {
    var uiState = MutableStateFlow(RutaUIState())
        private set

    init {
        viewModelScope.launch {
            getRutas()
        }
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            repository.isConnectedFlow().collect { isConnected ->
                if (isConnected) {
                    syncRutas()
                }
                uiState.update {
                    it.copy(isConnected = isConnected)
                }
            }
        }
    }

    fun onSetRuta(rutaId: Int?) {
        if (rutaId == null || rutaId == 0) {
            uiState.update {
                it.copy(
                    rutaID = 0,
                    nombre = "",
                    descripcion = "",
                    isLoading = false
                )
            }
        } else {
            viewModelScope.launch {
                repository.getRutaById(rutaId).collect { ruta ->
                    uiState.update {
                        it.copy(
                            rutaID = ruta.rutaID,
                            nombre = ruta.nombre,
                            descripcion = ruta.descripcion ?: "",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onNombreChanged(nombre: String) {
        uiState.update {
            it.copy(nombre = nombre)
        }
    }

    fun onDescripcionChanged(descripcion: String) {
        uiState.update {
            it.copy(descripcion = descripcion)
        }
    }

    fun saveRuta() {
        viewModelScope.launch {
            try {
                val rutaDto = uiState.value.toDTO()
                if (uiState.value.rutaID == null || uiState.value.rutaID == 0) {
                    repository.addRuta(rutaDto)
                } else {
                    repository.updateRuta(rutaDto)
                }
                uiState.value = RutaUIState()
            } catch (e: Exception) {
                uiState.update {
                    it.copy(errorMessage = "Error saving/updating route: ${e.localizedMessage}")
                }
            }
        }
    }

    fun newRuta() {
        uiState.value = RutaUIState()
    }

    fun deleteRuta(rutaId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteRuta(rutaId)
                getRutas()
            } catch (e: Exception) {
                uiState.update {
                    it.copy(errorMessage = "Error deleting route: ${e.localizedMessage}")
                }
            }
        }
    }

    fun validation(): Boolean {
        val isValid = uiState.value.nombre.isNotEmpty() && uiState.value.descripcion.isNotEmpty()
        uiState.update {
            it.copy(
                nombreError = uiState.value.nombre.isEmpty(),
                descripcionError = uiState.value.descripcion.isEmpty(),
                saveSuccess = isValid
            )
        }
        return isValid
    }

    private fun getRutas() {
        viewModelScope.launch {
            repository.getRutas().collect { rutas ->
                uiState.update {
                    it.copy(isLoading = false, rutas = rutas)
                }
            }
        }
    }

    fun syncRutas() {
        viewModelScope.launch {
            uiState.update { it.copy(isSyncing = true) }
            try {
                repository.syncPendingRutas()
                uiState.update { it.copy(isSyncing = false, errorMessage = null) }
            } catch (e: Exception) {
                uiState.update { it.copy(isSyncing = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            uiState.update { it.copy(isSyncing = true) }
            try {
                repository.triggerManualSync()
                uiState.update { it.copy(isSyncing = false, errorMessage = null) }
            } catch (e: Exception) {
                uiState.update { it.copy(isSyncing = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}

data class RutaUIState(
    val rutaID: Int? = null,
    var nombre: String = "",
    var descripcion: String = "",
    var saveSuccess: Boolean = false,
    var nombreError: Boolean = false,
    var descripcionError: Boolean = false,
    val rutas: List<RutaEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val isConnected: Boolean = true,
    val errorMessage: String? = null
)

fun RutaUIState.toDTO() = RutaDto(
    rutaID = rutaID ?: 0,
    nombre = nombre,
    descripcion = descripcion
)

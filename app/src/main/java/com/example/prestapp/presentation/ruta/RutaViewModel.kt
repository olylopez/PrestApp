package com.example.prestapp.presentation.ruta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    }

    fun onSetRuta(rutaId: Int) {
        viewModelScope.launch {
            val ruta = repository.getRutaById(rutaId).firstOrNull()
            ruta?.let {
                uiState.update {
                    it.copy(
                        rutaID = ruta.rutaID,
                        nombre = ruta.nombre,
                        descripcion = ruta.descripcion ?: ""
                    )
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
            repository.getRutas().collect { result ->
                when (result) {
                    is Resource.Loading -> uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> uiState.update {
                        it.copy(isLoading = false, rutas = result.data ?: emptyList())
                    }
                    is Resource.Error -> uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
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
    val errorMessage: String? = null
)

fun RutaUIState.toDTO() = RutaDto(
    rutaID = rutaID ?: 0,
    nombre = nombre,
    descripcion = descripcion
)
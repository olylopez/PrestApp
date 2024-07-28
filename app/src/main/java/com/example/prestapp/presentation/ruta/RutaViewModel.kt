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
    private val repository: RutaRepository,
) : ViewModel() {
    var uiState = MutableStateFlow(RutaUIState())
        private set

    val rutas: StateFlow<List<RutaEntity>> = repository.getRutas()
        .map { result ->
            when (result) {
                is Resource.Success -> result.data ?: emptyList()
                is Resource.Error -> emptyList()
                is Resource.Loading -> emptyList()
                else -> emptyList() // Manejo del else
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun getRutaById(rutaId: Int) {
        viewModelScope.launch {
            repository.getRutaById(rutaId).collect { ruta ->
                ruta?.let {
                    uiState.update { state ->
                        state.copy(
                            rutaID = it.rutaID,
                            nombre = it.nombre,
                            descripcion = it.descripcion ?: "" // Maneja el valor nulo
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
            val rutaDto = RutaDto(
                rutaID = uiState.value.rutaID ?: 0,
                nombre = uiState.value.nombre,
                descripcion = uiState.value.descripcion
            )
            repository.addRuta(rutaDto)
        }
    }

    fun updateRuta() {
        viewModelScope.launch {
            val rutaDto = RutaDto(
                rutaID = uiState.value.rutaID ?: 0,
                nombre = uiState.value.nombre,
                descripcion = uiState.value.descripcion
            )
            repository.updateRuta(rutaDto)
        }
    }

    fun deleteRuta(rutaId: Int) {
        viewModelScope.launch {
            repository.deleteRuta(rutaId)
        }
    }

    fun validation(): Boolean {
        uiState.value.nombreError = uiState.value.nombre.isEmpty()
        uiState.value.descripcionError = uiState.value.descripcion.isEmpty()
        uiState.update {
            it.copy(
                saveSuccess = !uiState.value.nombreError && !uiState.value.descripcionError
            )
        }
        return uiState.value.saveSuccess
    }

    fun newRuta() {
        viewModelScope.launch {
            uiState.value = RutaUIState()
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

fun RutaUIState.toEntity() = RutaEntity(
    rutaID = rutaID ?: 0,
    nombre = nombre,
    descripcion = descripcion
)

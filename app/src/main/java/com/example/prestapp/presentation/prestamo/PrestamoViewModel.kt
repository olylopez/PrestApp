package com.example.prestapp.presentation.prestamo

import android.app.DatePickerDialog
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestapp.data.local.entities.PrestamoEntity
import com.example.prestapp.data.local.entities.RutaEntity
import com.example.prestapp.data.remote.dtos.PrestamoDto
import com.example.prestapp.data.repository.ClienteRepository
import com.example.prestapp.data.repository.PrestamoRepository
import com.example.prestapp.data.repository.RutaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PrestamoViewModel @Inject constructor(
    private val repository: PrestamoRepository,
    private val rutaRepository: RutaRepository,
    private val clienteRepository: ClienteRepository
) : ViewModel() {
    var uiState = MutableStateFlow(PrestamoUIState())
        private set

    init {
        viewModelScope.launch {
            getPrestamos()
            getRutas()
        }
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            repository.isConnectedFlow().collect { isConnected ->
                if (isConnected) {
                    syncPrestamos()
                }
                uiState.update {
                    it.copy(isConnected = isConnected)
                }
            }
        }
    }

    fun searchClienteByCedula(cedula: String) {
        viewModelScope.launch {
            val cliente = clienteRepository.getClienteByCedula(cedula)
            if (cliente != null) {
                uiState.update {
                    it.copy(
                        clienteID = cliente.clienteID,  // Asocia el cliente encontrado al préstamo
                        isClienteFound = true,
                        clienteNombre = cliente.nombre,
                        clienteApodo = cliente.apodo ?: "N/A",
                        clienteNegocio = cliente.negocioReferencia ?: "N/A",
                        clienteDireccion = cliente.direccion,
                        clienteTelefono = cliente.telefono ?: "N/A",
                        clienteCelular = cliente.celular,
                        clienteCedula = cliente.cedula,
                        clienteBalance = cliente.balance,
                        clienteEstaAlDia = cliente.estaAlDia
                    )
                }
            } else {
                uiState.update {
                    it.copy(isClienteFound = false)
                }
            }
        }
    }

    fun onSetPrestamo(prestamoId: Int?) {
        if (prestamoId == null || prestamoId == 0) {
            uiState.update {
                it.copy(
                    prestamoID = 0,
                    cedula = "",
                    capital = 0.0,
                    cuotas = 13,
                    interes = 0.30,
                    montoCuota = 0.0,
                    montoPagado = 0.0,
                    estaPagado = false,
                    fechaPrestamo = "",
                    formaPago = "Semanal",
                    rutaID = 0,
                    clienteID = 0,  // Mantén el clienteID para uso interno
                    isLoading = false
                )
            }
        } else {
            viewModelScope.launch {
                repository.getPrestamoById(prestamoId).collect { prestamo ->
                    uiState.update {
                        it.copy(
                            prestamoID = prestamo.prestamoID,
                            cedula = prestamo.cedula,
                            capital = prestamo.capital.toDouble(), // Convertir BigDecimal a Double
                            cuotas = prestamo.cuotas,
                            interes = prestamo.interes.toDouble(), // Convertir BigDecimal a Double
                            montoCuota = prestamo.montoCuota.toDouble(), // Convertir BigDecimal a Double
                            montoPagado = prestamo.montoPagado.toDouble(), // Convertir BigDecimal a Double
                            estaPagado = prestamo.estaPagado,
                            fechaPrestamo = prestamo.fechaPrestamo,
                            formaPago = prestamo.formaPago,
                            rutaID = prestamo.rutaID,
                            clienteID = prestamo.clienteID,  // Mantén el clienteID para uso interno
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onCedulaChanged(cedula: String) {
        uiState.update { it.copy(cedula = cedula) }
    }

    fun onCapitalChanged(capital: Double) {
        uiState.update {
            val montoCuota = calculateMontoCuota(capital, it.cuotas, it.interes)
            it.copy(capital = capital, montoCuota = montoCuota)
        }
    }

    fun onRutaChanged(rutaId: Int, rutaNombre: String) {
        uiState.update {
            it.copy(rutaID = rutaId, rutaNombre = rutaNombre)
        }
    }

    fun onFechaPrestamoChanged(date: Date) {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(date)
        uiState.update { it.copy(fechaPrestamo = formattedDate) }
    }

    fun showDatePicker(context: Context) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onFechaPrestamoChanged(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun hasContent(): Boolean {
        return uiState.value.run {
            cedula.isNotEmpty() || capital > 0 || cuotas > 0 || fechaPrestamo.isNotEmpty()
        }
    }

    fun onCuotasChanged(cuotas: Int) {
        uiState.update {
            val montoCuota = calculateMontoCuota(it.capital, cuotas, it.interes)
            it.copy(cuotas = cuotas, montoCuota = montoCuota)
        }
    }

    fun savePrestamo() {
        viewModelScope.launch {
            try {
                val prestamoDto = uiState.value.toDTO()
                if (uiState.value.prestamoID == null || uiState.value.prestamoID == 0) {
                    repository.addPrestamo(prestamoDto, uiState.value.clienteID)  // Pasa el clienteId al crear un nuevo préstamo
                } else {
                    repository.updatePrestamo(prestamoDto)
                }
                uiState.value = PrestamoUIState()
            } catch (e: Exception) {
                uiState.update {
                    it.copy(errorMessage = "Error al guardar/actualizar el préstamo: ${e.localizedMessage}")
                }
            }
        }
    }
    fun filterPrestamosByRuta(rutaId: Int) {
        viewModelScope.launch {
            repository.getPrestamosByRutaId(rutaId).collect { prestamos ->
                uiState.update {
                    it.copy(isLoading = false, prestamos = prestamos)
                }
            }
        }
    }

    fun newPrestamo() {
        uiState.value = PrestamoUIState()
    }

    fun deletePrestamo(prestamoId: Int) {
        viewModelScope.launch {
            try {
                repository.deletePrestamo(prestamoId)
                getPrestamos()
            } catch (e: Exception) {
                uiState.update {
                    it.copy(errorMessage = "Error al eliminar el préstamo: ${e.localizedMessage}")
                }
            }
        }
    }

    fun validation(): Boolean {
        val isValid = uiState.value.cedula.isNotEmpty() &&
                uiState.value.capital > 500 &&
                uiState.value.cuotas in 1..30 &&
                uiState.value.rutaID != 0 &&  // Verifica que una ruta haya sido seleccionada
                uiState.value.clienteID != 0 // Verifica que un cliente haya sido seleccionado
        uiState.update {
            it.copy(
                cedulaError = uiState.value.cedula.isEmpty(),
                capitalError = uiState.value.capital <= 499,
                cuotasError = uiState.value.cuotas !in 1..30,
                saveSuccess = isValid
            )
        }
        return isValid
    }

    private fun getPrestamos() {
        viewModelScope.launch {
            repository.getPrestamos().collect { prestamos ->
                uiState.update {
                    it.copy(isLoading = false, prestamos = prestamos)
                }
            }
        }
    }

    private fun getRutas() {
        viewModelScope.launch {
            rutaRepository.getRutas().collect { rutas ->
                uiState.update { it.copy(rutas = rutas) }
            }
        }
    }

    fun syncPrestamos() {
        viewModelScope.launch {
            uiState.update { it.copy(isSyncing = true) }
            try {
                repository.syncPendingPrestamos()
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

    private fun calculateMontoCuota(capital: Double, cuotas: Int, interes: Double): Double {
        return (capital + capital * interes) / cuotas
    }
}

data class PrestamoUIState(
    val prestamoID: Int? = null,
    var cedula: String = "",
    var capital: Double = 0.0,
    var cuotas: Int = 13,
    var interes: Double = 0.30,
    var montoCuota: Double = 0.0,
    var montoPagado: Double = 0.0,
    var estaPagado: Boolean = false,
    var fechaPrestamo: String = "",
    var formaPago: String = "Semanal",
    var rutas: List<RutaEntity> = emptyList(),
    var rutaID: Int = 0,
    var rutaNombre: String = "",
    var saveSuccess: Boolean = false,
    var cedulaError: Boolean = false,
    var capitalError: Boolean = false,
    var cuotasError: Boolean = false,
    val prestamos: List<PrestamoEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val isConnected: Boolean = true,
    val errorMessage: String? = null,

    val isClienteFound: Boolean = false,
    val clienteNombre: String = "",
    val clienteApodo: String = "",
    val clienteNegocio: String = "",
    val clienteDireccion: String = "",
    val clienteTelefono: String = "",
    val clienteCelular: String = "",
    val clienteCedula: String = "",
    val clienteBalance: BigDecimal = BigDecimal("0.0"),
    val clienteEstaAlDia: Boolean = true,
    val clienteID: Int = 0 // New field for clienteId
)

fun PrestamoUIState.toDTO() = PrestamoDto(
    prestamoID = prestamoID ?: 0,
    cedula = cedula,
    capital = BigDecimal(capital),  // Convertir Double a BigDecimal
    cuotas = cuotas,
    interes = BigDecimal(interes),  // Convertir Double a BigDecimal
    montoPagado = BigDecimal(montoPagado),  // Convertir Double a BigDecimal
    fechaPrestamo = fechaPrestamo,
    formaPago = formaPago,
    rutaID = rutaID
)



package com.example.prestapp.presentation.pago

import android.app.DatePickerDialog
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestapp.ConnectivityReceiver
import com.example.prestapp.data.local.entities.PagoEntity
import com.example.prestapp.data.local.entities.RutaEntity
import com.example.prestapp.data.remote.dtos.PagoDto
import com.example.prestapp.data.repository.ClienteRepository
import com.example.prestapp.data.repository.PagoRepository
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
class PagoViewModel @Inject constructor(
    private val repository: PagoRepository,
    private val prestamoRepository: PrestamoRepository,
    private val rutaRepository: RutaRepository,
    private val clienteRepository: ClienteRepository,
    private val connectivityReceiver: ConnectivityReceiver
) : ViewModel() {
    var uiState = MutableStateFlow(PagoUIState())
        private set

    init {
        viewModelScope.launch {
            getPagos()
            getRutas()
        }
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityReceiver.isConnectedFlow.collect { isConnected ->
                if (isConnected) {
                    syncPagos()
                }
                uiState.update {
                    it.copy(isConnected = isConnected)
                }
            }
        }
    }

    fun getPagos() {
        viewModelScope.launch {
            repository.getPagos().collect { pagos ->
                uiState.update {
                    it.copy(isLoading = false, pagos = pagos)
                }
            }
        }
    }


    fun onSetPago(prestamoId: Int?) {
        if (prestamoId == null || prestamoId == 0) {
            uiState.update {
                it.copy(
                    pagoID = 0,
                    prestamoID = 0,
                    monto = 0.0,
                    fechaPago = "",
                    isLoading = false
                )
            }
        } else {
            viewModelScope.launch {
                prestamoRepository.getPrestamoById(prestamoId).collect { prestamo ->
                    uiState.update {
                        it.copy(
                            prestamoID = prestamo.prestamoID,
                            cedula = prestamo.cedula,
                            capital = prestamo.capital.toDouble(),
                            cuotas = prestamo.cuotas,
                            interes = prestamo.interes.toDouble(),
                            montoCuota = prestamo.montoCuota.toDouble(),
                            montoPagado = prestamo.montoPagado.toDouble(),
                            estaPagado = prestamo.estaPagado,
                            fechaPrestamo = prestamo.fechaPrestamo,
                            formaPago = prestamo.formaPago,
                            rutaID = prestamo.rutaID,
                            clienteID = prestamo.clienteID,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun syncPagos() {
        viewModelScope.launch {
            uiState.update { it.copy(isSyncing = true) }
            try {
                repository.syncPendingPagos()
                uiState.update { it.copy(isSyncing = false, errorMessage = null) }
            } catch (e: Exception) {
                uiState.update { it.copy(isSyncing = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun savePago() {
        viewModelScope.launch {
            try {
                val pagoDto = uiState.value.toDTO()
                if (uiState.value.pagoID == null || uiState.value.pagoID == 0) {
                    repository.addPago(pagoDto)
                } else {
                    repository.updatePago(pagoDto)
                }
                uiState.value = PagoUIState()
            } catch (e: Exception) {
                uiState.update {
                    it.copy(errorMessage = "Error al guardar/actualizar el pago: ${e.localizedMessage}")
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
                        clienteID = cliente.clienteID,
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

    fun onMontoChanged(monto: Double) {
        uiState.update { it.copy(monto = monto) }
    }

    fun onFechaPagoChanged(date: Date) {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(date)
        uiState.update { it.copy(fechaPago = formattedDate) }
    }

    fun showDatePicker(context: Context) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onFechaPagoChanged(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun hasContent(): Boolean {
        return uiState.value.run {
            prestamoID != 0 || monto > 0 || fechaPago.isNotEmpty()
        }
    }


    fun deletePago(pagoId: Int) {
        viewModelScope.launch {
            try {
                repository.deletePago(pagoId)
                getPagos()
            } catch (e: Exception) {
                uiState.update {
                    it.copy(errorMessage = "Error al eliminar el pago: ${e.localizedMessage}")
                }
            }
        }
    }

    fun validation(): Boolean {
        val isValid = uiState.value.prestamoID != 0 &&
                uiState.value.monto > 0 &&
                uiState.value.fechaPago.isNotEmpty()
        uiState.update {
            it.copy(
                montoError = uiState.value.monto <= 0,
                saveSuccess = isValid
            )
        }
        return isValid
    }



    private fun getRutas() {
        viewModelScope.launch {
            rutaRepository.getRutas().collect { rutas ->
                uiState.update { it.copy(rutas = rutas) }
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

data class PagoUIState(
    val pagoID: Int? = null,
    var prestamoID: Int = 0,
    var monto: Double = 0.0,
    var fechaPago: String = "",
    var saveSuccess: Boolean = false,
    var montoError: Boolean = false,
    val pagos: List<PagoEntity> = emptyList(),
    val rutas: List<RutaEntity> = emptyList(),
    var isLoading: Boolean = false,
    var isSyncing: Boolean = false,
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
    val clienteID: Int = 0, // New field for clienteId

    // Additional fields from PrestamoEntity (if needed)
    var cedula: String = "",
    var capital: Double = 0.0,
    var cuotas: Int = 13,
    var interes: Double = 0.30,
    var montoCuota: Double = 0.0,
    var montoPagado: Double = 0.0,
    var estaPagado: Boolean = false,
    var fechaPrestamo: String = "",
    var formaPago: String = "Semanal",
    var rutaID: Int = 0,
)

fun PagoUIState.toDTO() = PagoDto(
    pagoID = pagoID ?: 0,
    prestamoID = prestamoID,
    monto = BigDecimal(monto),
    fechaPago = fechaPago
)

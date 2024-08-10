package com.example.prestapp.presentation.cliente

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestapp.data.local.entities.ClienteEntity
import com.example.prestapp.data.local.entities.PrestamoEntity
import com.example.prestapp.data.remote.dtos.ClienteDto
import com.example.prestapp.data.repository.ClienteRepository
import com.example.prestapp.data.repository.PrestamoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ClienteViewModel @Inject constructor(
    private val repository: ClienteRepository,
    private val repositoryP: PrestamoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var uiState = MutableStateFlow(ClienteUIState())
        private set

    var tempImageUri: Uri? = null
        private set

    init {
        viewModelScope.launch {
            getClientes()
        }
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            repository.isConnectedFlow().collect { isConnected ->
                if (isConnected) {
                    syncClientes()
                }
                uiState.update {
                    it.copy(isConnected = isConnected)
                }
            }
        }
    }

    fun requestPermissionsAndTakePicture(activity: Activity) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissions.all { ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED }) {
            takePicture(activity)
        } else {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_PERMISSIONS)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePicture(activity: Activity) {
        val file = File(activity.filesDir, "temp_image.png")
        val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.provider", file)
        tempImageUri = uri

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE)
        }
    }

    fun handlePictureResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            tempImageUri?.let { uri ->
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                val base64Image = encodeImageToBase64(bitmap)
                uiState.update { it.copy(foto = base64Image) }
            }
        }
    }

    fun syncClientes() {
        viewModelScope.launch {
            uiState.update { it.copy(isSyncing = true) }
            try {
                repository.syncPendingClientes()
                //fetchClientesFromApi()
                uiState.update { it.copy(isSyncing = false, errorMessage = null) }
            } catch (e: Exception) {
                uiState.update { it.copy(isSyncing = false, errorMessage = e.localizedMessage) }
            }
        }
    }
    fun loadCliente(clienteId: Int) {
        viewModelScope.launch {
            repository.getClienteById(clienteId).collect { cliente ->
                val prestamos = repositoryP.getPrestamosByCedula(cliente.cedula).firstOrNull() ?: emptyList()

                uiState.update {
                    it.copy(
                        clienteID = cliente.clienteID,
                        nombre = cliente.nombre,
                        apodo = cliente.apodo.toString(),
                        negocioReferencia = cliente.negocioReferencia.toString(),
                        direccion = cliente.direccion,
                        telefono = cliente.telefono.toString(),
                        celular = cliente.celular,
                        cedula = cliente.cedula,
                        balance = cliente.balance,
                        estaAlDia = cliente.estaAlDia,
                        prestamos = prestamos  // Cargar los préstamos del cliente
                    )
                }
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

    fun deleteCliente(clienteId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteCliente(clienteId)
                getClientes() // Refresh the list after deletion
            } catch (e: Exception) {
                uiState.update {
                    it.copy(errorMessage = "Error deleting cliente: ${e.localizedMessage}")
                }
            }
        }
    }
    suspend fun getClienteIdByCedula(cedula: String): Int? {
        return repository.getClienteIdByCedula(cedula)
    }

    fun getPrestamosByClienteId(clienteId: Int): Flow<List<PrestamoEntity>> {
        return repositoryP.getPrestamosByClienteId(clienteId)
    }

    fun updateFoto(base64Image: String) {
        uiState.update { it.copy(foto = base64Image) }
    }

    fun onNombreChanged(nombre: String?) {
        uiState.update { it.copy(nombre = nombre ?: "") }
    }

    fun onApodoChanged(apodo: String?) {
        uiState.update { it.copy(apodo = apodo ?: "") }
    }

    fun onNegocioReferenciaChanged(negocioReferencia: String?) {
        uiState.update { it.copy(negocioReferencia = negocioReferencia ?: "") }
    }

    fun onDireccionChanged(direccion: String?) {
        uiState.update { it.copy(direccion = direccion ?: "") }
    }

    fun onTelefonoChanged(telefono: String?) {
        uiState.update { it.copy(telefono = telefono ?: "") }
    }

    fun onCelularChanged(celular: String?) {
        uiState.update { it.copy(celular = celular ?: "") }
    }

    fun onCedulaChanged(cedula: String?) {
        uiState.update { it.copy(cedula = cedula ?: "") }
    }

    fun saveCliente() {
        viewModelScope.launch {
            try {
                val clienteDto = uiState.value.toDTO()
                if (uiState.value.clienteID == 0) {
                    repository.addCliente(clienteDto)
                } else {
                    repository.updateCliente(clienteDto)
                }
                uiState.value = ClienteUIState() // Reset UI state
            } catch (e: Exception) {
                uiState.update {
                    it.copy(errorMessage = "Error saving/updating cliente: ${e.localizedMessage}")
                }
            }
        }
    }

    fun hasContent(): Boolean {
        return uiState.value.run {
            nombre.isNotEmpty() || apodo.isNotEmpty() || negocioReferencia.isNotEmpty() || direccion.isNotEmpty() ||
                    telefono.isNotEmpty() || celular.isNotEmpty() || cedula.isNotEmpty() || foto != null
        }
    }

    fun validation(): Boolean {
        val isValid = uiState.value.nombre.isNotEmpty() &&
                uiState.value.direccion.isNotEmpty() &&
                uiState.value.celular.isNotEmpty() &&
                uiState.value.cedula.isNotEmpty()
        uiState.update {
            it.copy(
                nombreError = uiState.value.nombre.isEmpty(),
                direccionError = uiState.value.direccion.isEmpty(),
                celularError = uiState.value.celular.isEmpty(),
                cedulaError = uiState.value.cedula.isEmpty(),
                saveSuccess = isValid
            )
        }
        return isValid
    }

    fun newCliente() {
        uiState.value = ClienteUIState() // Reset UI state
    }

    fun getCliente(clienteId: Int): Flow<ClienteEntity?> {
        return repository.getClienteById(clienteId)
    }

    private fun getClientes() {
        viewModelScope.launch {
            repository.getClientes().collect { clientes ->
                uiState.update {
                    it.copy(isLoading = false, clientes = clientes)
                }
            }
        }
    }

    fun onSetCliente(clienteId: Int?) {
        if (clienteId == null || clienteId == 0) {
            uiState.update {
                it.copy(
                    clienteID = 0,
                    nombre = "",
                    apodo = "",
                    negocioReferencia = "",
                    direccion = "",
                    telefono = "",
                    celular = "",
                    cedula = "",
                    foto = null,
                    balance = 0.0,
                    estaAlDia = true,
                    isLoading = false
                )
            }
        } else {
            viewModelScope.launch {
                repository.getClienteById(clienteId).collect { cliente ->
                    cliente?.let {
                        uiState.update {
                            it.copy(
                                clienteID = cliente.clienteID,
                                nombre = cliente.nombre,
                                apodo = cliente.apodo ?: "",
                                negocioReferencia = cliente.negocioReferencia ?: "",
                                direccion = cliente.direccion,
                                telefono = cliente.telefono ?: "",
                                celular = cliente.celular,
                                cedula = cliente.cedula,
                                foto = cliente.foto,
                                balance = cliente.balance,
                                estaAlDia = cliente.estaAlDia,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun encodeImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 1001
        const val REQUEST_CODE_TAKE_PICTURE = 1002
    }
}

data class ClienteUIState(
    val clienteID: Int? = null,
    var nombre: String = "",
    var apodo: String = "",
    var negocioReferencia: String = "",
    var direccion: String = "",
    var telefono: String = "",
    var celular: String = "",
    var cedula: String = "",
    var foto: String? = null,
    var balance: Double = 0.0,
    var prestamos: List<PrestamoEntity> = emptyList(),  // Añade esta propiedad
    var estaAlDia: Boolean = true,
    var saveSuccess: Boolean = false,
    var nombreError: Boolean = false,
    var direccionError: Boolean = false,
    var celularError: Boolean = false,
    var cedulaError: Boolean = false,
    var fotoError: Boolean = false,
    val clientes: List<ClienteEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val isConnected: Boolean = true,
    val errorMessage: String? = null
)

fun ClienteUIState.toDTO() = ClienteDto(
    clienteID = clienteID ?: 0,
    nombre = nombre,
    apodo = apodo,
    negocioReferencia = negocioReferencia,
    direccion = direccion,
    telefono = telefono,
    celular = celular,
    cedula = cedula,
    foto = foto ?: "",
    balance = BigDecimal.valueOf(balance),
    estaAlDia = estaAlDia
)
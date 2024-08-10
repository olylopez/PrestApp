package com.example.prestapp.presentation.prestamo

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.prestapp.R
import com.example.prestapp.presentation.cliente.ClienteViewModel
import com.example.prestapp.presentation.componentes.DropDownInput
import com.example.prestapp.screens.Screen
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoScreen(
    navController: NavHostController,
    viewModel: PrestamoViewModel = hiltViewModel(),
    viewModelC: ClienteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showNotFoundDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Registro de Préstamo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Cédula del Cliente
                OutlinedTextField(
                    value = uiState.cedula,
                    onValueChange = { viewModel.onCedulaChanged(it) },
                    label = { Text("Cédula del Cliente") },
                    isError = uiState.cedulaError,
                    trailingIcon = {
                        IconButton(onClick = {
                            if (uiState.cedula.isNotEmpty()) {
                                viewModel.searchClienteByCedula(uiState.cedula)
                                showNotFoundDialog = !uiState.isClienteFound
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar Cliente")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.isClienteFound) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberImagePainter(
                            data = R.drawable.profile,
                            builder = {
                                crossfade(true)
                                placeholder(R.drawable.profile)
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                    Text(text = "Nombre: ${uiState.clienteNombre}", style = MaterialTheme.typography.headlineMedium)
                    Text(text = "Apodo: ${uiState.clienteApodo ?: "N/A"}")
                    Text(text = "Negocio: ${uiState.clienteNegocio ?: "N/A"}")
                    Text(text = "Dirección: ${uiState.clienteDireccion}")
                    Text(text = "Teléfono: ${uiState.clienteTelefono ?: "N/A"}")
                    Text(text = "Celular: ${uiState.clienteCelular}")
                    Text(text = "Cédula: ${uiState.clienteCedula}")
                    Text(text = "Balance: ${uiState.clienteBalance}")
                    Text(text = "Estado: ${if (uiState.clienteEstaAlDia) "Al día" else "Pendiente"}")
                } else if (showNotFoundDialog) {
                    AlertDialog(
                        onDismissRequest = { showNotFoundDialog = false },
                        title = { Text(text = "Cliente no encontrado") },
                        text = { Text(text = "La cédula no corresponde a ningún cliente registrado. ¿Desea crear un nuevo cliente con esta cédula?") },
                        confirmButton = {
                            Button(onClick = {
                                // Aquí debes manejar la navegación al registro de cliente
                                showNotFoundDialog = false
                            }) {
                                Text("Sí")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showNotFoundDialog = false }) {
                                Text("No")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Detalles del Préstamo", style = MaterialTheme.typography.headlineMedium)

                // Selector de Ruta
                var expanded by remember { mutableStateOf(false) }
                val selectedRuta = uiState.rutas.find { it.rutaID == uiState.rutaID }

                OutlinedTextField(
                    value = selectedRuta?.let { "${it.rutaID} - ${it.nombre}" } ?: "",
                    onValueChange = { /* No editable */ },
                    label = { Text("Seleccionar Ruta") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                )

                // Selección de Ruta
                DropDownInput(
                    items = uiState.rutas,
                    label = "Ruta",
                    itemToString = { "${it.rutaID} - ${it.nombre}" },
                    onItemSelected = { viewModel.onRutaChanged(it.rutaID, it.nombre) },
                    selectedItem = uiState.rutaNombre,
                    isError = false // Maneja el estado de error según tus necesidades
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Capital Input
                OutlinedTextField(
                    value = if (uiState.capital == 0.0) "" else NumberFormat.getNumberInstance(Locale.US).format(uiState.capital),
                    onValueChange = {
                        val value = it.replace(",", "").toDoubleOrNull() ?: 0.0
                        viewModel.onCapitalChanged(value)
                    },
                    label = { Text("Capital") },
                    isError = uiState.capitalError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    visualTransformation = ThousandSeparatorVisualTransformation()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Cuotas Input
                OutlinedTextField(
                    value = uiState.cuotas.toString(),
                    onValueChange = { viewModel.onCuotasChanged(it.toIntOrNull() ?: 13) },
                    label = { Text("Cuotas (1 a 30)") },
                    isError = uiState.cuotasError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Monto Cuota Output
                OutlinedTextField(
                    value = NumberFormat.getNumberInstance(Locale.US).format(uiState.montoCuota),
                    onValueChange = { /* Solo lectura */ },
                    label = { Text("Monto Cuota") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fecha de Préstamo Input
                OutlinedTextField(
                    value = uiState.fechaPrestamo,
                    onValueChange = { /* No direct input change, handled by DatePicker */ },
                    label = { Text("Fecha de Préstamo") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.showDatePicker(context) }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (viewModel.hasContent()) {
                                showDialog = true
                            } else {
                                viewModel.newPrestamo()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Nuevo", color = Color.White)
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text(text = "Confirmación") },
                            text = { Text(text = "¿Estás seguro de que deseas limpiar los campos?") },
                            confirmButton = {
                                Button(onClick = {
                                    viewModel.newPrestamo()
                                    showDialog = false
                                }) {
                                    Text("Sí")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showDialog = false }) {
                                    Text("No")
                                }
                            }
                        )
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (viewModel.validation()) {
                                    viewModel.savePrestamo()
                                    navController.navigate(Screen.ClienteList.toString())
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar", color = Color.White)
                    }
                }
                uiState.errorMessage?.let { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    )
}

class ThousandSeparatorVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val number = text.text.toDoubleOrNull()
        val formattedNumber = number?.let { NumberFormat.getNumberInstance(Locale.US).format(it) } ?: text.text
        return TransformedText(AnnotatedString(formattedNumber), OffsetMapping.Identity)
    }
}
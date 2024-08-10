package com.example.prestapp.presentation.cliente

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.prestapp.R
import com.example.prestapp.screens.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteScreen(
    navController: NavHostController,
    clienteId: Int,
    viewModel: ClienteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current as Activity

    LaunchedEffect(clienteId) {
        viewModel.onSetCliente(clienteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Registro de Cliente") },
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
                OutlinedTextField(
                    value = uiState.cedula,
                    onValueChange = { viewModel.onCedulaChanged(it) },
                    label = { Text("Cédula") },
                    isError = uiState.cedulaError,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                        .clickable {
                            viewModel.requestPermissionsAndTakePicture(context)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.foto == null) {
                        Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Camera Icon",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Image(
                            painter = rememberImagePainter(data = uiState.foto),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.nombre,
                    onValueChange = { viewModel.onNombreChanged(it) },
                    label = { Text("Nombre del Cliente") },
                    isError = uiState.nombreError,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.apodo,
                    onValueChange = { viewModel.onApodoChanged(it) },
                    label = { Text("Apodo del Cliente") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.telefono,
                    onValueChange = { viewModel.onTelefonoChanged(it) },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.direccion,
                    onValueChange = { viewModel.onDireccionChanged(it) },
                    label = { Text("Dirección del Cliente") },
                    isError = uiState.direccionError,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.negocioReferencia,
                    onValueChange = { viewModel.onNegocioReferenciaChanged(it) },
                    label = { Text("Referencia de Negocio") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.celular,
                    onValueChange = { viewModel.onCelularChanged(it) },
                    label = { Text("Celular") },
                    isError = uiState.celularError,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.balance.toString(),
                    onValueChange = { },
                    label = { Text("Balance Pendiente") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
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
                                viewModel.newCliente()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "new button", tint = Color.White)
                        Text("Nuevo", color = Color.White)
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text(text = "Confirmación") },
                            text = { Text(text = "¿Estás seguro de que deseas limpiar los campos?") },
                            confirmButton = {
                                Button(onClick = {
                                    viewModel.newCliente()
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
                    Button(onClick = {
                        if (viewModel.validation()) {
                            viewModel.saveCliente()
                            navController.navigate(Screen.ClienteList.toString())
                        }
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "save", tint = Color.White)
                        Text(text = "Guardar", color = Color.White)

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
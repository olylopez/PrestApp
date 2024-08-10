package com.example.prestapp.presentation.cliente

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.prestapp.R
import com.example.prestapp.data.local.entities.ClienteEntity
import com.example.prestapp.screens.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteListScreen(
    navController: NavHostController,
    viewModel: ClienteViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    var showTitleAndSearch by remember { mutableStateOf(true) }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }
            .collect { index ->
                showTitleAndSearch = index == 0
            }
    }

    Scaffold(
        topBar = {
            if (showTitleAndSearch) {
                Column {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Lista de Clientes",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (uiState.isConnected) "Conectado" else "Desconectado",
                                    color = if (uiState.isConnected) Color.Green else Color.Red,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                IconButton(onClick = { viewModel.triggerManualSync() }) {
                                    if (uiState.isSyncing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(Icons.Default.Sync, contentDescription = "Sync")
                                    }
                                }
                            }
                        }
                    )
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Buscar") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                val filteredClientes = uiState.clientes.filter {
                    it.nombre.contains(searchText, ignoreCase = true) ||
                            it.cedula.contains(searchText, ignoreCase = true) ||
                            it.direccion.contains(searchText, ignoreCase = true)
                }

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredClientes) { cliente ->
                        ClienteItem(
                            cliente = cliente,
                            onEditCliente = { navController.navigate("${Screen.ClienteRegistro}/${cliente.clienteID}") },
                            onViewDetails = { navController.navigate("${Screen.ClienteDetalle}/${cliente.clienteID}") },
                            onDeleteCliente = { viewModel.deleteCliente(cliente.clienteID) }
                        )
                    }
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

            if (!uiState.isConnected) {
                Text(
                    text = "No hay conexión a Internet",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                Text(
                    text = if (uiState.isSyncing) "Sincronizando datos..." else "Datos sincronizados",
                    color = if (uiState.isSyncing) Color.Yellow else Color.Green,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ClienteItem(
    cliente: ClienteEntity,
    onEditCliente: (ClienteEntity) -> Unit,
    onViewDetails: (ClienteEntity) -> Unit,
    onDeleteCliente: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmación") },
            text = {
                Column {
                    Text(text = "¿Estás seguro de que deseas eliminar este cliente?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "ID: ${cliente.clienteID}")
                    Text(text = "Nombre: ${cliente.nombre}")
                }
            },
            confirmButton = {
                Button(onClick = {
                    onDeleteCliente()
                    showDialog = false
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    ListItem(
        headlineContent = { Text(text = cliente.nombre) },
        supportingContent = {
            Column {
                Text(text = "Dirección: ${cliente.direccion}")
                Text(text = "Cédula: ${cliente.cedula}")
            }
        },
        leadingContent = {
            Image(
                painter = rememberImagePainter(
                    data = R.drawable.profile,
                    builder = {
                        crossfade(true)
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onViewDetails(cliente) },
                contentScale = ContentScale.Crop
            )
        },
        trailingContent = {
            IconButton(
                onClick = { showDialog = true },
                content = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete button"
                    )
                }
            )
        },
        modifier = Modifier
            .clickable { onEditCliente(cliente) }
            .padding(8.dp)
    )
    Divider(color = Color.Gray, thickness = 1.dp)
}
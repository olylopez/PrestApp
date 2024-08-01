package com.example.prestapp.presentation.ruta

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.prestapp.data.local.entities.RutaEntity
import com.example.prestapp.screens.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaListScreen(
    viewModel: RutaViewModel = hiltViewModel(),
    navController: NavHostController,
    onEditRuta: (RutaEntity) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }
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
                            Text(
                                text = "Lista de Rutas",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
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
                val filteredRutas = uiState.rutas.filter {
                    it.nombre.contains(searchText, ignoreCase = true) ||
                            it.descripcion?.contains(searchText, ignoreCase = true) == true
                }

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredRutas) { ruta ->
                        RutaItem(
                            ruta = ruta,
                            onEditRuta = { navController.navigate("${Screen.RutaScreen}/${ruta.rutaID}") },
                            onDeleteRuta = { viewModel.deleteRuta(ruta.rutaID) }
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
        }
    }
}

@Composable
fun RutaItem(
    ruta: RutaEntity,
    onEditRuta: (RutaEntity) -> Unit,
    onDeleteRuta: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmación") },
            text = {
                Column {
                    Text(text = "¿Estás seguro de que deseas eliminar esta ruta?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "ID: ${ruta.rutaID}")
                    Text(text = "Nombre: ${ruta.nombre}")
                }
            },
            confirmButton = {
                Button(onClick = {
                    onDeleteRuta()
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
        headlineContent = { Text(text = ruta.nombre) },
        supportingContent = { Text(text = ruta.descripcion ?: "") },
        leadingContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF4CAF50), shape = CircleShape)
            ) {
                Text(text = ruta.rutaID.toString(), color = Color.White)
            }
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
            .clickable { onEditRuta(ruta) }
            .padding(8.dp)
    )
    Divider(color = Color.Gray, thickness = 1.dp)
}

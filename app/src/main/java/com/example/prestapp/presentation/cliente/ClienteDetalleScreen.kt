package com.example.prestapp.presentation.cliente

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.prestapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteDetalleScreen(
    navController: NavHostController,
    viewModel: ClienteViewModel = hiltViewModel(),
    clienteId: Int // Recibe el ID del cliente
) {
    val uiState by viewModel.uiState.collectAsState()
    val prestamos by viewModel.getPrestamosByClienteId(clienteId).collectAsState(initial = emptyList())

    LaunchedEffect(clienteId) {
        viewModel.loadCliente(clienteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalle del Cliente") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Encabezado con la información del cliente
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
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

                        Text(text = "Nombre: ${uiState.nombre}", style = MaterialTheme.typography.headlineMedium)
                        Text(text = "Apodo: ${uiState.apodo ?: "N/A"}")
                        Text(text = "Negocio: ${uiState.negocioReferencia ?: "N/A"}")
                        Text(text = "Dirección: ${uiState.direccion}")
                        Text(text = "Teléfono: ${uiState.telefono ?: "N/A"}")
                        Text(text = "Celular: ${uiState.celular}")
                        Text(text = "Cédula: ${uiState.cedula}")
                        Text(text = "Balance: ${uiState.balance}")
                        Text(text = "Estado: ${if (uiState.estaAlDia) "Al día" else "Pendiente"}")

                        Spacer(modifier = Modifier.height(16.dp))

                        // Título para la lista de préstamos
                        Text(text = "Préstamos del Cliente", style = MaterialTheme.typography.headlineMedium)
                    }
                }

                // Lista de préstamos del cliente
                items(prestamos) { prestamo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Capital: ${prestamo.capital.toPlainString()}")
                            Text(text = "Cuotas: ${prestamo.cuotas}")
                            Text(text = "Monto Pagado: ${prestamo.montoPagado.toPlainString()}")
                            Text(text = "Estado: ${if (prestamo.estaPagado) "Pagado" else "Pendiente"}")
                            Text(text = "Fecha: ${prestamo.fechaPrestamo}")
                        }
                    }
                }

                item {
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
    )
}
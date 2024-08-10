package com.example.prestapp.presentation.prestamo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.prestapp.presentation.componentes.DropDownInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoListScreen(
    navController: NavHostController,
    viewModel: PrestamoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Préstamos por Ruta") },
                // Navigation and other actions...
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Using DropDownInput for selecting the route
                DropDownInput(
                    items = uiState.rutas,
                    label = "Seleccionar Ruta",
                    itemToString = { it.nombre },
                    onItemSelected = { ruta ->
                        viewModel.filterPrestamosByRuta(ruta.rutaID)
                    },
                    selectedItem = uiState.rutaNombre, // Assuming you have `rutaNombre` to track selected route
                    isError = false // Handle error state if needed
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(uiState.prestamos) { prestamo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Capital: ${prestamo.capital}")
                                Text(text = "Cuotas: ${prestamo.cuotas}")
                                Text(text = "Monto Pagado: ${prestamo.montoPagado}")
                                Text(text = "Estado: ${if (prestamo.estaPagado) "Pagado" else "Pendiente"}")
                                Text(text = "Fecha: ${prestamo.fechaPrestamo}")
                            }
                        }
                    }
                }
            }
        }
    )
}


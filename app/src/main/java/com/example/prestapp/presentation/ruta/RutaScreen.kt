package com.example.prestapp.presentation.ruta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.prestapp.screens.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaScreen(
    viewModel: RutaViewModel = hiltViewModel(),
    navController: NavHostController,
    rutaId: Int?
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.onSetRuta(rutaId ?: 0)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registro de Ruta",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            RutaBody(
                uiState = uiState,
                onNombreChanged = viewModel::onNombreChanged,
                onDescripcionChanged = viewModel::onDescripcionChanged,
                onSaveRuta = { viewModel.saveRuta() },
                onNewRuta = { viewModel.newRuta() },
                onValidation = viewModel::validation,
                navController = navController
            )
        }
    }
}

@Composable
fun RutaBody(
    uiState: RutaUIState,
    onNombreChanged: (String) -> Unit,
    onDescripcionChanged: (String) -> Unit,
    onSaveRuta: () -> Unit,
    onNewRuta: () -> Unit,
    onValidation: () -> Boolean,
    navController: NavHostController
) {
    var guardo by remember { mutableStateOf(false) }
    var errorGuardar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            label = { Text(text = "Nombre de la ruta") },
            value = uiState.nombre,
            onValueChange = {
                onNombreChanged(it)
            },
            isError = uiState.nombreError,
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.nombreError) {
            Text(
                text = "Campo Obligatorio",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            label = { Text(text = "Descripción de la ruta") },
            value = uiState.descripcion,
            onValueChange = {
                onDescripcionChanged(it)
            },
            isError = uiState.descripcionError,
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.descripcionError) {
            Text(
                text = "Campo Obligatorio",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    onNewRuta()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "new button",
                    tint = Color.White
                )
                Text("Nuevo", color = Color.White)
            }
            Button(
                onClick = {
                    if (onValidation()) {
                        onSaveRuta()
                        guardo = true
                        navController.navigate(Screen.RutaListScreen.toString())
                    } else {
                        errorGuardar = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "save", tint = Color.White)
                Text(text = "Guardar", color = Color.White)
            }
        }
    }
}
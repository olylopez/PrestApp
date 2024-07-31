package com.example.prestapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConfiguracionDialog(
    onDismissRequest: () -> Unit,
    onUsersClick: () -> Unit,
    onRutasClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Configuración")
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onUsersClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),                   modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Usuarios", color = Color.White)
                }
                Button(
                    onClick = onRutasClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Color verde
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Rutas", color = Color.White)
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cerrar")
            }
        }
    )
}

package com.example.prestapp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.prestapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import com.example.prestapp.screens.Screen

@Composable
fun MainScreen(navController: NavHostController) {
    val primaryBackground = MaterialTheme.colorScheme.background
    val primarySurface = MaterialTheme.colorScheme.surface
    val primaryTextColor = MaterialTheme.colorScheme.onBackground
    val buttonGreen = Color(0xFF4CAF50) // Verde para los botones
    val lowerBackground = Color(0xFF81C784) // Fondo verde oscuro para la parte inferior

    // Definir tipografías personalizadas
    val lobsterFont = FontFamily(Font(R.font.lobster_regular))
    val robotoFont = FontFamily(Font(R.font.roboto_regular))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryBackground), // Fondo dependiendo del tema
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Box para el área superior con el logo y contadores
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Contadores de clientes y rutas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicText(text = "Clientes: 122", style = MaterialTheme.typography.bodyLarge.copy(color = primaryTextColor, fontFamily = robotoFont, fontWeight = FontWeight.Bold))
                    BasicText(text = "Rutas: 5", style = MaterialTheme.typography.bodyLarge.copy(color = primaryTextColor, fontFamily = robotoFont, fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Logo
                Image(
                    painter = painterResource(R.drawable.descarga),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "PREST APP", style = MaterialTheme.typography.headlineLarge.copy(color = primaryTextColor, fontSize = 32.sp, fontFamily = lobsterFont, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
            }

            // Área inferior con fondo verde oscuro y botones
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(lowerBackground, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Botones para las funcionalidades
                    Button(
                        onClick = { navController.navigate(Screen.ClientesList) },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonGreen), // Color verde para los botones
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.cobro),
                                contentDescription = "Realizar Cobro",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "REALIZAR COBRO", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate(Screen.PrestamoForm) },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonGreen), // Color verde para los botones
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.prestamo),
                                contentDescription = "Realizar Prestamo",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "REALIZAR PRÉSTAMO", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate(Screen.HistorialCobros) },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonGreen), // Color verde para los botones
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.cuadre),
                                contentDescription = "Cuadre General",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "VER HISTORIAL", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate(Screen.ClientesList) },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonGreen), // Color verde para los botones
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.person),
                                contentDescription = "Clientes",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "CLIENTES", color = Color.White)
                        }
                    }
                }
            }

            // Box para configuración
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(lowerBackground, shape = RoundedCornerShape(32.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Configuración",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CONFIGURACIÓN",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                    )
                }
            }
        }
    }
}

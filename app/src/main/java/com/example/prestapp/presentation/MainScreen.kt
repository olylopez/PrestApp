package com.example.prestapp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val primaryTextColor = MaterialTheme.colorScheme.onBackground
    val buttonGreen = Color(0xFF4CAF50) // Verde para los botones
    val lowerBackground = Color(0xFF388E3C) // Fondo verde oscuro para la parte inferior

    // Definir tipografías personalizadas
    val lobsterFont = FontFamily(Font(R.font.lobster_regular))
    val robotoFont = FontFamily(Font(R.font.roboto_regular))

    var showDialog by remember { mutableStateOf(false) }

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

                Spacer(modifier = Modifier.height(16.dp))

                // Logo
                Image(
                    painter = painterResource(R.drawable.descarga),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

            }


            Text(text = "PREST APP", style = MaterialTheme.typography.headlineLarge.copy(color = primaryTextColor, fontSize = 32.sp, fontFamily = lobsterFont, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            // Área inferior con fondo verde oscuro y botones
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Botón Realizar Cobro
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(color = buttonGreen, shape = CircleShape)
                            .clickable { navController.navigate(Screen.ClientesList) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.cobro),
                                contentDescription = "Realizar Cobro",
                                modifier = Modifier.size(40.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "COBRO", color = Color.White, textAlign = TextAlign.Center, fontSize = 10.sp)
                        }
                    }

                    // Botón Realizar Préstamo
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(color = buttonGreen, shape = CircleShape)
                            .clickable { navController.navigate(Screen.PrestamoForm) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.prestamo),
                                contentDescription = "Realizar Préstamo",
                                modifier = Modifier.size(40.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "PRÉSTAMO", color = Color.White, textAlign = TextAlign.Center, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Botón Ver Historial
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(color = buttonGreen, shape = CircleShape)
                            .clickable { navController.navigate(Screen.HistorialCobros) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.cuadre),
                                contentDescription = "Cuadre General",
                                modifier = Modifier.size(40.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "HISTORIAL", color = Color.White, textAlign = TextAlign.Center, fontSize = 10.sp)
                        }
                    }

                    // Botón Clientes
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(color = buttonGreen, shape = CircleShape)
                            .clickable { navController.navigate(Screen.ClientesList) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.person),
                                contentDescription = "Clientes",
                                modifier = Modifier.size(40.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "CLIENTES", color = Color.White, textAlign = TextAlign.Center, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón de configuración
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(color = buttonGreen, shape = CircleShape)
                        .clickable { showDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Configuración",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Configuración",
                    color = primaryTextColor,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold
                )
            }


            if (showDialog) {
                ConfiguracionDialog(
                    onDismissRequest = { showDialog = false },
                    onUsersClick = { navController.navigate(Screen.UsuariosList.toString()); showDialog = false },
                    onRutasClick = { navController.navigate(Screen.RutaScreen.toString()); showDialog = false }
                )
            }
        }
    }
}

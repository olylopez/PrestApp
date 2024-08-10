package com.example.prestapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.prestapp.presentation.cliente.ClienteViewModel
import com.example.prestapp.presentation.ruta.RutaViewModel
import com.example.prestapp.screens.PrestAppNavHost
import com.example.prestapp.ui.theme.PrestAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var connectivityReceiver: ConnectivityReceiver

    private val viewModel: RutaViewModel by viewModels()
    private val viewModelC: ClienteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrestAppTheme {
                val navHostController = rememberNavController()
                PrestAppNavHost(navHostController = navHostController)
            }
        }
        observeConnectivity()
    }

    private fun observeConnectivity() {
        lifecycleScope.launch {
            connectivityReceiver.isConnectedFlow.collect { isConnected ->
                if (isConnected) {
                    viewModel.syncRutas()
                    viewModelC.syncClientes()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModelC.handlePictureResult(requestCode, resultCode, data)
    }
}


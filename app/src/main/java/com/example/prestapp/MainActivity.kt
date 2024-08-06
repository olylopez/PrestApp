package com.example.prestapp

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
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
                }
            }
        }
    }
}


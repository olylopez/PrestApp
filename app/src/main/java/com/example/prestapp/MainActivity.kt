package com.example.prestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.prestapp.screens.PrestAppNavHost
import com.example.prestapp.ui.theme.PrestAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrestAppTheme {
                val navHostController = rememberNavController()
                PrestAppNavHost(navHostController = navHostController)
            }
        }
    }
}


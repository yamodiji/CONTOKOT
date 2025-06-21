package com.speedDrawer.speed_drawer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.speedDrawer.speed_drawer.presentation.screens.HomeScreen
import com.speedDrawer.speed_drawer.presentation.theme.SpeedDrawerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // Permission launcher for accessing installed apps
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, app can proceed
        } else {
            // Permission denied, show explanation or request again
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        // Request necessary permissions
        requestPermissions()
        
        setContent {
            SpeedDrawerTheme {
                val systemUiController = rememberSystemUiController()
                val isDarkTheme = !MaterialTheme.colorScheme.background.luminance.let { it > 0.5f }
                
                // Set system bars to be transparent
                LaunchedEffect(isDarkTheme) {
                    systemUiController.setSystemBarsColor(
                        color = androidx.compose.ui.graphics.Color.Transparent,
                        darkIcons = !isDarkTheme
                    )
                }
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
    
    private fun requestPermissions() {
        // Check if we need to request query all packages permission for Android 11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // This permission is automatically granted for launcher apps
            // but you may need to add it to AndroidManifest.xml
        }
        
        // Check for other permissions if needed
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.VIBRATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.VIBRATE)
        }
    }
}

@Composable
private fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("settings") {
            // TODO: Implement SettingsScreen
            // SettingsScreen(
            //     onNavigateBack = {
            //         navController.popBackStack()
            //     }
            // )
        }
    }
} 
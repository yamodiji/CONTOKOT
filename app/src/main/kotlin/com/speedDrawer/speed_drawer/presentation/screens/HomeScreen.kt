package com.speedDrawer.speed_drawer.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.speedDrawer.speed_drawer.presentation.components.AppGridComponent
import com.speedDrawer.speed_drawer.presentation.components.SearchBarComponent
import com.speedDrawer.speed_drawer.presentation.viewmodel.AppViewModel
import com.speedDrawer.speed_drawer.presentation.viewmodel.SettingsViewModel
import com.speedDrawer.speed_drawer.util.Constants
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    appViewModel: AppViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    // Collect state from ViewModels
    val isLoading by appViewModel.isLoading.collectAsState()
    val searchQuery by appViewModel.searchQuery.collectAsState()
    val displayApps by appViewModel.displayApps.collectAsState()
    val errorMessage by appViewModel.errorMessage.collectAsState()
    
    // Settings state
    val iconSize by settingsViewModel.iconSize.collectAsState()
    val autoFocus by settingsViewModel.autoFocus.collectAsState()
    val showKeyboard by settingsViewModel.showKeyboard.collectAsState()
    val clearSearchOnClose by settingsViewModel.clearSearchOnClose.collectAsState()
    val vibrationEnabled by settingsViewModel.isVibrationEnabled.collectAsState()
    val animationsEnabled by settingsViewModel.areAnimationsEnabled.collectAsState()
    
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Handle lifecycle events (equivalent to Flutter's didChangeAppLifecycleState)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // App resumed - refresh apps if needed
                    appViewModel.refreshApps()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    // App paused - clear search if setting is enabled
                    if (clearSearchOnClose) {
                        appViewModel.clearSearch()
                    }
                }
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            appViewModel.clearError()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = Constants.PADDING_MEDIUM),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search bar
            SearchBarComponent(
                query = searchQuery,
                onQueryChange = { query ->
                    appViewModel.search(query)
                },
                onClear = {
                    appViewModel.clearSearch()
                },
                onSettingsClick = onNavigateToSettings,
                autoFocus = autoFocus && showKeyboard,
                modifier = Modifier.padding(horizontal = Constants.PADDING_MEDIUM)
            )
            
            Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))
            
            // Content area
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    else -> {
                        AppGridComponent(
                            apps = displayApps,
                            onAppTap = { app ->
                                // Provide haptic feedback if enabled
                                coroutineScope.launch {
                                    val success = appViewModel.launchApp(app)
                                    if (!success) {
                                        snackbarHostState.showSnackbar(
                                            message = "Failed to launch ${app.displayName}",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            },
                            onAppLongPress = { app ->
                                // Show app options dialog
                                showAppOptionsDialog(app, appViewModel, snackbarHostState, coroutineScope)
                            },
                            iconSize = iconSize.dp,
                            animationsEnabled = animationsEnabled,
                            vibrationEnabled = vibrationEnabled
                        )
                    }
                }
            }
        }
    }
}

/**
 * Show app options dialog (equivalent to Flutter's _showAppOptions)
 */
private fun showAppOptionsDialog(
    app: com.speedDrawer.speed_drawer.data.model.AppInfo,
    appViewModel: AppViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    // TODO: Implement app options dialog
    // This would typically show options like:
    // - Add/Remove from favorites
    // - App info
    // - Uninstall (if possible)
    
    // For now, just toggle favorite status
    appViewModel.toggleFavorite(app)
    
    coroutineScope.launch {
        val message = if (app.isFavorite) {
            "Removed ${app.displayName} from favorites"
        } else {
            "Added ${app.displayName} to favorites"
        }
        snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
    }
} 
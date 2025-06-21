package com.speedDrawer.speed_drawer.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.speedDrawer.speed_drawer.data.model.AppInfo
import com.speedDrawer.speed_drawer.util.Constants

@Composable
fun AppGridComponent(
    apps: List<AppInfo>,
    onAppTap: (AppInfo) -> Unit,
    onAppLongPress: (AppInfo) -> Unit,
    iconSize: Dp = Constants.MEDIUM_ICON_SIZE,
    animationsEnabled: Boolean = true,
    vibrationEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (apps.isEmpty()) {
        EmptyStateComponent(
            modifier = modifier.fillMaxSize()
        )
    } else {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        
        // Calculate optimal number of columns based on icon size and screen width
        val itemWidth = iconSize + Constants.PADDING_MEDIUM * 2
        val columnsCount = (screenWidth / itemWidth).toInt().coerceIn(3, 6)
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnsCount),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(Constants.PADDING_SMALL),
            horizontalArrangement = Arrangement.spacedBy(Constants.PADDING_SMALL),
            verticalArrangement = Arrangement.spacedBy(Constants.PADDING_SMALL)
        ) {
            items(
                items = apps,
                key = { app -> app.packageName }
            ) { app ->
                AppItemComponent(
                    app = app,
                    onTap = { onAppTap(app) },
                    onLongPress = { onAppLongPress(app) },
                    iconSize = iconSize,
                    animationsEnabled = animationsEnabled,
                    vibrationEnabled = vibrationEnabled,
                    modifier = Modifier.animateItemPlacement()
                )
            }
            
            // Add bottom padding
            item {
                Spacer(modifier = Modifier.height(Constants.PADDING_LARGE))
            }
        }
    }
}

@Composable
private fun EmptyStateComponent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = "No apps found",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))
        
        Text(
            text = "No apps found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Constants.PADDING_SMALL))
        
        Text(
            text = "Try adjusting your search",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            textAlign = TextAlign.Center
        )
    }
} 
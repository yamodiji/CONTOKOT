package com.speedDrawer.speed_drawer.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.speedDrawer.speed_drawer.data.model.AppInfo
import com.speedDrawer.speed_drawer.util.Constants

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItemComponent(
    app: AppInfo,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    iconSize: Dp = Constants.MEDIUM_ICON_SIZE,
    animationsEnabled: Boolean = true,
    vibrationEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    // Animation spring configuration
    val animationSpec = if (animationsEnabled) {
        spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    } else {
        spring<Float>(stiffness = Spring.StiffnessHigh)
    }
    
    Card(
        modifier = modifier
            .animateContentSize(animationSpec = animationSpec)
            .combinedClickable(
                onClick = onTap,
                onLongClick = {
                    if (vibrationEnabled) {
                        haptic.performHapticFeedback(
                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                        )
                    }
                    onLongPress()
                }
            ),
        shape = RoundedCornerShape(Constants.BORDER_RADIUS),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (app.isFavorite) 6.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Constants.PADDING_SMALL),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Constants.PADDING_SMALL)
        ) {
            // App icon with favorite indicator
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                // App icon background
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(RoundedCornerShape(iconSize * 0.2f))
                        .background(
                            if (app.isFavorite) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            } else {
                                Color.Transparent
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // App icon using Coil for async loading
                    AsyncImage(
                        model = app.icon,
                        contentDescription = "${app.displayName} icon",
                        modifier = Modifier
                            .size(iconSize * 0.8f)
                            .clip(RoundedCornerShape(iconSize * 0.15f))
                    )
                }
                
                // Favorite star indicator
                if (app.isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(iconSize * 0.25f)
                            .offset(x = (4).dp, y = (-4).dp)
                    )
                }
            }
            
            // App name
            Text(
                text = app.displayName,
                fontSize = (iconSize.value * 0.2f).sp,
                fontWeight = if (app.isFavorite) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Launch count indicator for most used apps
            if (app.launchCount > 0) {
                Text(
                    text = "${app.launchCount}",
                    fontSize = (iconSize.value * 0.15f).sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
} 
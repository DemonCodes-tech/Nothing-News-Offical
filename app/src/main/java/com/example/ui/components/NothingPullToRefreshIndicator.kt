package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.SignalRed
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NothingPullToRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS

    if (!isNothing) {
        // Official Google Material 3 Pull-to-Refresh Indicator with Dynamic Wallpaper Color
        PullToRefreshDefaults.Indicator(
            state = state,
            isRefreshing = isRefreshing,
            modifier = modifier,
            color = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    } else {
        // Only appear when pulling or actively refreshing
        val distance = state.distanceFraction
        if (!isRefreshing && distance <= 0.05f) {
            return
        }

        val pullProgress = distance.coerceIn(0f, 1.3f)
        val alphaProgress = if (isRefreshing) 1f else (pullProgress * 1.5f).coerceIn(0f, 1f)
        val scaleProgress = if (isRefreshing) 1f else (0.7f + 0.3f * pullProgress.coerceAtMost(1f))
        val verticalOffsetDp = if (isRefreshing) 16.dp else (48 * pullProgress.coerceAtMost(1f)).dp

        // Bespoke Nothing OS Micro-LED Industrial Pull-to-Refresh Indicator
        val infiniteTransition = rememberInfiniteTransition(label = "nothing_refresh_rotation")
        val rotationAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing)
            ),
            label = "spin_angle"
        )

        val pillShape = RoundedCornerShape(9999.dp)

        Box(
            modifier = modifier
                .graphicsLayer {
                    translationY = verticalOffsetDp.toPx()
                    scaleX = scaleProgress
                    scaleY = scaleProgress
                    alpha = alphaProgress
                }
                .clip(pillShape)
                .background(Color(0xFF141414))
                .border(1.dp, Color(0xFF2E2E2E), pillShape)
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Micro-LED Ring
                Canvas(
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer {
                            rotationZ = if (isRefreshing) rotationAngle else (pullProgress * 270f)
                        }
                ) {
                    val radius = size.minDimension / 2.2f
                    val center = Offset(size.width / 2, size.height / 2)
                    val dotCount = 8

                    for (i in 0 until dotCount) {
                        val angleRad = Math.toRadians((i * (360f / dotCount)).toDouble())
                        val x = center.x + radius * cos(angleRad).toFloat()
                        val y = center.y + radius * sin(angleRad).toFloat()

                        val isLead = i == 0
                        drawCircle(
                            color = if (isLead) SignalRed else Color.White.copy(alpha = (i + 1f) / dotCount),
                            radius = if (isLead) 1.6.dp.toPx() else 1.1.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isRefreshing) "CHECKING 3 SOURCES..." else "PULL FOR FACT AUDIT",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}

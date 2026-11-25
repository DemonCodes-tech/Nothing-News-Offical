package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.SignalRed
import kotlin.math.roundToInt

internal fun computeActiveDotCount(progress: Float, totalDots: Int): Int {
    if (totalDots <= 0) return 0
    val safeProgress = progress.coerceIn(0f, 1f)
    if (safeProgress <= 0.005f) return 0
    return (safeProgress * totalDots).roundToInt().coerceIn(1, totalDots)
}

@Composable
fun SegmentedProgressBar(
    progress: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier,
    totalSegments: Int = 12,
    showLabel: Boolean = true,
    labelPrefix: String = "READ PROGRESS",
    dotSize: Dp = 7.dp
) {
    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS
    val isDarkTheme = LocalIsDarkTheme.current

    val safeProgress = progress.coerceIn(0f, 1f)
    val activeDots = computeActiveDotCount(safeProgress, totalSegments)
    val percentageInt = (safeProgress * 100).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$labelPrefix: $percentageInt%"
            }
    ) {
        if (showLabel) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = labelPrefix,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "$percentageInt%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isNothing && activeDots > 0) SignalRed else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (isNothing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until totalSegments) {
                    val isActive = i < activeDots
                    val dotColor by animateColorAsState(
                        targetValue = if (isActive) SignalRed else NothingBlack,
                        animationSpec = tween(durationMillis = 220),
                        label = "nothing_progress_dot_color_$i"
                    )
                    val borderColor = if (isActive) {
                        SignalRed
                    } else if (isDarkTheme) {
                        Color(0xFF3A3A3A)
                    } else {
                        NothingBlack
                    }

                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(dotColor)
                            .border(
                                width = 1.dp,
                                color = borderColor,
                                shape = CircleShape
                            )
                    )
                }
            }
        } else {
            SquiggleProgressBar(
                progress = safeProgress,
                scrollPhaseFraction = safeProgress
            )
        }
    }
}


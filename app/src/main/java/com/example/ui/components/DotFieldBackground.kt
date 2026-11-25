package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SignalRed
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class DotPulseRipple(
    val xNorm: Float,
    val yNorm: Float,
    val ageSec: Float = 0f
)

/**
 * Computes normalized pulse intensity [0f..1f] for a Signal Red dot in the Nothing OS matrix
 * by combining two continuous traveling pulse waves with any active touch-spawned radial ripples.
 */
fun computeNothingDotPulse(
    dotXNorm: Float,
    dotYNorm: Float,
    timeSec: Float,
    ripples: List<DotPulseRipple> = emptyList()
): Float {
    val waveA = 0.5f + 0.5f * sin(dotXNorm * 7.5f + dotYNorm * 5.2f - timeSec * 2.5f)
    val waveB = 0.5f + 0.5f * cos(dotXNorm * 4.2f - dotYNorm * 6.8f + timeSec * 1.8f)
    var intensity = waveA * 0.58f + waveB * 0.42f

    for (ripple in ripples) {
        val dist = hypot(dotXNorm - ripple.xNorm, dotYNorm - ripple.yNorm)
        val ringRadius = ripple.ageSec * 0.88f
        val ringWidth = 0.14f
        val diff = abs(dist - ringRadius)
        if (diff < ringWidth) {
            val fade = (1f - ripple.ageSec / 1.15f).coerceIn(0f, 1f)
            val ringPeak = (1f - diff / ringWidth) * fade
            intensity += ringPeak * 0.75f
        }
    }
    return intensity.coerceIn(0f, 1f)
}

/**
 * Nothing OS Signal Red pulsing dot matrix background with continuous traveling pulse waves
 * and touch-reactive shockwave ripples + mechanical haptic clicks.
 * Completely omitted in Material You mode.
 */
@Composable
fun DotFieldBackground(
    inTutorialScope: Boolean = false,
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    if (designStyle != DesignStyle.NOTHING_OS) {
        return
    }

    val isDark = LocalIsDarkTheme.current
    val touchBus = LocalAmbientTouchBus.current
    val hapticEngine = LocalHapticEngine.current

    var timeSec by remember { mutableFloatStateOf(0f) }
    var ripples by remember { mutableStateOf<List<DotPulseRipple>>(emptyList()) }

    LaunchedEffect(touchBus, inTutorialScope) {
        touchBus?.events?.collect { event ->
            if (event.inTutorial == inTutorialScope) {
                ripples = (ripples.takeLast(5) + DotPulseRipple(event.xNorm, event.yNorm, 0f))
                if (touchBus.tryClaimDotHaptic(event.timestampNanos, event.isDrag)) {
                    hapticEngine?.trigger(
                        if (event.isDrag) HapticCue.DOT_DRAG_TICK else HapticCue.DOT_PULSE_TAP
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { frameNanos ->
                if (lastNanos != 0L) {
                    val dt = ((frameNanos - lastNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
                    timeSec += dt
                    if (ripples.isNotEmpty()) {
                        ripples = ripples
                            .map { it.copy(ageSec = it.ageSec + dt) }
                            .filter { it.ageSec < 1.15f }
                    }
                }
                lastNanos = frameNanos
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag(if (inTutorialScope) "nothing_tutorial_red_dot_field" else "nothing_red_dot_field")
    ) {
        val spacingPx = 26.dp.toPx()
        val baseRadiusPx = 1.05.dp.toPx()
        val maxExtraRadiusPx = 1.45.dp.toPx()

        val columns = (size.width / spacingPx).toInt() + 1
        val rows = (size.height / spacingPx).toInt() + 1

        val startX = (size.width - (columns - 1) * spacingPx) / 2f
        val startY = (size.height - (rows - 1) * spacingPx) / 2f
        val safeW = size.width.coerceAtLeast(1f)
        val safeH = size.height.coerceAtLeast(1f)

        val minAlpha = if (isDark) 0.16f else 0.12f
        val alphaSpan = if (isDark) 0.60f else 0.50f

        for (i in 0 until columns) {
            val xPx = startX + i * spacingPx
            val xNorm = (xPx / safeW).coerceIn(0f, 1f)
            for (j in 0 until rows) {
                val yPx = startY + j * spacingPx
                val yNorm = (yPx / safeH).coerceIn(0f, 1f)
                val pulse = computeNothingDotPulse(
                    dotXNorm = xNorm,
                    dotYNorm = yNorm,
                    timeSec = timeSec,
                    ripples = ripples
                )
                drawCircle(
                    color = SignalRed.copy(alpha = minAlpha + alphaSpan * pulse),
                    radius = baseRadiusPx + maxExtraRadiusPx * pulse,
                    center = Offset(xPx, yPx)
                )
            }
        }
    }
}

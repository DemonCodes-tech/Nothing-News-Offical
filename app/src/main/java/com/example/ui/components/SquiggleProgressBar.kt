package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

internal fun computeSquigglePhaseRadians(
    scrollFraction: Float,
    driftCycles: Float,
    scrollWaveCycles: Float = 5.0f
): Float {
    val safeScroll = scrollFraction.coerceIn(0f, 1f)
    val twoPi = (2.0 * PI).toFloat()
    return (driftCycles * twoPi) + (safeScroll * scrollWaveCycles * twoPi)
}

internal fun computeSquiggleWaveY(
    xPx: Float,
    startXPx: Float,
    centerY: Float,
    amplitudePx: Float,
    wavelengthPx: Float,
    phaseRadians: Float
): Float {
    if (wavelengthPx <= 0f || amplitudePx <= 0f) return centerY
    val distancePx = (xPx - startXPx).coerceAtLeast(0f)
    val envelope = (distancePx / (wavelengthPx * 0.35f)).coerceIn(0f, 1f)
    val angle = ((distancePx / wavelengthPx) * (2.0 * PI).toFloat()) + phaseRadians
    return centerY + sin(angle) * amplitudePx * envelope
}

/**
 * Expressive Material 3 wavy/squiggling progress indicator.
 * Combines continuous ambient wave drift with scroll-driven phase motion
 * and renders a riding endpoint dot ahead of a muted remaining track.
 */
@Composable
fun SquiggleProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    scrollPhaseFraction: Float = progress,
    trackHeight: Dp = 14.dp,
    waveAmplitude: Dp = 3.2.dp,
    wavelength: Dp = 22.dp,
    strokeWidth: Dp = 3.dp,
    dotRadius: Dp = 4.4.dp,
    scrollWaveCycles: Float = 5.0f,
    driftDurationMillis: Int = 4200,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.34f),
    accessibilityLabel: String = "Reading progress: ${(progress.coerceIn(0f, 1f) * 100).toInt()}%"
) {
    val safeProgress = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = safeProgress,
        animationSpec = tween(durationMillis = 120, easing = LinearEasing),
        label = "squiggle_progress_fill"
    )
    val animatedScrollPhase by animateFloatAsState(
        targetValue = scrollPhaseFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 90, easing = LinearEasing),
        label = "squiggle_scroll_phase"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "squiggle_drift_clock")
    val driftCycles by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = driftDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "squiggle_drift_cycles"
    )

    val phaseRadians = computeSquigglePhaseRadians(
        scrollFraction = animatedScrollPhase,
        driftCycles = driftCycles,
        scrollWaveCycles = scrollWaveCycles
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .semantics {
                contentDescription = accessibilityLabel
            }
    ) {
        val strokePx = strokeWidth.toPx().coerceAtLeast(1.5f)
        val dotRadiusPx = dotRadius.toPx().coerceAtLeast(strokePx)
        val haloRadiusPx = dotRadiusPx * 1.65f
        val horizontalMargin = haloRadiusPx + 1.dp.toPx()
        val usableWidth = (size.width - horizontalMargin * 2f).coerceAtLeast(1f)
        val startX = horizontalMargin
        val endX = startX + usableWidth
        val centerY = size.height / 2f

        val maxSafeAmplitude = ((size.height / 2f) - haloRadiusPx * 0.65f).coerceAtLeast(1f)
        val amplitudePx = waveAmplitude.toPx().coerceAtMost(maxSafeAmplitude)
        val wavelengthPx = wavelength.toPx().coerceAtLeast(8.dp.toPx())

        val activeTipX = (startX + usableWidth * animatedProgress).coerceIn(startX, endX)
        val trackGapPx = dotRadiusPx + strokePx * 1.4f
        val trackStartX = if (animatedProgress > 0.005f) {
            (activeTipX + trackGapPx).coerceAtMost(endX)
        } else {
            (startX + trackGapPx).coerceAtMost(endX)
        }

        // Remaining muted track after the riding end dot
        if (trackStartX < endX - 1f) {
            drawLine(
                color = trackColor,
                start = Offset(trackStartX, centerY),
                end = Offset(endX, centerY),
                strokeWidth = (strokePx * 0.85f).coerceAtLeast(1.5f),
                cap = StrokeCap.Round
            )
        }

        // Active squiggle wave path
        val tipY = if (animatedProgress > 0.005f) {
            val path = Path()
            val stepPx = 2.5f
            path.moveTo(startX, centerY)
            var currentX = startX + stepPx
            while (currentX < activeTipX) {
                val y = computeSquiggleWaveY(
                    xPx = currentX,
                    startXPx = startX,
                    centerY = centerY,
                    amplitudePx = amplitudePx,
                    wavelengthPx = wavelengthPx,
                    phaseRadians = phaseRadians
                )
                path.lineTo(currentX, y)
                currentX += stepPx
            }
            val finalY = computeSquiggleWaveY(
                xPx = activeTipX,
                startXPx = startX,
                centerY = centerY,
                amplitudePx = amplitudePx,
                wavelengthPx = wavelengthPx,
                phaseRadians = phaseRadians
            )
            path.lineTo(activeTipX, finalY)

            drawPath(
                path = path,
                color = activeColor,
                style = Stroke(
                    width = strokePx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            finalY
        } else {
            centerY
        }

        // Expressive riding endpoint dot with subtle M3 halo
        val dotCenter = Offset(activeTipX, tipY)
        drawCircle(
            color = activeColor.copy(alpha = if (animatedProgress > 0.005f) 0.22f else 0.14f),
            radius = haloRadiusPx,
            center = dotCenter
        )
        drawCircle(
            color = activeColor,
            radius = dotRadiusPx,
            center = dotCenter
        )
    }
}

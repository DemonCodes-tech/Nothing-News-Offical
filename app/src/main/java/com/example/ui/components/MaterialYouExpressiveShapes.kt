package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.ExpressiveMorphShape
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.buildMorphingExpressivePath
import com.example.ui.viewmodel.AppScreen
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

enum class ExpressiveShapeKind(val morphShape: ExpressiveMorphShape) {
    ORGANIC_FLOWER(ExpressiveMorphShape.ORGANIC_FLOWER),
    SCALLOPED_SQUIRCLE(ExpressiveMorphShape.SCALLOPED_SQUIRCLE),
    CLOVER(ExpressiveMorphShape.CLOVER),
    SOFT_PILL(ExpressiveMorphShape.SOFT_PILL),
    BURST_STAR(ExpressiveMorphShape.BURST_STAR),
    ROUNDED_DIAMOND(ExpressiveMorphShape.ROUNDED_DIAMOND),
    SOFT_CIRCLE(ExpressiveMorphShape.SOFT_CIRCLE),
    ORGANIC_BLOB(ExpressiveMorphShape.ORGANIC_BLOB),
    SOFT_HEART(ExpressiveMorphShape.SOFT_HEART)
}

/**
 * Physics state for an always-present, roaming, rebounding, touch-reactive,
 * and continuously morphing Material 3 Expressive shape particle.
 */
data class ExpressiveRoamerParticle(
    val id: Int,
    val xNorm: Float,
    val yNorm: Float,
    val vx: Float,
    val vy: Float,
    val sizeFraction: Float,
    val rotationDeg: Float,
    val rotationSpeedDeg: Float,
    val morphCursor: Float,
    val morphSpeed: Float,
    val curvePhase: Float,
    val curveFreq: Float,
    val colorSlot: Int,
    val baseAlpha: Float,
    val shapeStride: Int = 1,
    val hitBoost: Float = 0f,
    val lastHitNanos: Long = 0L
)

/**
 * Tests whether a normalized touch coordinate lands within [particle]'s interactive hit area.
 */
fun hitTestExpressiveParticle(
    particle: ExpressiveRoamerParticle,
    touchXNorm: Float,
    touchYNorm: Float,
    viewportWidth: Float,
    viewportHeight: Float,
    minTouchRadiusPx: Float = 96f,
    maxHitRadiusPx: Float = 420f
): Boolean {
    if (viewportWidth <= 1f || viewportHeight <= 1f) return false
    val baseDim = min(viewportWidth, viewportHeight)
    val visualRadius = (baseDim * particle.sizeFraction * 0.52f).coerceIn(minTouchRadiusPx, maxHitRadiusPx)
    val dx = (touchXNorm - particle.xNorm) * viewportWidth
    val dy = (touchYNorm - particle.yNorm) * viewportHeight
    return hypot(dx, dy) <= visualRadius
}

/**
 * Applies an instant morph leap, scale pop, and elastic scatter bounce when the user
 * touches or swipes through [particle].
 */
fun applyShapeTouchReaction(
    particle: ExpressiveRoamerParticle,
    touchXNorm: Float,
    touchYNorm: Float,
    nowNanos: Long = 0L,
    advanceMorph: Boolean = true
): ExpressiveRoamerParticle {
    var dx = particle.xNorm - touchXNorm
    var dy = particle.yNorm - touchYNorm
    val dist = hypot(dx, dy)
    if (dist < 0.005f) {
        val fallbackAngle = (particle.id * 1.15f) + Math.toRadians(particle.rotationDeg.toDouble()).toFloat()
        dx = cos(fallbackAngle)
        dy = sin(fallbackAngle)
    } else {
        dx /= dist
        dy /= dist
    }

    val scatterSpeed = 0.115f
    val shapeCount = ExpressiveMorphShape.entries.size.toFloat()
    val nextMorphCursor = if (advanceMorph) {
        (particle.morphCursor + 1.0f) % shapeCount
    } else {
        particle.morphCursor
    }
    val flippedRotation = if (particle.rotationSpeedDeg >= 0f) -26f else 26f

    return particle.copy(
        vx = dx * scatterSpeed,
        vy = dy * scatterSpeed,
        rotationSpeedDeg = flippedRotation,
        morphCursor = nextMorphCursor,
        hitBoost = 1.0f,
        lastHitNanos = nowNanos
    )
}

/**
 * Advances a single roaming particle by [dtSec]:
 * - Applies smooth sinusoidal perpendicular curl so trajectories curve organically across the screen
 * - Bounces smoothly off [minNorm]..[maxNorm] viewport boundaries like buoyant bubbles
 * - Decays transient [hitBoost] and settles boosted scatter speeds back into calm drift
 * - Advances continuous shape morph cursor & gentle rotation
 */
fun stepExpressiveParticle(
    particle: ExpressiveRoamerParticle,
    dtSec: Float,
    minNorm: Float = 0.06f,
    maxNorm: Float = 0.94f,
    minSpeed: Float = 0.048f,
    maxSpeed: Float = 0.115f
): ExpressiveRoamerParticle {
    val safeDt = dtSec.coerceIn(0f, 0.05f)
    if (safeDt <= 0f) return particle

    val nextPhase = (particle.curvePhase + safeDt * particle.curveFreq) % (Math.PI.toFloat() * 2f)
    val rawSpeed = hypot(particle.vx, particle.vy).coerceAtLeast(0.0001f)
    val calmTargetSpeed = rawSpeed.coerceIn(minSpeed, maxSpeed)

    // Gently steer velocity perpendicular to motion via sinusoidal curve force
    val perpX = -particle.vy / rawSpeed
    val perpY = particle.vx / rawSpeed
    val steer = sin(nextPhase) * 0.045f * safeDt

    var nextVx = particle.vx + perpX * steer
    var nextVy = particle.vy + perpY * steer

    val newSpeed = hypot(nextVx, nextVy).coerceAtLeast(0.0001f)
    nextVx = (nextVx / newSpeed) * calmTargetSpeed
    nextVy = (nextVy / newSpeed) * calmTargetSpeed

    var nextX = particle.xNorm + nextVx * safeDt
    var nextY = particle.yNorm + nextVy * safeDt

    if (nextX < minNorm) {
        nextX = minNorm + (minNorm - nextX).coerceAtMost(0.05f)
        nextVx = kotlin.math.abs(nextVx)
    } else if (nextX > maxNorm) {
        nextX = maxNorm - (nextX - maxNorm).coerceAtMost(0.05f)
        nextVx = -kotlin.math.abs(nextVx)
    }

    if (nextY < minNorm) {
        nextY = minNorm + (minNorm - nextY).coerceAtMost(0.05f)
        nextVy = kotlin.math.abs(nextVy)
    } else if (nextY > maxNorm) {
        nextY = maxNorm - (nextY - maxNorm).coerceAtMost(0.05f)
        nextVy = -kotlin.math.abs(nextVy)
    }

    val shapeCount = ExpressiveMorphShape.entries.size.toFloat()
    val nextMorphCursor = (particle.morphCursor + particle.morphSpeed * safeDt) % shapeCount
    val nextRotation = (particle.rotationDeg + particle.rotationSpeedDeg * safeDt + 360f) % 360f
    val nextHitBoost = (particle.hitBoost - safeDt * 2.2f).coerceAtLeast(0f)

    // Ease elevated spin speed smoothly back toward gentle ambient spin
    val targetBaseSpin = if (particle.rotationSpeedDeg >= 0f) 14f else -14f
    val nextRotSpeed = particle.rotationSpeedDeg + (targetBaseSpin - particle.rotationSpeedDeg) * (safeDt * 1.8f).coerceAtMost(1f)

    return particle.copy(
        xNorm = nextX.coerceIn(minNorm, maxNorm),
        yNorm = nextY.coerceIn(minNorm, maxNorm),
        vx = nextVx,
        vy = nextVy,
        rotationDeg = nextRotation,
        rotationSpeedDeg = nextRotSpeed,
        morphCursor = nextMorphCursor,
        curvePhase = nextPhase,
        hitBoost = nextHitBoost
    )
}

private fun resolveMorphPairAndProgress(particle: ExpressiveRoamerParticle): Triple<ExpressiveMorphShape, ExpressiveMorphShape, Float> {
    val shapes = ExpressiveMorphShape.entries
    val count = shapes.size
    val baseIdx = particle.morphCursor.toInt().coerceIn(0, count - 1)
    val frac = (particle.morphCursor - baseIdx).coerceIn(0f, 1f)
    val from = shapes[(baseIdx * particle.shapeStride) % count]
    val to = shapes[((baseIdx + 1) * particle.shapeStride) % count]
    return Triple(from, to, frac)
}

private fun createBackgroundParticles(): List<ExpressiveRoamerParticle> = listOf(
    ExpressiveRoamerParticle(
        id = 0,
        xNorm = 0.78f,
        yNorm = 0.16f,
        vx = -0.062f,
        vy = 0.048f,
        sizeFraction = 0.50f,
        rotationDeg = 12f,
        rotationSpeedDeg = 12f,
        morphCursor = 0.1f,
        morphSpeed = 0.28f,
        curvePhase = 0.3f,
        curveFreq = 0.9f,
        colorSlot = 0,
        baseAlpha = 0.34f,
        shapeStride = 1
    ),
    ExpressiveRoamerParticle(
        id = 1,
        xNorm = 0.16f,
        yNorm = 0.48f,
        vx = 0.055f,
        vy = -0.052f,
        sizeFraction = 0.44f,
        rotationDeg = 95f,
        rotationSpeedDeg = -14f,
        morphCursor = 2.4f,
        morphSpeed = 0.25f,
        curvePhase = 1.7f,
        curveFreq = 0.8f,
        colorSlot = 1,
        baseAlpha = 0.30f,
        shapeStride = 2
    ),
    ExpressiveRoamerParticle(
        id = 2,
        xNorm = 0.80f,
        yNorm = 0.82f,
        vx = -0.048f,
        vy = -0.058f,
        sizeFraction = 0.42f,
        rotationDeg = 40f,
        rotationSpeedDeg = 10f,
        morphCursor = 4.6f,
        morphSpeed = 0.31f,
        curvePhase = 3.1f,
        curveFreq = 1.1f,
        colorSlot = 2,
        baseAlpha = 0.32f,
        shapeStride = 1
    ),
    ExpressiveRoamerParticle(
        id = 3,
        xNorm = 0.28f,
        yNorm = 0.20f,
        vx = 0.068f,
        vy = 0.042f,
        sizeFraction = 0.32f,
        rotationDeg = 180f,
        rotationSpeedDeg = -16f,
        morphCursor = 6.2f,
        morphSpeed = 0.33f,
        curvePhase = 4.5f,
        curveFreq = 1.25f,
        colorSlot = 3,
        baseAlpha = 0.26f,
        shapeStride = 4
    ),
    ExpressiveRoamerParticle(
        id = 4,
        xNorm = 0.56f,
        yNorm = 0.62f,
        vx = 0.052f,
        vy = 0.064f,
        sizeFraction = 0.36f,
        rotationDeg = 240f,
        rotationSpeedDeg = 15f,
        morphCursor = 7.8f,
        morphSpeed = 0.27f,
        curvePhase = 2.4f,
        curveFreq = 0.95f,
        colorSlot = 1,
        baseAlpha = 0.28f,
        shapeStride = 2
    )
)

private fun createForegroundAccentParticles(compact: Boolean): List<ExpressiveRoamerParticle> =
    if (compact) {
        listOf(
            ExpressiveRoamerParticle(
                id = 101,
                xNorm = 0.86f,
                yNorm = 0.14f,
                vx = -0.072f,
                vy = 0.054f,
                sizeFraction = 0.10f,
                rotationDeg = 18f,
                rotationSpeedDeg = 18f,
                morphCursor = 1.2f,
                morphSpeed = 0.34f,
                curvePhase = 0.8f,
                curveFreq = 1.3f,
                colorSlot = 3,
                baseAlpha = 0.16f,
                shapeStride = 2
            ),
            ExpressiveRoamerParticle(
                id = 102,
                xNorm = 0.14f,
                yNorm = 0.82f,
                vx = 0.064f,
                vy = -0.066f,
                sizeFraction = 0.09f,
                rotationDeg = 110f,
                rotationSpeedDeg = -20f,
                morphCursor = 5.5f,
                morphSpeed = 0.30f,
                curvePhase = 2.9f,
                curveFreq = 1.15f,
                colorSlot = 4,
                baseAlpha = 0.15f,
                shapeStride = 1
            )
        )
    } else {
        listOf(
            ExpressiveRoamerParticle(
                id = 201,
                xNorm = 0.88f,
                yNorm = 0.18f,
                vx = -0.068f,
                vy = 0.056f,
                sizeFraction = 0.11f,
                rotationDeg = 0f,
                rotationSpeedDeg = 18f,
                morphCursor = 0.8f,
                morphSpeed = 0.32f,
                curvePhase = 0.4f,
                curveFreq = 1.2f,
                colorSlot = 3,
                baseAlpha = 0.16f,
                shapeStride = 2
            ),
            ExpressiveRoamerParticle(
                id = 202,
                xNorm = 0.12f,
                yNorm = 0.54f,
                vx = 0.058f,
                vy = 0.062f,
                sizeFraction = 0.095f,
                rotationDeg = 75f,
                rotationSpeedDeg = -16f,
                morphCursor = 3.7f,
                morphSpeed = 0.36f,
                curvePhase = 2.2f,
                curveFreq = 1.35f,
                colorSlot = 4,
                baseAlpha = 0.14f,
                shapeStride = 1
            ),
            ExpressiveRoamerParticle(
                id = 203,
                xNorm = 0.72f,
                yNorm = 0.86f,
                vx = -0.074f,
                vy = -0.052f,
                sizeFraction = 0.105f,
                rotationDeg = 160f,
                rotationSpeedDeg = 22f,
                morphCursor = 6.9f,
                morphSpeed = 0.29f,
                curvePhase = 4.1f,
                curveFreq = 1.1f,
                colorSlot = 0,
                baseAlpha = 0.18f,
                shapeStride = 4
            )
        )
    }

@Composable
private fun rememberPaletteSlotColors(): List<Color> {
    return listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary
    )
}

/**
 * Full-screen atmospheric underlay of always-present, roaming, rebounding, touch-reactive,
 * and continuously morphing Material 3 Expressive shapes across all app screens.
 * Completely removed whenever Nothing OS style is active.
 */
@Composable
fun MaterialYouExpressiveBackground(
    activeScreen: AppScreen? = null,
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    if (designStyle == DesignStyle.NOTHING_OS) {
        return
    }

    val touchBus = LocalAmbientTouchBus.current
    val hapticEngine = LocalHapticEngine.current
    val density = LocalDensity.current
    val minTouchRadiusPx = with(density) { 52.dp.toPx() }
    val maxHitRadiusPx = with(density) { 150.dp.toPx() }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var particles by remember { mutableStateOf(createBackgroundParticles()) }
    var elapsedSec by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(activeScreen) {
        particles = particles.mapIndexed { idx, p ->
            val sign = if (idx % 2 == 0) 1f else -1f
            p.copy(
                vx = (p.vx + sign * 0.015f).coerceIn(-0.11f, 0.11f),
                vy = (p.vy - sign * 0.012f).coerceIn(-0.11f, 0.11f)
            )
        }
    }

    LaunchedEffect(touchBus, canvasSize) {
        touchBus?.events?.collect { event ->
            if (!event.inTutorial && canvasSize.width > 1 && canvasSize.height > 1) {
                var anyHit = false
                val cooldown = if (event.isDrag) 200_000_000L else 60_000_000L
                particles = particles.map { p ->
                    val canHitAgain = event.timestampNanos - p.lastHitNanos >= cooldown
                    if (canHitAgain && hitTestExpressiveParticle(
                            particle = p,
                            touchXNorm = event.xNorm,
                            touchYNorm = event.yNorm,
                            viewportWidth = canvasSize.width.toFloat(),
                            viewportHeight = canvasSize.height.toFloat(),
                            minTouchRadiusPx = minTouchRadiusPx,
                            maxHitRadiusPx = maxHitRadiusPx
                        )
                    ) {
                        anyHit = true
                        applyShapeTouchReaction(p, event.xNorm, event.yNorm, event.timestampNanos)
                    } else {
                        p
                    }
                }
                if (anyHit && touchBus.tryClaimShapeHaptic(event.timestampNanos, event.isDrag)) {
                    hapticEngine?.trigger(
                        if (event.isDrag) HapticCue.SHAPE_DRAG_TICK else HapticCue.SHAPE_HIT
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
                    elapsedSec += dt
                    particles = particles.map { stepExpressiveParticle(it, dt, minNorm = 0.08f, maxNorm = 0.92f) }
                }
                lastNanos = frameNanos
            }
        }
    }

    val palette = rememberPaletteSlotColors()
    val reusablePath = remember { Path() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .testTag("material_you_expressive_background")
    ) {
        val baseDim = min(size.width, size.height)
        particles.forEachIndexed { idx, particle ->
            val breathe = 0.94f + 0.08f * sin(elapsedSec * 1.4f + idx * 1.3f)
            val popScale = 1f + 0.22f * particle.hitBoost
            val drawSize = baseDim * particle.sizeFraction * breathe * popScale
            val left = size.width * particle.xNorm - drawSize / 2f
            val top = size.height * particle.yNorm - drawSize / 2f

            val (from, to, progress) = resolveMorphPairAndProgress(particle)
            buildMorphingExpressivePath(
                size = Size(drawSize, drawSize),
                from = from,
                to = to,
                morphProgress = progress,
                rotationDegrees = particle.rotationDeg,
                outPath = reusablePath
            )
            val baseSlotColor = palette[particle.colorSlot % palette.size]
            val tint = baseSlotColor.copy(alpha = (particle.baseAlpha + particle.hitBoost * 0.16f).coerceAtMost(0.55f))
            translate(left = left, top = top) {
                drawPath(path = reusablePath, color = tint)
            }

            if (particle.hitBoost > 0.02f) {
                val ringRadius = (drawSize * 0.52f) * (1f + (1f - particle.hitBoost) * 0.35f)
                drawCircle(
                    color = palette[(particle.colorSlot + 3) % palette.size].copy(alpha = particle.hitBoost * 0.45f),
                    radius = ringRadius,
                    center = Offset(size.width * particle.xNorm, size.height * particle.yNorm),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

/**
 * Non-blocking, subtle foreground floating morphing shapes overlay so Material 3 Expressive
 * silhouettes stay visibly present anywhere around the screen, reacting with haptics, instant
 * morph leaps, and elastic scatter bounces without intercepting clicks or scrolls.
 * Completely removed in Nothing OS mode.
 */
@Composable
fun MaterialYouForegroundShapesOverlay(
    activeScreen: AppScreen? = null,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    if (designStyle == DesignStyle.NOTHING_OS) {
        return
    }

    val touchBus = LocalAmbientTouchBus.current
    val hapticEngine = LocalHapticEngine.current
    val density = LocalDensity.current
    val minTouchRadiusPx = with(density) { 46.dp.toPx() }
    val maxHitRadiusPx = with(density) { 80.dp.toPx() }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var particles by remember(compact) { mutableStateOf(createForegroundAccentParticles(compact)) }
    var elapsedSec by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(activeScreen) {
        particles = particles.mapIndexed { idx, p ->
            val flip = if (idx % 2 == 0) 1f else -1f
            p.copy(vx = (p.vx + flip * 0.01f).coerceIn(-0.11f, 0.11f))
        }
    }

    LaunchedEffect(touchBus, compact, canvasSize) {
        touchBus?.events?.collect { event ->
            if (event.inTutorial == compact && canvasSize.width > 1 && canvasSize.height > 1) {
                var anyHit = false
                val cooldown = if (event.isDrag) 200_000_000L else 60_000_000L
                particles = particles.map { p ->
                    val canHitAgain = event.timestampNanos - p.lastHitNanos >= cooldown
                    if (canHitAgain && hitTestExpressiveParticle(
                            particle = p,
                            touchXNorm = event.xNorm,
                            touchYNorm = event.yNorm,
                            viewportWidth = canvasSize.width.toFloat(),
                            viewportHeight = canvasSize.height.toFloat(),
                            minTouchRadiusPx = minTouchRadiusPx,
                            maxHitRadiusPx = maxHitRadiusPx
                        )
                    ) {
                        anyHit = true
                        applyShapeTouchReaction(p, event.xNorm, event.yNorm, event.timestampNanos)
                    } else {
                        p
                    }
                }
                if (anyHit && touchBus.tryClaimShapeHaptic(event.timestampNanos, event.isDrag)) {
                    hapticEngine?.trigger(
                        if (event.isDrag) HapticCue.SHAPE_DRAG_TICK else HapticCue.SHAPE_HIT
                    )
                }
            }
        }
    }

    LaunchedEffect(compact) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { frameNanos ->
                if (lastNanos != 0L) {
                    val dt = ((frameNanos - lastNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
                    elapsedSec += dt
                    particles = particles.map {
                        stepExpressiveParticle(
                            particle = it,
                            dtSec = dt,
                            minNorm = 0.05f,
                            maxNorm = 0.95f,
                            minSpeed = 0.052f,
                            maxSpeed = 0.115f
                        )
                    }
                }
                lastNanos = frameNanos
            }
        }
    }

    val palette = rememberPaletteSlotColors()
    val reusablePath = remember { Path() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .testTag(if (compact) "tutorial_foreground_shapes" else "material_you_foreground_shapes")
    ) {
        val baseDim = min(size.width, size.height)
        particles.forEachIndexed { idx, particle ->
            val pulse = 0.92f + 0.10f * sin(elapsedSec * 1.9f + idx * 1.7f)
            val popScale = 1f + 0.28f * particle.hitBoost
            val drawSize = (baseDim * particle.sizeFraction * pulse).coerceIn(24.dp.toPx(), 54.dp.toPx()) * popScale
            val left = size.width * particle.xNorm - drawSize / 2f
            val top = size.height * particle.yNorm - drawSize / 2f

            val (from, to, progress) = resolveMorphPairAndProgress(particle)
            buildMorphingExpressivePath(
                size = Size(drawSize, drawSize),
                from = from,
                to = to,
                morphProgress = progress,
                rotationDegrees = particle.rotationDeg,
                outPath = reusablePath
            )
            val baseSlotColor = palette[particle.colorSlot % palette.size]
            val tint = baseSlotColor.copy(alpha = (particle.baseAlpha + particle.hitBoost * 0.20f).coerceAtMost(0.42f))
            translate(left = left, top = top) {
                drawPath(path = reusablePath, color = tint)
            }

            if (particle.hitBoost > 0.02f) {
                val ringRadius = (drawSize * 0.56f) * (1f + (1f - particle.hitBoost) * 0.45f)
                drawCircle(
                    color = baseSlotColor.copy(alpha = particle.hitBoost * 0.55f),
                    radius = ringRadius,
                    center = Offset(size.width * particle.xNorm, size.height * particle.yNorm),
                    style = Stroke(width = 1.8.dp.toPx())
                )
            }
        }
    }
}

/**
 * Floating ambient Material 3 Expressive morphing shapes displayed inside the Tutorial
 * modal dialog backdrop when Material You is active. Completely removed in Nothing OS mode.
 */
@Composable
fun ExpressiveTutorialAmbientLayer(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    if (designStyle == DesignStyle.NOTHING_OS) {
        return
    }

    val touchBus = LocalAmbientTouchBus.current
    val hapticEngine = LocalHapticEngine.current
    val density = LocalDensity.current
    val minTouchRadiusPx = with(density) { 48.dp.toPx() }
    val maxHitRadiusPx = with(density) { 120.dp.toPx() }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var particles by remember {
        mutableStateOf(
            listOf(
                ExpressiveRoamerParticle(
                    id = 301,
                    xNorm = 0.84f,
                    yNorm = 0.14f,
                    vx = -0.060f,
                    vy = 0.052f,
                    sizeFraction = 0.42f,
                    rotationDeg = 0f,
                    rotationSpeedDeg = 14f,
                    morphCursor = 1.0f,
                    morphSpeed = 0.30f,
                    curvePhase = 0.2f,
                    curveFreq = 1.0f,
                    colorSlot = 1,
                    baseAlpha = 0.28f,
                    shapeStride = 1
                ),
                ExpressiveRoamerParticle(
                    id = 302,
                    xNorm = 0.16f,
                    yNorm = 0.84f,
                    vx = 0.056f,
                    vy = -0.058f,
                    sizeFraction = 0.38f,
                    rotationDeg = 120f,
                    rotationSpeedDeg = -16f,
                    morphCursor = 4.2f,
                    morphSpeed = 0.28f,
                    curvePhase = 1.9f,
                    curveFreq = 1.15f,
                    colorSlot = 0,
                    baseAlpha = 0.32f,
                    shapeStride = 2
                ),
                ExpressiveRoamerParticle(
                    id = 303,
                    xNorm = 0.50f,
                    yNorm = 0.48f,
                    vx = 0.064f,
                    vy = 0.044f,
                    sizeFraction = 0.30f,
                    rotationDeg = 45f,
                    rotationSpeedDeg = 12f,
                    morphCursor = 7.4f,
                    morphSpeed = 0.34f,
                    curvePhase = 3.5f,
                    curveFreq = 0.9f,
                    colorSlot = 2,
                    baseAlpha = 0.24f,
                    shapeStride = 4
                )
            )
        )
    }
    var elapsedSec by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(currentStep) {
        particles = particles.mapIndexed { idx, p ->
            val dir = if (idx % 2 == 0) 1f else -1f
            p.copy(
                vx = (p.vx + dir * 0.014f).coerceIn(-0.11f, 0.11f),
                morphCursor = (p.morphCursor + 0.35f) % ExpressiveMorphShape.entries.size.toFloat()
            )
        }
    }

    LaunchedEffect(touchBus, canvasSize) {
        touchBus?.events?.collect { event ->
            if (event.inTutorial && canvasSize.width > 1 && canvasSize.height > 1) {
                var anyHit = false
                val cooldown = if (event.isDrag) 200_000_000L else 60_000_000L
                particles = particles.map { p ->
                    val canHitAgain = event.timestampNanos - p.lastHitNanos >= cooldown
                    if (canHitAgain && hitTestExpressiveParticle(
                            particle = p,
                            touchXNorm = event.xNorm,
                            touchYNorm = event.yNorm,
                            viewportWidth = canvasSize.width.toFloat(),
                            viewportHeight = canvasSize.height.toFloat(),
                            minTouchRadiusPx = minTouchRadiusPx,
                            maxHitRadiusPx = maxHitRadiusPx
                        )
                    ) {
                        anyHit = true
                        applyShapeTouchReaction(p, event.xNorm, event.yNorm, event.timestampNanos)
                    } else {
                        p
                    }
                }
                if (anyHit && touchBus.tryClaimShapeHaptic(event.timestampNanos, event.isDrag)) {
                    hapticEngine?.trigger(
                        if (event.isDrag) HapticCue.SHAPE_DRAG_TICK else HapticCue.SHAPE_HIT
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
                    elapsedSec += dt
                    particles = particles.map { stepExpressiveParticle(it, dt, minNorm = 0.08f, maxNorm = 0.92f) }
                }
                lastNanos = frameNanos
            }
        }
    }

    val palette = rememberPaletteSlotColors()
    val reusablePath = remember { Path() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .testTag("tutorial_expressive_ambient")
    ) {
        val base = min(size.width, size.height)
        particles.forEachIndexed { idx, particle ->
            val breathe = 0.94f + 0.07f * sin(elapsedSec * 1.5f + idx * 1.4f)
            val popScale = 1f + 0.22f * particle.hitBoost
            val drawSize = base * particle.sizeFraction * breathe * popScale
            val left = size.width * particle.xNorm - drawSize / 2f
            val top = size.height * particle.yNorm - drawSize / 2f

            val (from, to, progress) = resolveMorphPairAndProgress(particle)
            buildMorphingExpressivePath(
                size = Size(drawSize, drawSize),
                from = from,
                to = to,
                morphProgress = progress,
                rotationDegrees = particle.rotationDeg,
                outPath = reusablePath
            )
            val baseSlotColor = palette[particle.colorSlot % palette.size]
            val tint = baseSlotColor.copy(alpha = (particle.baseAlpha + particle.hitBoost * 0.16f).coerceAtMost(0.52f))
            translate(left = left, top = top) {
                drawPath(path = reusablePath, color = tint)
            }
        }
    }
}

/**
 * Animated rotating, breathing, and continuously morphing Material 3 Expressive halo
 * placed behind Marin Kitagawa on the Tutorial stage when Material You is active.
 * Completely removed in Nothing OS mode.
 */
@Composable
fun ExpressiveMarinStageBackdrop(
    step: Int,
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    if (designStyle == DesignStyle.NOTHING_OS) {
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "marin_stage_halo")
    val outerSpin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(18000, easing = LinearEasing), RepeatMode.Restart),
        label = "marin_outer_spin"
    )
    val innerSpin by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Restart),
        label = "marin_inner_spin"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "marin_halo_pulse"
    )
    val morphCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = ExpressiveMorphShape.entries.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 27000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "marin_halo_morph"
    )

    var stepBumpTarget by remember { mutableFloatStateOf(1f) }
    LaunchedEffect(step) {
        stepBumpTarget = 1.08f
    }
    val stepScale by animateFloatAsState(
        targetValue = stepBumpTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        finishedListener = {
            if (stepBumpTarget > 1f) stepBumpTarget = 1f
        },
        label = "marin_step_bump"
    )

    val outerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.56f)
    val innerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.52f)
    val reusablePath = remember { Path() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("tutorial_marin_expressive_halo")
    ) {
        val shapes = ExpressiveMorphShape.entries
        val count = shapes.size

        val outerCursor = (morphCycle + step) % count.toFloat()
        val outerIdx = outerCursor.toInt().coerceIn(0, count - 1)
        val outerFrac = (outerCursor - outerIdx).coerceIn(0f, 1f)

        val diameter = min(size.width, size.height) * 0.88f * pulse * stepScale
        val cx = (size.width - diameter) / 2f
        val cy = (size.height - diameter) / 2f

        buildMorphingExpressivePath(
            size = Size(diameter, diameter),
            from = shapes[outerIdx],
            to = shapes[(outerIdx + 1) % count],
            morphProgress = outerFrac,
            rotationDegrees = outerSpin,
            outPath = reusablePath
        )
        translate(left = cx, top = cy) {
            drawPath(path = reusablePath, color = outerColor)
        }

        val innerCursor = (morphCycle + 3f + step * 2f) % count.toFloat()
        val innerIdx = innerCursor.toInt().coerceIn(0, count - 1)
        val innerFrac = (innerCursor - innerIdx).coerceIn(0f, 1f)
        val innerSize = diameter * 0.76f

        buildMorphingExpressivePath(
            size = Size(innerSize, innerSize),
            from = shapes[innerIdx],
            to = shapes[(innerIdx + 2) % count],
            morphProgress = innerFrac,
            rotationDegrees = innerSpin,
            outPath = reusablePath
        )
        translate(
            left = (size.width - innerSize) / 2f,
            top = (size.height - innerSize) / 2f
        ) {
            drawPath(path = reusablePath, color = innerColor)
        }
    }
}

/**
 * Compact animated Material 3 Expressive badge that rotates, breathes, and smoothly morphs
 * across all 9 silhouettes when [morphContinuously] is enabled.
 * Completely omitted in Nothing OS mode.
 */
@Composable
fun ExpressiveAnimatedShapeBadge(
    kind: ExpressiveShapeKind = ExpressiveShapeKind.ORGANIC_FLOWER,
    size: Dp = 24.dp,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    rotationDurationMs: Int = 12000,
    morphContinuously: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val designStyle = LocalDesignStyle.current
    if (designStyle == DesignStyle.NOTHING_OS) {
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "expressive_badge_${kind.name}")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = rotationDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "badge_spin"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badge_pulse"
    )
    val morphCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = ExpressiveMorphShape.entries.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "badge_morph"
    )

    val reusablePath = remember { Path() }

    Box(
        modifier = modifier
            .size(size)
            .testTag("expressive_shape_badge_${kind.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val drawSide = min(this.size.width, this.size.height) * scale
            val shapes = ExpressiveMorphShape.entries
            val count = shapes.size

            val (from, to, progress) = if (morphContinuously) {
                val cursor = (morphCycle + kind.ordinal) % count.toFloat()
                val idx = cursor.toInt().coerceIn(0, count - 1)
                Triple(shapes[idx], shapes[(idx + 1) % count], (cursor - idx).coerceIn(0f, 1f))
            } else {
                Triple(kind.morphShape, kind.morphShape, 0f)
            }

            buildMorphingExpressivePath(
                size = Size(drawSide, drawSide),
                from = from,
                to = to,
                morphProgress = progress,
                rotationDegrees = rotation,
                outPath = reusablePath
            )
            translate(
                left = (this.size.width - drawSide) / 2f,
                top = (this.size.height - drawSide) / 2f
            ) {
                drawPath(path = reusablePath, color = containerColor)
            }
        }
        content()
    }
}

/**
 * Animated Material 3 Expressive morphing showcase motif for empty states across Feed and
 * Saved Offline Library in Material You mode. Completely omitted in Nothing OS mode.
 */
@Composable
fun ExpressiveEmptyStateMotif(
    icon: ImageVector,
    iconContentDescription: String,
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    if (designStyle == DesignStyle.NOTHING_OS) {
        return
    }

    Box(
        modifier = modifier
            .size(84.dp)
            .testTag("expressive_empty_state_motif"),
        contentAlignment = Alignment.Center
    ) {
        ExpressiveAnimatedShapeBadge(
            kind = ExpressiveShapeKind.ORGANIC_FLOWER,
            size = 82.dp,
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
            rotationDurationMs = 15000
        )
        ExpressiveAnimatedShapeBadge(
            kind = ExpressiveShapeKind.SCALLOPED_SQUIRCLE,
            size = 56.dp,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f),
            rotationDurationMs = 11000
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

/**
 * Compact trio of animated morphing expressive shapes used on Material You theme cards
 * when Material You is active. Hidden when Nothing OS mode is selected.
 */
@Composable
fun ExpressiveShapeCluster(
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    if (designStyle == DesignStyle.NOTHING_OS) {
        return
    }

    Row(
        modifier = modifier.testTag("expressive_shape_cluster"),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExpressiveAnimatedShapeBadge(
            kind = ExpressiveShapeKind.ORGANIC_FLOWER,
            size = 16.dp,
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
            rotationDurationMs = 9000
        )
        ExpressiveAnimatedShapeBadge(
            kind = ExpressiveShapeKind.SOFT_HEART,
            size = 16.dp,
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f),
            rotationDurationMs = 11000
        )
        ExpressiveAnimatedShapeBadge(
            kind = ExpressiveShapeKind.BURST_STAR,
            size = 15.dp,
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f),
            rotationDurationMs = 13000
        )
    }
}

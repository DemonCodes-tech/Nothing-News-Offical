package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

val NothingShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(2.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(6.dp),
    extraLarge = RoundedCornerShape(8.dp)
)

val MaterialYouShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * Full set of Material 3 Expressive organic and geometric silhouettes
 * that morph seamlessly into one another via unified polar-radius sampling.
 */
enum class ExpressiveMorphShape {
    ORGANIC_FLOWER,
    SCALLOPED_SQUIRCLE,
    CLOVER,
    SOFT_PILL,
    BURST_STAR,
    ROUNDED_DIAMOND,
    SOFT_CIRCLE,
    ORGANIC_BLOB,
    SOFT_HEART
}

const val EXPRESSIVE_POLAR_SAMPLES = 144

private object ExpressiveShapePolarRegistry {
    val cosTable = FloatArray(EXPRESSIVE_POLAR_SAMPLES) { idx ->
        cos((2.0 * PI * idx) / EXPRESSIVE_POLAR_SAMPLES).toFloat()
    }
    val sinTable = FloatArray(EXPRESSIVE_POLAR_SAMPLES) { idx ->
        sin((2.0 * PI * idx) / EXPRESSIVE_POLAR_SAMPLES).toFloat()
    }

    val profiles: Map<ExpressiveMorphShape, FloatArray> by lazy {
        val heartRadii = buildExactHeartPolarProfile()
        ExpressiveMorphShape.entries.associateWith { shape ->
            if (shape == ExpressiveMorphShape.SOFT_HEART) {
                heartRadii
            } else {
                FloatArray(EXPRESSIVE_POLAR_SAMPLES) { idx ->
                    val theta = (2.0 * PI * idx) / EXPRESSIVE_POLAR_SAMPLES
                    computeAnalyticalRadius(shape, theta)
                }
            }
        }
    }

    private fun computeAnalyticalRadius(shape: ExpressiveMorphShape, theta: Double): Float {
        val cosT = cos(theta)
        val sinT = sin(theta)
        return when (shape) {
            ExpressiveMorphShape.ORGANIC_FLOWER -> {
                val lobe = (0.5 + 0.5 * cos(8.0 * theta)).pow(0.62).toFloat()
                0.76f + 0.22f * lobe
            }
            ExpressiveMorphShape.SCALLOPED_SQUIRCLE -> {
                val p = 3.2
                val denom = (abs(cosT).pow(p) + abs(sinT).pow(p)).pow(1.0 / p)
                val squircleFactor = if (denom > 0.0001) (1.0 / denom).toFloat() else 1f
                val wave = (0.5 + 0.5 * cos(8.0 * theta)).pow(0.70).toFloat()
                (squircleFactor * 0.78f) * (0.88f + 0.12f * wave)
            }
            ExpressiveMorphShape.CLOVER -> {
                val lobe = (0.5 + 0.5 * cos(4.0 * theta)).pow(0.62).toFloat()
                0.71f + 0.27f * lobe
            }
            ExpressiveMorphShape.SOFT_PILL -> {
                val a = 0.98
                val b = 0.56
                val exp = 2.6
                val term = (abs(cosT) / a).pow(exp) + (abs(sinT) / b).pow(exp)
                val r = if (term > 0.0001) term.pow(-1.0 / exp).toFloat() else 0.98f
                r.coerceIn(0.45f, 0.99f)
            }
            ExpressiveMorphShape.BURST_STAR -> {
                val spike = (0.5 + 0.5 * cos(12.0 * theta)).pow(1.45).toFloat()
                0.63f + 0.35f * spike
            }
            ExpressiveMorphShape.ROUNDED_DIAMOND -> {
                val p = 1.14
                val denom = (abs(cosT).pow(p) + abs(sinT).pow(p)).pow(1.0 / p)
                val r = if (denom > 0.0001) (0.97 / denom).toFloat() else 0.97f
                r.coerceIn(0.60f, 0.98f)
            }
            ExpressiveMorphShape.SOFT_CIRCLE -> 0.87f
            ExpressiveMorphShape.ORGANIC_BLOB -> {
                val wave = 0.79f +
                    0.105f * cos(3.0 * theta + 0.4).toFloat() +
                    0.065f * sin(5.0 * theta - 0.8).toFloat() +
                    0.03f * cos(2.0 * theta).toFloat()
                wave.coerceIn(0.55f, 0.98f)
            }
            ExpressiveMorphShape.SOFT_HEART -> 0.85f
        }
    }

    /**
     * Precomputes a crisp, symmetrical Material 3 Soft Heart polar radial profile
     * via ray-segment intersection against the classic parametric heart centered cleanly
     * inside its interior (screen coords: -Y = top cleft, +Y = bottom tip).
     */
    private fun buildExactHeartPolarProfile(): FloatArray {
        val polyCount = 720
        val polyX = FloatArray(polyCount)
        val polyY = FloatArray(polyCount)
        var maxR = 0.001f

        for (i in 0 until polyCount) {
            val t = (2.0 * PI * i) / polyCount
            val s = sin(t)
            val x = (16.0 * s * s * s).toFloat()
            val y = (-(13.0 * cos(t) - 5.0 * cos(2.0 * t) - 2.0 * cos(3.0 * t) - cos(4.0 * t))).toFloat()
            polyX[i] = x
            polyY[i] = y
            val dist = hypot(x, y)
            if (dist > maxR) maxR = dist
        }

        for (i in 0 until polyCount) {
            polyX[i] = (polyX[i] / maxR) * 0.96f
            polyY[i] = (polyY[i] / maxR) * 0.96f
        }

        return FloatArray(EXPRESSIVE_POLAR_SAMPLES) { idx ->
            val dx = cosTable[idx]
            val dy = sinTable[idx]
            var bestDist = 0.85f
            var found = false

            for (k in 0 until polyCount) {
                val next = (k + 1) % polyCount
                val ax = polyX[k]
                val ay = polyY[k]
                val sx = polyX[next] - ax
                val sy = polyY[next] - ay

                val denom = dx * (-sy) - dy * (-sx)
                if (abs(denom) > 1e-6f) {
                    val tRay = (ax * (-sy) - ay * (-sx)) / denom
                    val uSeg = (dx * ay - dy * ax) / denom
                    if (tRay > 0f && uSeg in -0.001f..1.001f) {
                        if (!found || tRay < bestDist) {
                            bestDist = tRay
                            found = true
                        }
                    }
                }
            }
            bestDist.coerceIn(0.24f, 0.98f)
        }
    }
}

/**
 * Quintic smootherstep curve that naturally holds briefly on each recognizable shape
 * before smoothly gliding into the next silhouette.
 */
fun smoothMorphEase(progress: Float): Float {
    val t = progress.coerceIn(0f, 1f)
    return t * t * t * (t * (t * 6f - 15f) + 10f)
}

/**
 * Builds an animated Material 3 Expressive path continuously morphed between
 * [from] and [to] at [morphProgress] (0f..1f) and rotated by [rotationDegrees].
 */
fun buildMorphingExpressivePath(
    size: Size,
    from: ExpressiveMorphShape,
    to: ExpressiveMorphShape,
    morphProgress: Float,
    rotationDegrees: Float = 0f,
    outPath: Path = Path()
): Path {
    val width = size.width.coerceAtLeast(1f)
    val height = size.height.coerceAtLeast(1f)
    val cx = width / 2f
    val cy = height / 2f
    val maxRadius = min(width, height) / 2f

    val profiles = ExpressiveShapePolarRegistry.profiles
    val fromProfile = profiles.getValue(from)
    val toProfile = profiles.getValue(to)
    val eased = smoothMorphEase(morphProgress)

    val rotRad = Math.toRadians(rotationDegrees.toDouble())
    val cosRot = cos(rotRad).toFloat()
    val sinRot = sin(rotRad).toFloat()

    val cosTable = ExpressiveShapePolarRegistry.cosTable
    val sinTable = ExpressiveShapePolarRegistry.sinTable

    outPath.reset()
    for (i in 0 until EXPRESSIVE_POLAR_SAMPLES) {
        val rFrom = fromProfile[i]
        val rTo = toProfile[i]
        val r = (rFrom + (rTo - rFrom) * eased) * maxRadius

        val ux = cosTable[i]
        val uy = sinTable[i]
        val rx = ux * cosRot - uy * sinRot
        val ry = ux * sinRot + uy * cosRot

        val x = cx + r * rx
        val y = cy + r * ry
        if (i == 0) outPath.moveTo(x, y) else outPath.lineTo(x, y)
    }
    outPath.close()
    return outPath
}

/**
 * Material 3 Expressive multi-petal organic flower silhouette.
 * Produces soft, rounded petals inspired by Material 3 Expressive Blossom / Sunny tokens.
 */
class OrganicFlowerShape(
    private val petals: Int = 8,
    private val petalDepth: Float = 0.22f,
    private val rotationDegrees: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = Outline.Generic(
        buildOrganicFlowerPath(
            size = size,
            petals = petals,
            petalDepth = petalDepth,
            rotationDegrees = rotationDegrees
        )
    )
}

/**
 * Material 3 Expressive scalloped squircle ("Cookie" / rounded square with soft outward scallops).
 */
class ScallopedSquircleShape(
    private val scallops: Int = 8,
    private val scallopDepth: Float = 0.10f,
    private val squircleExponent: Float = 3.2f,
    private val rotationDegrees: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = Outline.Generic(
        buildScallopedSquirclePath(
            size = size,
            scallops = scallops,
            scallopDepth = scallopDepth,
            squircleExponent = squircleExponent,
            rotationDegrees = rotationDegrees
        )
    )
}

/**
 * Material 3 Expressive 4-lobe organic clover squircle.
 */
class CloverSquircleShape(
    private val rotationDegrees: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = Outline.Generic(
        buildOrganicFlowerPath(
            size = size,
            petals = 4,
            petalDepth = 0.24f,
            rotationDegrees = rotationDegrees
        )
    )
}

fun buildOrganicFlowerPath(
    size: Size,
    petals: Int = 8,
    petalDepth: Float = 0.22f,
    rotationDegrees: Float = 0f
): Path {
    val width = size.width.coerceAtLeast(1f)
    val height = size.height.coerceAtLeast(1f)
    val cx = width / 2f
    val cy = height / 2f
    val maxRadius = min(width, height) / 2f
    val safePetals = petals.coerceAtLeast(3)
    val safeDepth = petalDepth.coerceIn(0.04f, 0.42f)
    val rotRad = Math.toRadians(rotationDegrees.toDouble())
    val steps = safePetals * 18

    val path = Path()
    for (i in 0 until steps) {
        val theta = (2.0 * PI * i) / steps
        val lobe = (0.5 + 0.5 * cos(safePetals * theta)).pow(0.62).toFloat()
        val r = maxRadius * ((1f - safeDepth) + safeDepth * lobe)
        val angle = theta + rotRad
        val x = cx + r * cos(angle).toFloat()
        val y = cy + r * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

fun buildScallopedSquirclePath(
    size: Size,
    scallops: Int = 8,
    scallopDepth: Float = 0.10f,
    squircleExponent: Float = 3.2f,
    rotationDegrees: Float = 0f
): Path {
    val width = size.width.coerceAtLeast(1f)
    val height = size.height.coerceAtLeast(1f)
    val cx = width / 2f
    val cy = height / 2f
    val rxMax = width / 2f
    val ryMax = height / 2f
    val safeScallops = scallops.coerceAtLeast(4)
    val safeDepth = scallopDepth.coerceIn(0.02f, 0.25f)
    val p = squircleExponent.coerceIn(2.0f, 5.0f).toDouble()
    val rotRad = Math.toRadians(rotationDegrees.toDouble())
    val steps = safeScallops * 18

    val path = Path()
    for (i in 0 until steps) {
        val theta = (2.0 * PI * i) / steps
        val cosT = cos(theta)
        val sinT = sin(theta)
        val superDenominator = (abs(cosT).pow(p) + abs(sinT).pow(p)).pow(1.0 / p)
        val squircleFactor = if (superDenominator > 0.0001) (1.0 / superDenominator).toFloat() else 1f
        val wave = (0.5 + 0.5 * cos(safeScallops * theta)).pow(0.70).toFloat()
        val scale = (squircleFactor * 0.86f) * ((1f - safeDepth) + safeDepth * wave)
        val localX = rxMax * scale * cosT.toFloat()
        val localY = ryMax * scale * sinT.toFloat()
        val rotatedX = cx + localX * cos(rotRad).toFloat() - localY * sin(rotRad).toFloat()
        val rotatedY = cy + localX * sin(rotRad).toFloat() + localY * cos(rotRad).toFloat()
        if (i == 0) path.moveTo(rotatedX, rotatedY) else path.lineTo(rotatedX, rotatedY)
    }
    path.close()
    return path
}

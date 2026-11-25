package com.example.ui.theme

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

enum class HapticCue {
    SHAPE_HIT,
    SHAPE_DRAG_TICK,
    DOT_PULSE_TAP,
    DOT_DRAG_TICK,
    UI_TAP,
    UI_TOGGLE,
    UI_NAV
}

val LocalHapticEngine = staticCompositionLocalOf<AppHapticEngine?> { null }

/**
 * Theme-aware tactile haptic feedback engine delivering distinct waveforms:
 * - Nothing OS: sharp, crisp mechanical clicks and micro-detents
 * - Material You: bouncy, soft organic pulses and double-taps
 *
 * Strictly gated by [isEnabled] (user toggle in Settings) and safely handles
 * devices or test environments without hardware vibrator support.
 */
class AppHapticEngine(
    private val context: Context?,
    private val designStyleProvider: () -> DesignStyle,
    private val enabledProvider: () -> Boolean,
    private val composeHaptics: HapticFeedback? = null
) {
    var lastTriggeredCue: HapticCue? = null
        private set
    var triggerCount: Int = 0
        private set

    fun isEnabled(): Boolean = enabledProvider()

    fun trigger(cue: HapticCue, force: Boolean = false): Boolean {
        if (!force && !enabledProvider()) return false

        lastTriggeredCue = cue
        triggerCount++

        val style = designStyleProvider()
        val vibrated = performPlatformVibration(style, cue)
        if (!vibrated) {
            performComposeFallback(cue)
        }
        return true
    }

    private fun performComposeFallback(cue: HapticCue) {
        val haptics = composeHaptics ?: return
        try {
            val type = when (cue) {
                HapticCue.SHAPE_HIT,
                HapticCue.DOT_PULSE_TAP,
                HapticCue.UI_TOGGLE -> HapticFeedbackType.LongPress
                HapticCue.SHAPE_DRAG_TICK,
                HapticCue.DOT_DRAG_TICK,
                HapticCue.UI_TAP,
                HapticCue.UI_NAV -> HapticFeedbackType.TextHandleMove
            }
            haptics.performHapticFeedback(type)
        } catch (_: Throwable) {
            // Ignore in headless/non-interactive environments
        }
    }

    @Suppress("DEPRECATION")
    private fun performPlatformVibration(style: DesignStyle, cue: HapticCue): Boolean {
        val ctx = context ?: return false
        return try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator == null || !vibrator.hasVibrator()) {
                return false
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = buildVibrationEffect(style, cue)
                vibrator.vibrate(effect)
            } else {
                val durationMs = when (cue) {
                    HapticCue.SHAPE_HIT, HapticCue.DOT_PULSE_TAP -> 22L
                    HapticCue.UI_TOGGLE -> 16L
                    HapticCue.UI_NAV -> 12L
                    HapticCue.SHAPE_DRAG_TICK, HapticCue.DOT_DRAG_TICK, HapticCue.UI_TAP -> 9L
                }
                vibrator.vibrate(durationMs)
            }
            true
        } catch (_: Throwable) {
            false
        }
    }

    private fun buildVibrationEffect(style: DesignStyle, cue: HapticCue): VibrationEffect {
        return if (style == DesignStyle.NOTHING_OS) {
            when (cue) {
                HapticCue.DOT_PULSE_TAP -> VibrationEffect.createWaveform(
                    longArrayOf(0L, 14L, 22L, 18L),
                    intArrayOf(0, 210, 0, 255),
                    -1
                )
                HapticCue.SHAPE_HIT -> VibrationEffect.createOneShot(18L, 230)
                HapticCue.DOT_DRAG_TICK, HapticCue.SHAPE_DRAG_TICK ->
                    VibrationEffect.createOneShot(8L, 145)
                HapticCue.UI_TOGGLE -> VibrationEffect.createOneShot(16L, 225)
                HapticCue.UI_NAV -> VibrationEffect.createOneShot(12L, 180)
                HapticCue.UI_TAP -> VibrationEffect.createOneShot(10L, 155)
            }
        } else {
            when (cue) {
                HapticCue.SHAPE_HIT -> VibrationEffect.createWaveform(
                    longArrayOf(0L, 18L, 28L, 12L),
                    intArrayOf(0, 215, 0, 145),
                    -1
                )
                HapticCue.DOT_PULSE_TAP -> VibrationEffect.createOneShot(18L, 190)
                HapticCue.SHAPE_DRAG_TICK, HapticCue.DOT_DRAG_TICK ->
                    VibrationEffect.createOneShot(10L, 130)
                HapticCue.UI_TOGGLE -> VibrationEffect.createOneShot(15L, 185)
                HapticCue.UI_NAV -> VibrationEffect.createOneShot(12L, 160)
                HapticCue.UI_TAP -> VibrationEffect.createOneShot(10L, 135)
            }
        }
    }
}

@Composable
fun rememberAppHapticEngine(
    designStyle: DesignStyle,
    enabled: Boolean
): AppHapticEngine {
    val context = LocalContext.current
    val composeHaptics = LocalHapticFeedback.current
    val currentStyle = rememberUpdatedState(designStyle)
    val currentEnabled = rememberUpdatedState(enabled)

    return remember(context, composeHaptics) {
        AppHapticEngine(
            context = context,
            designStyleProvider = { currentStyle.value },
            enabledProvider = { currentEnabled.value },
            composeHaptics = composeHaptics
        )
    }
}

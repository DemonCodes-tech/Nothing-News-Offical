package com.example.ui.components

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.flow.MutableSharedFlow

data class AmbientTouchEvent(
    val xNorm: Float,
    val yNorm: Float,
    val isDrag: Boolean,
    val inTutorial: Boolean,
    val timestampNanos: Long
)

class AmbientTouchBus {
    val events = MutableSharedFlow<AmbientTouchEvent>(extraBufferCapacity = 32)
    private var lastShapeHapticNanos = 0L
    private var lastDotHapticNanos = 0L

    fun emit(event: AmbientTouchEvent) {
        events.tryEmit(event)
    }

    fun tryClaimShapeHaptic(nowNanos: Long, isDrag: Boolean): Boolean {
        val cooldownNanos = if (isDrag) 85_000_000L else 45_000_000L
        if (nowNanos - lastShapeHapticNanos < cooldownNanos) return false
        lastShapeHapticNanos = nowNanos
        return true
    }

    fun tryClaimDotHaptic(nowNanos: Long, isDrag: Boolean): Boolean {
        val cooldownNanos = if (isDrag) 85_000_000L else 45_000_000L
        if (nowNanos - lastDotHapticNanos < cooldownNanos) return false
        lastDotHapticNanos = nowNanos
        return true
    }
}

val LocalAmbientTouchBus = staticCompositionLocalOf<AmbientTouchBus?> { null }

/**
 * Passively observes pointer down and drag positions on [PointerEventPass.Initial] without
 * consuming events so underlying buttons, cards, sliders, and scrollable lists keep full fidelity
 * while roaming shapes and pulsing Nothing OS dots react instantaneously.
 */
fun Modifier.observeAmbientTouchPassThrough(
    bus: AmbientTouchBus?,
    inTutorial: Boolean
): Modifier {
    if (bus == null) return this
    return this.pointerInput(bus, inTutorial) {
        awaitPointerEventScope {
            var lastEmitNanos = 0L
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val w = size.width.coerceAtLeast(1).toFloat()
                val h = size.height.coerceAtLeast(1).toFloat()
                event.changes.forEach { change ->
                    if (change.pressed) {
                        val isDown = !change.previousPressed
                        val now = System.nanoTime()
                        if (isDown || now - lastEmitNanos >= 45_000_000L) {
                            lastEmitNanos = now
                            bus.emit(
                                AmbientTouchEvent(
                                    xNorm = (change.position.x / w).coerceIn(0f, 1f),
                                    yNorm = (change.position.y / h).coerceIn(0f, 1f),
                                    isDrag = !isDown,
                                    inTutorial = inTutorial,
                                    timestampNanos = now
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

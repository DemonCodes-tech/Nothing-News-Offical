package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SignalRed
import kotlinx.coroutines.delay

enum class GlyphPattern {
    PULSE,
    CHECK,
    SMILEY,
    WIRE,
    ALERT
}

@Composable
fun GlyphMatrixWidget(
    modifier: Modifier = Modifier,
    onEasterEggTriggered: () -> Unit = {}
) {
    var activePattern by remember { mutableStateOf(GlyphPattern.WIRE) }
    var frameIndex by remember { mutableIntStateOf(0) }

    // Real frame-by-frame LED animation driver
    LaunchedEffect(activePattern) {
        frameIndex = 0
        while (true) {
            delay(160)
            frameIndex = (frameIndex + 1) % 12
        }
    }

    val rows = 7
    val cols = 7

    Box(
        modifier = modifier
            .testTag("glyph_matrix_widget")
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onEasterEggTriggered()
                    }
                )
            }
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (activePattern == GlyphPattern.ALERT) SignalRed else Color.White)
                    )
                    Text(
                        text = "GLYPH MATRIX // 7x7 LED",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = activePattern.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (activePattern == GlyphPattern.ALERT) SignalRed else MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7x7 Micro-LED Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF070707))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    .padding(12.dp)
            ) {
                for (r in 0 until rows) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (c in 0 until cols) {
                            val isLit = isLedLit(r, c, activePattern, frameIndex)
                            val isRed = (activePattern == GlyphPattern.ALERT && isLit) || (r == 3 && c == 3 && activePattern == GlyphPattern.PULSE && frameIndex % 2 == 0)

                            val targetColor = when {
                                isLit && isRed -> SignalRed
                                isLit -> Color.White
                                else -> Color(0xFF1F1F1F)
                            }

                            val animatedColor by animateColorAsState(
                                targetValue = targetColor,
                                animationSpec = tween(durationMillis = 90),
                                label = "led_${r}_${c}"
                            )

                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(animatedColor)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Control Buttons to switch Glyph animation mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GlyphPattern.values().forEach { pattern ->
                    val isSelected = activePattern == pattern
                    OutlinedButton(
                        onClick = { activePattern = pattern },
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                            .testTag("glyph_pattern_${pattern.name.lowercase()}"),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text(
                            text = pattern.name.take(3),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

private fun isLedLit(row: Int, col: Int, pattern: GlyphPattern, frame: Int): Boolean {
    return when (pattern) {
        GlyphPattern.PULSE -> {
            val dist = maxOf(kotlin.math.abs(row - 3), kotlin.math.abs(col - 3))
            val currentRing = (frame / 2) % 4
            dist == currentRing || (dist == 0 && frame % 2 == 0)
        }
        GlyphPattern.CHECK -> {
            // Pixel art checkmark in 7x7:
            // (1,4), (2,3), (3,2), (4,3), (5,4), (6,5)
            val checkmarkCoords = setOf(
                Pair(4, 1), Pair(5, 2), Pair(6, 3), Pair(5, 4), Pair(4, 5), Pair(3, 6)
            )
            val animatedStage = (frame % 7)
            checkmarkCoords.contains(Pair(row, col)) && (col <= animatedStage)
        }
        GlyphPattern.SMILEY -> {
            // Nothing OS smiley:
            // Eyes: (2,2), (2,4)
            // Mouth: (4,1), (5,2), (5,3), (5,4), (4,5)
            // Outer round boundary dots
            val eyes = (row == 2 && (col == 2 || col == 4))
            val mouth = (row == 4 && (col == 1 || col == 5)) || (row == 5 && (col in 2..4))
            val wink = frame % 6 == 0 && row == 2 && col == 4
            if (wink) (row == 2 && col == 2) || mouth else eyes || mouth
        }
        GlyphPattern.WIRE -> {
            // Radiating wave moving left to right
            val waveCol = (frame % 8)
            col == waveCol || col == waveCol - 1 || (row == 3 && col <= waveCol)
        }
        GlyphPattern.ALERT -> {
            // Flashing border or exclamation
            val isBorder = row == 0 || row == 6 || col == 0 || col == 6
            val isExclamation = (col == 3 && row in 2..4) || (col == 3 && row == 5 && frame % 2 == 0)
            if (frame % 2 == 0) isBorder else isExclamation
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.LocalDeviceProfile
import com.example.ui.theme.LocalHapticEngine

@Composable
fun NothingCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    testTag: String = "nothing_card",
    contentPadding: Dp = LocalDeviceProfile.current.cardPadding,
    content: @Composable () -> Unit
) {
    val designStyle = LocalDesignStyle.current
    val deviceProfile = LocalDeviceProfile.current
    val hapticEngine = LocalHapticEngine.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS

    if (!isNothing) {
        // Material 3 / Material You Expressive translucent layered card so roaming shapes shine through cleanly
        val shape = RoundedCornerShape(deviceProfile.cardRadius)
        Surface(
            onClick = {
                onClick?.let {
                    hapticEngine?.trigger(HapticCue.UI_TAP)
                    it()
                }
            },
            enabled = onClick != null,
            modifier = modifier.testTag(testTag),
            shape = shape,
            color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.80f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            )
        ) {
            Box(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    } else {
        // Nothing OS Stark Monochrome Industrial Card
        val cornerRadius = 4.dp
        val shape = RoundedCornerShape(cornerRadius)
        val borderColor = MaterialTheme.colorScheme.outline
        val backgroundColor = MaterialTheme.colorScheme.surface

        Box(
            modifier = modifier
                .testTag(testTag)
                .clip(shape)
                .background(backgroundColor)
                .border(1.dp, borderColor, shape)
                .then(
                    if (onClick != null) {
                        Modifier.clickable {
                            hapticEngine?.trigger(HapticCue.UI_TAP)
                            onClick()
                        }
                    } else {
                        Modifier
                    }
                )
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle

@Composable
fun MechanicalSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "mechanical_switch"
) {
    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS

    val trackWidth = 48.dp
    val trackHeight = 26.dp
    val knobWidth = if (isNothing) 18.dp else 20.dp
    val knobHeight = if (isNothing) 18.dp else 20.dp
    val cornerRadius = if (isNothing) 2.dp else 13.dp

    val knobOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 4.dp,
        animationSpec = tween(durationMillis = 150),
        label = "switch_knob_offset"
    )

    val trackColor = if (checked) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val knobColor = if (checked) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderColor = MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .testTag(testTag)
            .size(width = trackWidth, height = trackHeight)
            .background(trackColor, RoundedCornerShape(cornerRadius))
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset)
                .size(width = knobWidth, height = knobHeight)
                .background(knobColor, RoundedCornerShape(if (isNothing) 1.dp else 10.dp))
        )
    }
}

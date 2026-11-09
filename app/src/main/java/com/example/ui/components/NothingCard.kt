package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle

@Composable
fun NothingCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    testTag: String = "nothing_card",
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS

    if (!isNothing) {
        // Material 3 / Material You Expressive Card with Dynamic Wallpaper Surface and 24dp Rounded Corners
        ElevatedCard(
            onClick = { onClick?.invoke() },
            enabled = onClick != null,
            modifier = modifier.testTag(testTag),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 1.5.dp,
                pressedElevation = 3.dp
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
                        Modifier.clickable { onClick() }
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

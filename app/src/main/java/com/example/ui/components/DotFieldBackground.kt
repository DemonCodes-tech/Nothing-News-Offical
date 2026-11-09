package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle

@Composable
fun DotFieldBackground(
    modifier: Modifier = Modifier,
    dotColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
) {
    val designStyle = LocalDesignStyle.current
    if (designStyle != DesignStyle.NOTHING_OS) {
        return
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val spacingPx = 28.dp.toPx()
        val radiusPx = 1.2.dp.toPx()

        val columns = (size.width / spacingPx).toInt() + 1
        val rows = (size.height / spacingPx).toInt() + 1

        val startX = (size.width - (columns - 1) * spacingPx) / 2
        val startY = (size.height - (rows - 1) * spacingPx) / 2

        for (i in 0 until columns) {
            for (j in 0 until rows) {
                drawCircle(
                    color = dotColor,
                    radius = radiusPx,
                    center = Offset(startX + i * spacingPx, startY + j * spacingPx)
                )
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import kotlin.math.roundToInt

@Composable
fun SegmentedProgressBar(
    progress: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier,
    totalSegments: Int = 12,
    showLabel: Boolean = true,
    labelPrefix: String = "READ PROGRESS"
) {
    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS
    val cornerRadius = if (isNothing) 0.dp else 4.dp

    val safeProgress = progress.coerceIn(0f, 1f)
    val activeSegments = (safeProgress * totalSegments).roundToInt()
    val percentageInt = (safeProgress * 100).toInt()

    Column(modifier = modifier.fillMaxWidth()) {
        if (showLabel) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = labelPrefix,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "$percentageInt%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (i in 0 until totalSegments) {
                val isActive = i < activeSegments
                val blockColor = if (isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(blockColor)
                        .then(
                            if (isNothing && !isActive) {
                                Modifier.border(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    RoundedCornerShape(cornerRadius)
                                )
                            } else Modifier
                        )
                )
            }
        }
    }
}

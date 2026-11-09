package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.WaterDamage
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.SignalRed

@Composable
fun NewsImageViewer(
    imageType: String,
    caption: String,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    showCaption: Boolean = true
) {
    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS

    val cornerRadius = if (isNothing) 4.dp else 22.dp
    val shape = RoundedCornerShape(cornerRadius)

    val visualColors = if (isNothing) {
        // Stark monochrome gradient for Nothing OS
        listOf(Color(0xFF1C1C1C), Color(0xFF0F0F0F), Color(0xFF262626))
    } else {
        // Expressive Material 3 dynamic color blends
        listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.surfaceVariant
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(Brush.linearGradient(visualColors))
                .then(
                    if (isNothing) {
                        Modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape)
                    } else {
                        Modifier
                    }
                )
        ) {
            // Nothing OS Dot-Matrix Dither Overlay Shader
            if (isNothing) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stepPx = 4.dp.toPx()
                    val dotRadius = 0.8.dp.toPx()
                    val cols = (size.width / stepPx).toInt()
                    val rows = (size.height / stepPx).toInt()

                    for (x in 0..cols) {
                        for (y in 0..rows) {
                            if ((x + y) % 2 == 0) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.08f),
                                    radius = dotRadius,
                                    center = Offset(x * stepPx, y * stepPx)
                                )
                            }
                        }
                    }
                }
            }

            // High-Contrast Iconographic Focal Centerpiece
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val (icon, label) = getVisualThemeDetails(imageType)

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(if (isNothing) RoundedCornerShape(4.dp) else CircleShape)
                            .background(
                                if (isNothing) Color(0xFF141414) else MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                            )
                            .then(
                                if (isNothing) Modifier.border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isNothing) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = label,
                        style = if (isNothing) MaterialTheme.typography.labelSmall else MaterialTheme.typography.titleMedium,
                        color = if (isNothing) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Attribution & Audit Watermark
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(if (isNothing) 2.dp else 12.dp))
                    .background(
                        if (isNothing) Color(0xCC000000) else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(SignalRed)
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = if (isNothing) "WIRE PHOTO // 3-SOURCE VERIFIED" else "Verified Wire Photo",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isNothing) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (showCaption) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isNothing) caption.uppercase() else caption,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

private fun getVisualThemeDetails(imageType: String): Pair<ImageVector, String> {
    return when (imageType) {
        "DESALINATION" -> Pair(Icons.Outlined.WaterDrop, "DESALINATION & WATER PURIFICATION FACILITY")
        "MEDICAL" -> Pair(Icons.Outlined.LocalHospital, "PEDIATRIC MEDICAL LOGISTICS CORRIDOR")
        "BAKERY" -> Pair(Icons.Outlined.Image, "UNRWA ROTARY BREAD PRODUCTION DEPOT")
        "INFRASTRUCTURE" -> Pair(Icons.Outlined.WaterDamage, "22KV HIGH-VOLTAGE POWER TRANSMISSION GRID")
        "MARITIME" -> Pair(Icons.Outlined.Image, "INTERNATIONAL COURT OF JUSTICE MARITIME DOCKET")
        "CLIMATE" -> Pair(Icons.Outlined.WaterDrop, "PACIFIC TAO/TRITON TELEMETRY BUOY ARRAY")
        "ENERGY" -> Pair(Icons.Outlined.Image, "SYNCHROTRON 34 keV TOMOGRAPHY BEAMLINE")
        "FINANCE" -> Pair(Icons.Outlined.Image, "BIS PROJECT AGORÁ WHOLESALE SETTLEMENT")
        else -> Pair(Icons.Outlined.Image, "OBJECTIVE FACTUAL WIRE TELEMETRY")
    }
}

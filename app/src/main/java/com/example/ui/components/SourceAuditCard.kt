package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.VerificationSource
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.LocalDeviceProfile
import com.example.ui.theme.SignalRed

@Composable
fun SourceAuditCard(
    sources: List<VerificationSource>,
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS

    Column(
        modifier = modifier
            .testTag("source_audit_section")
            .fillMaxWidth()
    ) {
        // Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(SignalRed)
            )
            Text(
                text = if (isNothing) "3-SOURCE INDEPENDENT CROSS-CHECK REGISTRY" else "3-Source Cross-Verification Registry",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "Every assertion cross-referenced against a minimum of 3 independent primary bureaus with zero editorial bias.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Source Cards List
        val cardPad = LocalDeviceProfile.current.cardPadding
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sources.forEachIndexed { _, source ->
                val shape = RoundedCornerShape(if (isNothing) 4.dp else LocalDeviceProfile.current.cardRadius)
                val cardBg = if (isNothing) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(cardBg)
                        .then(
                            if (isNothing) {
                                Modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape)
                            } else {
                                Modifier
                            }
                        )
                        .padding(cardPad)
                ) {
                    Column {
                        // Source Header & Credibility Score
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 6.dp)
                            ) {
                                NewsChannelLogoBadge(
                                    channelId = source.name,
                                    shortCode = source.name.take(3)
                                )
                                Icon(
                                    imageVector = Icons.Outlined.Verified,
                                    contentDescription = "Verified Source",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = source.name,
                                    style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(if (isNothing) 2.dp else 8.dp))
                                    .background(
                                        if (isNothing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${source.credibilityScore}% ${source.tier.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isNothing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "VERIFIED // ${source.timestamp.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Corroborating Excerpt in Quotation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(if (isNothing) 2.dp else 10.dp))
                                .background(if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface)
                                .then(
                                    if (isNothing) Modifier.border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    else Modifier
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                text = source.corroboratingExcerpt,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.DiscrepancyClaim
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.SignalRed

@Composable
fun DiscrepancyCallout(
    discrepancy: DiscrepancyClaim,
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS

    val shape = RoundedCornerShape(if (isNothing) 4.dp else 18.dp)
    val cardBackground = if (isNothing) {
        Color(0xFF140707) // Deep dark red-tinted surface for Nothing OS
    } else {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
    }

    Box(
        modifier = modifier
            .testTag("discrepancy_callout")
            .fillMaxWidth()
            .clip(shape)
            .background(cardBackground)
            .border(
                width = if (isNothing) 1.dp else 1.5.dp,
                color = if (isNothing) SignalRed.copy(alpha = 0.8f) else MaterialTheme.colorScheme.error,
                shape = shape
            )
            .padding(14.dp)
    ) {
        Column {
            // Header Warning Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(SignalRed)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = if (isNothing) "[FLAGGED DISCREPANCY // FACT CHECK ACTIVE]" else "Flagged Claim Discrepancy",
                    style = MaterialTheme.typography.labelMedium,
                    color = SignalRed
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contested Claim Quote
            Text(
                text = "\"${discrepancy.claimText}\"",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Variance Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(if (isNothing) 2.dp else 12.dp))
                    .background(if (isNothing) Color(0xFF0F0F0F) else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    .padding(10.dp)
            ) {
                Text(
                    text = if (isNothing) "VARIANCE AUDIT:" else "Cross-Source Variance:",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isNothing) SignalRed else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = discrepancy.varianceDetails,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isNothing) "ESTABLISHED CONSENSUS FACT:" else "Verified Consensus Fact:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = discrepancy.establishedConsensus,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

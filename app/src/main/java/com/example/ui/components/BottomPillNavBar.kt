package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.FeedTab
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.SignalRed

@Composable
fun BottomPillNavBar(
    activeTab: FeedTab,
    isNothing: Boolean,
    onSelectTab: (FeedTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .widthIn(min = 248.dp, max = 380.dp)
            .testTag("bottom_pill_nav_bar"),
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = if (isNothing) 0.dp else 8.dp,
        shadowElevation = if (isNothing) 4.dp else 8.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PillTabItem(
                label = stringResource(if (isNothing) R.string.nav_my_feed_nothing else R.string.nav_my_feed_material),
                iconRes = R.drawable.ic_crown,
                contentDescription = "My Feed",
                selected = activeTab == FeedTab.MY_FEED,
                isNothing = isNothing,
                onClick = { onSelectTab(FeedTab.MY_FEED) },
                testTag = "bottom_nav_my_feed",
                modifier = Modifier.weight(1f)
            )

            PillTabItem(
                label = stringResource(if (isNothing) R.string.nav_world_nothing else R.string.nav_world_material),
                iconRes = R.drawable.ic_planet,
                contentDescription = "World Feed",
                selected = activeTab == FeedTab.WORLD,
                isNothing = isNothing,
                onClick = { onSelectTab(FeedTab.WORLD) },
                testTag = "bottom_nav_world",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PillTabItem(
    label: String,
    iconRes: Int,
    contentDescription: String,
    selected: Boolean,
    isNothing: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = when {
            selected && isNothing -> MaterialTheme.colorScheme.primary
            selected && !isNothing -> MaterialTheme.colorScheme.primaryContainer
            else -> Color.Transparent
        },
        animationSpec = spring(stiffness = 450f),
        label = "pill_tab_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            selected && isNothing -> MaterialTheme.colorScheme.onPrimary
            selected && !isNothing -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(stiffness = 450f),
        label = "pill_tab_fg"
    )

    val hapticEngine = LocalHapticEngine.current

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(containerColor)
            .clickable {
                hapticEngine?.trigger(HapticCue.UI_NAV)
                onClick()
            }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
        if (selected && isNothing) {
            Spacer(modifier = Modifier.width(5.dp))
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(SignalRed)
            )
        }
    }
}

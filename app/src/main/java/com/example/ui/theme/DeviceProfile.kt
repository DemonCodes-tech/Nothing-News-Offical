package com.example.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DeviceProfile(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val isSmallScreen: Boolean, // < 380dp width or < 560dp height
    val isCompact: Boolean,     // < 600dp width (standard phone)
    val isMedium: Boolean,      // 600dp .. 839dp (foldable unfolded, mini tablet)
    val isExpanded: Boolean,    // >= 840dp (large tablet)
    val isLandscape: Boolean,
    val isTwoPane: Boolean,     // >= 720dp width
    val contentPadding: Dp,
    val cardRadius: Dp,
    val cardPadding: Dp = 10.dp,
    val itemSpacing: Dp = 8.dp,
    val feedImageHeight: Dp = 106.dp,
    val detailImageHeight: Dp = 146.dp
)

val LocalDeviceProfile = compositionLocalOf {
    DeviceProfile(
        screenWidth = 400.dp,
        screenHeight = 800.dp,
        isSmallScreen = false,
        isCompact = true,
        isMedium = false,
        isExpanded = false,
        isLandscape = false,
        isTwoPane = false,
        contentPadding = 10.dp,
        cardRadius = 4.dp,
        cardPadding = 10.dp,
        itemSpacing = 8.dp,
        feedImageHeight = 106.dp,
        detailImageHeight = 146.dp
    )
}

fun calculateDeviceProfile(width: Dp, height: Dp, isNothing: Boolean): DeviceProfile {
    val isSmall = width < 380.dp || height < 560.dp
    val isCompact = width < 600.dp
    val isMedium = width in 600.dp..839.dp
    val isExpanded = width >= 840.dp
    val isLandscape = width > height
    val isTwoPane = width >= 720.dp

    val padding = when {
        isSmall -> 8.dp
        isCompact -> 10.dp
        isMedium -> 16.dp
        else -> 22.dp
    }

    val cardPadding = when {
        isSmall -> 8.dp
        isCompact -> 10.dp
        else -> 12.dp
    }

    val itemSpacing = when {
        isSmall -> 6.dp
        isCompact -> 8.dp
        else -> 12.dp
    }

    val feedImageHeight = when {
        isSmall -> 94.dp
        isCompact -> 106.dp
        else -> 136.dp
    }

    val detailImageHeight = when {
        isSmall -> 126.dp
        isCompact -> 146.dp
        else -> 184.dp
    }

    val radius = if (isNothing) {
        if (isSmall) 2.dp else 4.dp
    } else {
        if (isSmall) 12.dp else 16.dp
    }

    return DeviceProfile(
        screenWidth = width,
        screenHeight = height,
        isSmallScreen = isSmall,
        isCompact = isCompact,
        isMedium = isMedium,
        isExpanded = isExpanded,
        isLandscape = isLandscape,
        isTwoPane = isTwoPane,
        contentPadding = padding,
        cardRadius = radius,
        cardPadding = cardPadding,
        itemSpacing = itemSpacing,
        feedImageHeight = feedImageHeight,
        detailImageHeight = detailImageHeight
    )
}

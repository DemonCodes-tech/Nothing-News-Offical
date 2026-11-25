package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Tsunami
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AlignmentMode
import com.example.data.model.BackgroundSyncPreferences
import com.example.data.model.BroadcasterRegistry
import com.example.data.model.PersonalizationCatalog
import com.example.data.model.UserFeedProfile
import com.example.data.notifications.NewsNotificationHelper
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NothingGreenBorderDark
import com.example.ui.theme.NothingGreenBorderLight
import com.example.ui.theme.NothingGreenTintDark
import com.example.ui.theme.NothingGreenTintLight
import com.example.ui.theme.SignalRed
import com.example.ui.viewmodel.NotificationPreferences
import kotlin.math.roundToInt

@Composable
fun TutorialDialog(
    visible: Boolean,
    currentStep: Int,
    currentDesignStyle: DesignStyle,
    appFontScale: Float = 1.0f,
    notificationPreferences: NotificationPreferences,
    favoriteBroadcasters: Set<String> = emptySet(),
    userFeedProfile: UserFeedProfile = UserFeedProfile(),
    backgroundSyncPreferences: BackgroundSyncPreferences = BackgroundSyncPreferences(),
    isUpgradingOldUser: Boolean = false,
    hasNotificationPermission: Boolean = true,
    isBatteryExempted: Boolean = false,
    onRequestNotificationPermission: () -> Unit = {},
    onRequestBatteryExemption: () -> Unit = {},
    onSendTestNotification: () -> Unit = {},
    onToggleFavoriteBroadcaster: (String) -> Unit = {},
    onSelectDesignStyle: (DesignStyle) -> Unit,
    onAppFontScaleChange: (Float) -> Unit = {},
    onResetAppFontScale: () -> Unit = {},
    onToggleNotification: (String) -> Unit,
    onSelectCountry: (String) -> Unit = {},
    onSelectAlignmentMode: (AlignmentMode) -> Unit = {},
    onToggleParty: (String) -> Unit = {},
    onSelectSpectrum: (String) -> Unit = {},
    onToggleAllSides: () -> Unit = {},
    onToggleTopic: (String) -> Unit = {},
    onSelectAllTopics: () -> Unit = {},
    onClearTopics: () -> Unit = {},
    onToggleWorkManagerSync: () -> Unit = {},
    onToggleLiveSyncService: () -> Unit = {},
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val context = LocalContext.current
    val isNothing = currentDesignStyle == DesignStyle.NOTHING_OS
    val isDark = LocalIsDarkTheme.current
    val hapticEngine = LocalHapticEngine.current
    val ambientTouchBus = LocalAmbientTouchBus.current

    LaunchedEffect(currentStep) {
        if (
            (currentStep == 2 || currentStep == 7) &&
            !hasNotificationPermission &&
            NewsNotificationHelper.requiresRuntimePermission() &&
            !NewsNotificationHelper.hasPromptedPermissionOnce(context)
        ) {
            onRequestNotificationPermission()
        }
    }

    BackHandler(enabled = visible) {
        if (currentStep > 0) onPrev() else onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isNothing) Color.Black.copy(alpha = 0.72f) else Color.Black.copy(alpha = 0.42f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            val isWideLayout = maxWidth >= 560.dp

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 760.dp)
                    .padding(horizontal = if (isWideLayout) 12.dp else 0.dp)
                    .clip(RoundedCornerShape(if (isNothing) 4.dp else 18.dp))
                    .border(
                        1.dp,
                        if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(if (isNothing) 4.dp else 18.dp)
                    )
                    .testTag("tutorial_setup_dialog"),
                color = if (isNothing) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.background.copy(alpha = 0.88f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .observeAmbientTouchPassThrough(ambientTouchBus, inTutorial = true)
                ) {
                    DotFieldBackground(inTutorialScope = true)
                    ExpressiveTutorialAmbientLayer(currentStep = currentStep)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // --- Top Header: Step Indicator & Skip ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isNothing) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(SignalRed)
                                        )
                                    } else {
                                        ExpressiveAnimatedShapeBadge(
                                            kind = ExpressiveShapeKind.ORGANIC_FLOWER,
                                            size = 14.dp,
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isNothing) "SETUP // STEP ${currentStep + 1} OF 9" else "NewsThing Setup • Step ${currentStep + 1} of 9",
                                        style = if (isNothing) MaterialTheme.typography.labelSmall else MaterialTheme.typography.titleSmall,
                                        color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                // 9-Step Progress Indicator (Black-to-Red Dots in Nothing OS, Squiggle in Material You)
                                val tutorialStepFraction = ((currentStep + 1).toFloat() / 9f).coerceIn(0f, 1f)
                                val animatedTutorialFraction by animateFloatAsState(
                                    targetValue = tutorialStepFraction,
                                    animationSpec = tween(durationMillis = 240),
                                    label = "tutorial_step_progress"
                                )
                                if (isNothing) {
                                    SegmentedProgressBar(
                                        progress = tutorialStepFraction,
                                        showLabel = false,
                                        totalSegments = 9,
                                        dotSize = 7.dp,
                                        modifier = Modifier
                                            .widthIn(max = 172.dp)
                                            .testTag("setup_dots_progress")
                                    )
                                } else {
                                    SquiggleProgressBar(
                                        progress = animatedTutorialFraction,
                                        scrollPhaseFraction = animatedTutorialFraction,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(end = 8.dp)
                                            .testTag("setup_squiggle_progress"),
                                        trackHeight = 12.dp,
                                        waveAmplitude = 2.5.dp,
                                        wavelength = 18.dp,
                                        strokeWidth = 2.6.dp,
                                        dotRadius = 3.8.dp,
                                        accessibilityLabel = "Setup step ${currentStep + 1} of 9"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Box(
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                    .clip(RoundedCornerShape(if (isNothing) 2.dp else 14.dp))
                                    .clickable {
                                        hapticEngine?.trigger(HapticCue.UI_NAV)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 10.dp)
                                    .testTag("setup_skip_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isNothing) "[SKIP]" else "Skip",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                    if (isUpgradingOldUser) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("returning_user_upgrade_banner"),
                            shape = RoundedCornerShape(if (isNothing) 3.dp else 10.dp),
                            color = if (isNothing) {
                                if (isDark) NothingGreenTintDark else NothingGreenTintLight
                            } else {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isNothing) {
                                    if (isDark) NothingGreenBorderDark else NothingGreenBorderLight
                                } else {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                                }
                            )
                        ) {
                            Text(
                                text = if (isNothing) "UPGRADE SYNC // OLD SETTINGS & SAVED WIRES PRESERVED" else "Welcome back! All your old settings & saved articles are preserved.",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isNothing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 9.5.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // --- Adaptive Content Area ---
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        val contentAreaHeight = maxHeight

                        AnimatedContent(
                            targetState = currentStep,
                            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                            label = "setup_step_content"
                        ) { step ->
                            val controlsScrollState = rememberScrollState()
                            LaunchedEffect(step) {
                                controlsScrollState.scrollTo(0)
                            }

                            if (isWideLayout) {
                                // Wide / Tablet / Landscape: Full-Height Side-by-Side
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(0.40f)
                                            .fillMaxHeight(),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        HugeMarinGuide(
                                            step = step,
                                            isNothing = isNothing
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(0.60f)
                                            .fillMaxHeight(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        MarinDialogueBubble(
                                            step = step,
                                            isNothing = isNothing,
                                            maxBubbleHeight = (contentAreaHeight * 0.45f).coerceAtLeast(130.dp)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                                .verticalScroll(controlsScrollState)
                                        ) {
                                            StepContentSelector(
                                                step = step,
                                                currentDesignStyle = currentDesignStyle,
                                                appFontScale = appFontScale,
                                                notificationPreferences = notificationPreferences,
                                                favoriteBroadcasters = favoriteBroadcasters,
                                                userFeedProfile = userFeedProfile,
                                                backgroundSyncPreferences = backgroundSyncPreferences,
                                                hasNotificationPermission = hasNotificationPermission,
                                                isBatteryExempted = isBatteryExempted,
                                                onRequestNotificationPermission = onRequestNotificationPermission,
                                                onRequestBatteryExemption = onRequestBatteryExemption,
                                                onSendTestNotification = onSendTestNotification,
                                                onToggleFavoriteBroadcaster = onToggleFavoriteBroadcaster,
                                                onSelectDesignStyle = onSelectDesignStyle,
                                                onAppFontScaleChange = onAppFontScaleChange,
                                                onResetAppFontScale = onResetAppFontScale,
                                                onToggleNotification = onToggleNotification,
                                                onSelectCountry = onSelectCountry,
                                                onSelectAlignmentMode = onSelectAlignmentMode,
                                                onToggleParty = onToggleParty,
                                                onSelectSpectrum = onSelectSpectrum,
                                                onToggleAllSides = onToggleAllSides,
                                                onToggleTopic = onToggleTopic,
                                                onSelectAllTopics = onSelectAllTopics,
                                                onClearTopics = onClearTopics,
                                                onToggleWorkManagerSync = onToggleWorkManagerSync,
                                                onToggleLiveSyncService = onToggleLiveSyncService,
                                                isNothing = isNothing
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Compact Portrait Phone: Larger Marin + Bubble Stage while keeping bottom controls compact
                                val isCompactBottomStep = step == 0 || step == 3 || step == 4 || step == 8
                                val upperStageMax = if (isCompactBottomStep) {
                                    (contentAreaHeight * 0.54f).coerceIn(185.dp, 340.dp)
                                } else {
                                    (contentAreaHeight * 0.42f).coerceIn(155.dp, 245.dp)
                                }
                                val marinMinWidth = if (isCompactBottomStep) 116.dp else 104.dp
                                val marinMaxWidth = if (isCompactBottomStep) 148.dp else 132.dp

                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 148.dp, max = upperStageMax),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .widthIn(min = marinMinWidth, max = marinMaxWidth)
                                                .height(upperStageMax),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            HugeMarinGuide(
                                                step = step,
                                                isNothing = isNothing
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .heightIn(max = upperStageMax),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            MarinDialogueBubble(
                                                step = step,
                                                isNothing = isNothing,
                                                maxBubbleHeight = upperStageMax
                                            )
                                        }
                                    }

                                    // Full-Width Scrollable Interactive Controls
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .verticalScroll(controlsScrollState)
                                    ) {
                                        StepContentSelector(
                                            step = step,
                                            currentDesignStyle = currentDesignStyle,
                                            appFontScale = appFontScale,
                                            notificationPreferences = notificationPreferences,
                                            favoriteBroadcasters = favoriteBroadcasters,
                                            userFeedProfile = userFeedProfile,
                                            backgroundSyncPreferences = backgroundSyncPreferences,
                                            hasNotificationPermission = hasNotificationPermission,
                                            isBatteryExempted = isBatteryExempted,
                                            onRequestNotificationPermission = onRequestNotificationPermission,
                                            onRequestBatteryExemption = onRequestBatteryExemption,
                                            onSendTestNotification = onSendTestNotification,
                                            onToggleFavoriteBroadcaster = onToggleFavoriteBroadcaster,
                                            onSelectDesignStyle = onSelectDesignStyle,
                                            onAppFontScaleChange = onAppFontScaleChange,
                                            onResetAppFontScale = onResetAppFontScale,
                                            onToggleNotification = onToggleNotification,
                                            onSelectCountry = onSelectCountry,
                                            onSelectAlignmentMode = onSelectAlignmentMode,
                                            onToggleParty = onToggleParty,
                                            onSelectSpectrum = onSelectSpectrum,
                                            onToggleAllSides = onToggleAllSides,
                                            onToggleTopic = onToggleTopic,
                                            onSelectAllTopics = onSelectAllTopics,
                                            onClearTopics = onClearTopics,
                                            onToggleWorkManagerSync = onToggleWorkManagerSync,
                                            onToggleLiveSyncService = onToggleLiveSyncService,
                                            isNothing = isNothing
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // --- Bottom Navigation Actions ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (currentStep > 0) {
                            OutlinedButton(
                                onClick = {
                                    hapticEngine?.trigger(HapticCue.UI_NAV)
                                    onPrev()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("setup_prev_button"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(if (isNothing) 2.dp else 20.dp)
                            ) {
                                Text(
                                    text = if (isNothing) "<- BACK" else "Back",
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 2,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = {
                                hapticEngine?.trigger(HapticCue.UI_NAV)
                                onNext()
                            },
                            modifier = Modifier
                                .weight(if (currentStep > 0) 1.85f else 1f)
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("setup_next_button"),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(if (isNothing) 2.dp else 20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = when (currentStep) {
                                    0 -> if (isNothing) "NEXT: CHANNELS ->" else "Next: Broadcasters ->"
                                    1 -> if (isNothing) "NEXT: ALERTS ->" else "Next: Alert Topics ->"
                                    2 -> if (isNothing) "NEXT: PRIVACY ->" else "Next: Zero Privacy ->"
                                    3 -> if (isNothing) "NEXT: FACT AUDIT ->" else "Next: Fact Audit ->"
                                    4 -> if (isNothing) "NEXT: MY FEED ->" else "Next: My Feed ->"
                                    5 -> if (isNothing) "NEXT: CATEGORIES ->" else "Next: News Categories ->"
                                    6 -> if (isNothing) "NEXT: BACKGROUND ->" else "Next: Background ->"
                                    7 -> if (isNothing) "REVIEW SUMMARY ->" else "Review & Launch ->"
                                    8 -> if (isNothing) "INITIALIZE // LAUNCH" else "Launch Live Feed"
                                    else -> "Continue"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 2,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    }

                    MaterialYouForegroundShapesOverlay(compact = true)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// HUGE MARIN CHARACTER STANDING BESIDE THE TUTORIAL CARDS
// -------------------------------------------------------------------------

@Composable
private fun HugeMarinGuide(
    step: Int,
    isNothing: Boolean
) {
    val drawableRes = when (step) {
        0 -> R.drawable.marin_step1 // Full body standing waving in denim jacket
        1 -> R.drawable.marin_step3 // Full body school uniform wink & peace sign
        2 -> R.drawable.marin_step2 // Overshoulder sweater Marin asking for notifications
        3 -> R.drawable.marin_privacy // Marin Zero-Data Privacy Shield cutout
        4 -> R.drawable.marin_step4 // Dark knit sweater laughing (3-Source Audit)
        5 -> R.drawable.marin_personalize // School uniform Marin explaining My Feed Crown & Planet World
        6 -> R.drawable.marin_racing_categories // Racing Queen Marin hyping fast cars, tech, sports, AI & categories
        7 -> R.drawable.marin_background_sync // Knit-sweater pointing Marin explaining Background Sync
        else -> R.drawable.marin_step5 // Radiant close-up smile
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Unconstrained large transparent character illustration
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            ExpressiveMarinStageBackdrop(step = step)
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = "Marin Kitagawa Guide",
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("marin_guide_image_step_$step"),
                contentScale = ContentScale.Fit,
                colorFilter = marinColorFilter(isNothing)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Grounding pedestal / status indicator below feet
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(if (isNothing) 1.dp else 8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 6.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (isNothing) SignalRed else MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = if (isNothing) "MARIN" else "Marin",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
            Text(
                text = "${step + 1}/9",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 9.sp
            )
        }
    }
}

// -------------------------------------------------------------------------
// DIALOGUE BUBBLE WITH SPEECH POINTER POINTING TO MARIN
// -------------------------------------------------------------------------

@Composable
private fun MarinDialogueBubble(
    step: Int,
    isNothing: Boolean,
    maxBubbleHeight: Dp
) {
    val dialogue = when (step) {
        0 -> "Hey, hey! You gotta pick how your news looks! Nothing OS is giving totally stark OLED monochrome with Space Mono dot fonts—it's got this super cool, edgy minimalist vibe! But Material You is just so kawaii with its organic shapes and dynamic Google colors—it's like picking the perfect makeup palette for a new cosplay! Go on, tap either card below to preview the transformation live! You gotta see which one matches your vibe best!"
        1 -> "Oh, and it’s not just about the looks—the content is totally stacked, too! We index over 100 verified international channels, from CNN and BBC to Al Jazeera, DW, Reuters, and NHK—it’s like having a world tour right on your screen! And here's the best part: just tap any channel to star it, and boom—your pinned favorites stay right at the front of your feed carousel! Seriously, it’s total VIP treatment for all your top faves!"
        2 -> "And hey, you'll never miss what actually matters globally! You can totally toggle alerts for overseas natural disasters—like earthquakes and tsunamis—which is super important! Plus, you can get updates on the Gaza and Palestine humanitarian crisis wires... oh, the poor babies, I wish I could help them! It totally breaks my heart! You can also get WHO health alerts! It's like having a super useful safety net right on your phone so you're always in the know. Maji, we gotta stay safe and informed!"
        3 -> "Real talk for a sec—privacy here is 100% legit! Like, literally zero accounts, zero tracking cookies, zero analytics, and zero creepy servers harvesting your reading habits! Everything from your pinned channels and saved wires to your feed settings stays totally local right on your own device. Super clean, zero drama!"
        4 -> "And ugh, speaking of staying informed, you know what’s the absolute worst? Contradictory headlines can be so totally confusing! It’s like, which one are you even supposed to believe?! That’s exactly why NewsThing mandates at least 3 independent global sources for every single wire—it's like having a whole squad of reliable besties double-checking the facts for you! And get this: if the casualty figures or claims disagree, they highlight the variance in this super intense, bold Signal Red! It's like a giant flashing alarm so you can spot the drama instantly! Maji reassuring, right?!"
        5 -> "Ooh, oh my gosh, check this out! You literally get your very own VIP feed right under this cute little crown icon at the bottom! You just pick the country you live in, choose whatever political parties you want—or like, a whole broad spectrum, or even Non-Partisan mode if that's your vibe—and then pick all your fave topics! And get this, you can leave All-Sides Mode totally on so you still get contrasting viewpoints, which is super cool! Next up, let's pick your favorite news categories!"
        6 -> "Vroooom! Check out the racing queen drip! Okay, okay, now comes the absolute BEST part—picking whatever news genres you're totally obsessed with! Are you into fast cars, Formula 1, and electric supercars like me?! Or maybe you want cutting-edge AI breakthroughs, robotics, and sick gaming drops?! We've even got high-stakes world sports, climate science, space exploration, and global finance! Just tap whatever categories you wanna see in your My Feed, or hit Select All if you're a certified hype beast who wants everything! Let's get this engine revved up! Ready, set, GOOO!"
        7 -> "Okay, okay, listen up! So, like, NewsThing can totally keep grabbing all those verified news wires and breaking alerts even when you're not, like, staring at the app! It literally works in the background, too! You just gotta tap 'Allow' down there to give it the battery exemption and notifications—super easy! And get this, you can totally toggle the WorkManager auto-sync or that live background sync service notification whenever you feel like it! Zero creepiness, like, 100% on-device control. So cool!"
        else -> "And okay, everything is totally configured and locked in! But get this—the solo developer who made this actually wanted me to tell you: 'This was coded by literally just ONE guy, and I promise I'll totally update the app, so just tell me what's wrong and I'll fix it!' Seriously?! Just one guy?! Maji respect for the solo grind! You know how much crazy dedication it takes to build something all by yourself?! I totally gotta support a passionate creator like that! And don't worry, I'll be right there in the update log keeping an eye on things to make sure everything is totally okay—you can count on me! Oh, and make sure you guys totally check for updates on the GitHub repo so you don't miss out on any new stuff! Anyway, just tap Initialize to launch your live feed! Let's goooo!"
    }

    val bubbleBg = if (isNothing) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.86f)
    }
    val bubbleBorder = if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
    val dialogueScrollState = rememberScrollState()
    LaunchedEffect(step) {
        dialogueScrollState.scrollTo(0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxBubbleHeight),
        verticalAlignment = Alignment.Top
    ) {
        // Speech Bubble Triangular Pointer pointing LEFT directly toward Marin
        Canvas(
            modifier = Modifier
                .width(10.dp)
                .height(26.dp)
                .padding(top = 10.dp)
        ) {
            val path = Path().apply {
                moveTo(size.width, 0f)
                lineTo(0f, size.height / 2f) // Pointing left towards Marin's face/chest!
                lineTo(size.width, size.height)
                close()
            }
            drawPath(path = path, color = bubbleBg)
        }

        // Dialogue Bubble Body
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(if (isNothing) 4.dp else 16.dp))
                .border(
                    1.dp,
                    bubbleBorder,
                    RoundedCornerShape(if (isNothing) 4.dp else 16.dp)
                )
                .testTag("marin_dialogue_bubble"),
            color = bubbleBg
        ) {
            Column(modifier = Modifier.padding(9.dp)) {
                // Nameplate Header (always visible)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "💬",
                            fontSize = 10.sp
                        )
                        Text(
                            text = if (isNothing) "MARIN KITAGAWA // GUIDE" else "Marin Kitagawa • Guide",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (isNothing) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(SignalRed)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // The Explanatory Speech (scrolls smoothly if taller than maxBubbleHeight)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(dialogueScrollState)
                ) {
                    Text(
                        text = dialogue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.5.sp,
                        lineHeight = 15.5.sp,
                        modifier = Modifier.testTag("marin_dialogue_text_step_$step")
                    )
                }

                if (dialogueScrollState.canScrollForward) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isNothing) "SCROLL ↓" else "Scroll for more ↓",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// STEP CONTENT SELECTOR
// -------------------------------------------------------------------------

@Composable
private fun StepContentSelector(
    step: Int,
    currentDesignStyle: DesignStyle,
    appFontScale: Float,
    notificationPreferences: NotificationPreferences,
    favoriteBroadcasters: Set<String>,
    userFeedProfile: UserFeedProfile,
    backgroundSyncPreferences: BackgroundSyncPreferences,
    hasNotificationPermission: Boolean,
    isBatteryExempted: Boolean,
    onRequestNotificationPermission: () -> Unit,
    onRequestBatteryExemption: () -> Unit,
    onSendTestNotification: () -> Unit,
    onToggleFavoriteBroadcaster: (String) -> Unit,
    onSelectDesignStyle: (DesignStyle) -> Unit,
    onAppFontScaleChange: (Float) -> Unit,
    onResetAppFontScale: () -> Unit,
    onToggleNotification: (String) -> Unit,
    onSelectCountry: (String) -> Unit,
    onSelectAlignmentMode: (AlignmentMode) -> Unit,
    onToggleParty: (String) -> Unit,
    onSelectSpectrum: (String) -> Unit,
    onToggleAllSides: () -> Unit,
    onToggleTopic: (String) -> Unit,
    onSelectAllTopics: () -> Unit,
    onClearTopics: () -> Unit,
    onToggleWorkManagerSync: () -> Unit,
    onToggleLiveSyncService: () -> Unit,
    isNothing: Boolean
) {
    when (step) {
        0 -> StepSelectOperatingSystem(
            currentDesignStyle = currentDesignStyle,
            appFontScale = appFontScale,
            onSelectDesignStyle = onSelectDesignStyle,
            onAppFontScaleChange = onAppFontScaleChange,
            onResetAppFontScale = onResetAppFontScale
        )
        1 -> StepSelectBroadcasters(
            favoriteBroadcasters = favoriteBroadcasters,
            onToggleFavoriteBroadcaster = onToggleFavoriteBroadcaster,
            isNothing = isNothing
        )
        2 -> StepNotificationPreferences(
            notificationPreferences = notificationPreferences,
            hasNotificationPermission = hasNotificationPermission,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onSendTestNotification = onSendTestNotification,
            onToggleNotification = onToggleNotification,
            isNothing = isNothing
        )
        3 -> StepZeroDataPrivacy(isNothing = isNothing)
        4 -> StepTriSourceVerification()
        5 -> FeedPersonalizationEditor(
            profile = userFeedProfile,
            isNothing = isNothing,
            compact = true,
            showTopics = false,
            onSelectCountry = onSelectCountry,
            onSelectAlignmentMode = onSelectAlignmentMode,
            onToggleParty = onToggleParty,
            onSelectSpectrum = onSelectSpectrum,
            onToggleAllSides = onToggleAllSides,
            onToggleTopic = onToggleTopic
        )
        6 -> StepChooseNewsCategories(
            profile = userFeedProfile,
            isNothing = isNothing,
            onToggleTopic = onToggleTopic,
            onSelectAllTopics = onSelectAllTopics,
            onClearTopics = onClearTopics
        )
        7 -> BackgroundSyncControlPanel(
            syncPrefs = backgroundSyncPreferences,
            hasNotificationPermission = hasNotificationPermission,
            isBatteryExempted = isBatteryExempted,
            isNothing = isNothing,
            compact = true,
            onToggleWorkManager = onToggleWorkManagerSync,
            onToggleLiveService = onToggleLiveSyncService,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onRequestBatteryExemption = onRequestBatteryExemption
        )
        8 -> StepSetupSummary(
            currentDesignStyle = currentDesignStyle,
            appFontScale = appFontScale,
            notificationPreferences = notificationPreferences,
            favoriteCount = favoriteBroadcasters.size,
            userFeedProfile = userFeedProfile,
            backgroundSyncPreferences = backgroundSyncPreferences,
            isNothing = isNothing
        )
    }
}

// -------------------------------------------------------------------------
// STEP 0: OS CHOOSER CARDS + ACCESSIBILITY TEXT SIZE
// -------------------------------------------------------------------------

@Composable
private fun StepSelectOperatingSystem(
    currentDesignStyle: DesignStyle,
    appFontScale: Float,
    onSelectDesignStyle: (DesignStyle) -> Unit,
    onAppFontScaleChange: (Float) -> Unit,
    onResetAppFontScale: () -> Unit
) {
    val isNothingActive = currentDesignStyle == DesignStyle.NOTHING_OS
    val isMaterialYouActive = currentDesignStyle == DesignStyle.MATERIAL_YOU
    val hapticEngine = LocalHapticEngine.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "CHOOSE INTERFACE ENGINE",
            style = MaterialTheme.typography.labelSmall,
            color = SignalRed,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )

        // CARD 1: NOTHING OS
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .border(
                    width = if (isNothingActive) 2.dp else 1.dp,
                    color = if (isNothingActive) Color.White else Color(0xFF2A2A2A),
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                    onSelectDesignStyle(DesignStyle.NOTHING_OS)
                }
                .testTag("setup_choose_nothing_os"),
            color = Color(0xFF0D0D0D)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SignalRed)
                        )
                        Text(
                            text = "NOTHING OS",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isNothingActive) Color.White else Color(0xFF1E1E1E))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isNothingActive) "ACTIVE ✓" else "SELECT",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isNothingActive) Color.Black else Color(0xFFAAAAAA),
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "OLED black contrast, Space Mono dot typography, mechanical toggles, and Signal Red accents.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFAAAAAA),
                    fontSize = 10.sp
                )
            }
        }

        // CARD 2: MATERIAL YOU
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(
                    width = if (isMaterialYouActive) 2.dp else 1.dp,
                    color = if (isMaterialYouActive) Color(0xFF6750A4) else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable {
                    hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                    onSelectDesignStyle(DesignStyle.MATERIAL_YOU)
                }
                .testTag("setup_choose_material_you"),
            color = Color(0xFFF3EDF7)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6750A4))
                        )
                        Text(
                            text = "MATERIAL YOU",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF1D1B20),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        if (isMaterialYouActive) {
                            ExpressiveShapeCluster()
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isMaterialYouActive) Color(0xFF6750A4) else Color(0xFFE8DEF8))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isMaterialYouActive) "ACTIVE ✓" else "SELECT",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isMaterialYouActive) Color.White else Color(0xFF49454F),
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Google dynamic color palette, animated M3 expressive flowers & scalloped squircles, and spring motion.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF49454F),
                    fontSize = 10.sp
                )
            }
        }

        // Accessibility Text Size Slider directly inside Step 0
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tutorial_accessibility_text_size_card"),
            shape = RoundedCornerShape(if (isNothingActive) 4.dp else 14.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isNothingActive) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            AccessibilityTextScaleControl(
                appFontScale = appFontScale,
                isNothing = isNothingActive,
                compact = true,
                showPreview = false,
                onFontScaleChange = onAppFontScaleChange,
                onResetFontScale = onResetAppFontScale,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

// -------------------------------------------------------------------------
// STEP 1: PIN FAVORITE BROADCASTERS
// -------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepSelectBroadcasters(
    favoriteBroadcasters: Set<String>,
    onToggleFavoriteBroadcaster: (String) -> Unit,
    isNothing: Boolean
) {
    val topOutlets = listOf(
        "cnn_int", "bbc_news", "sky_news", "al_jazeera_en", "france_24",
        "dw", "rt_int", "cgtn", "nhk_world", "euronews", "africanews",
        "bloomberg", "cnbc", "fox_news", "msnbc", "reuters", "cna", "cbc_news",
        "abc_news", "nbc_news", "cbs_news", "the_guardian", "ndtv", "trt_world", "un_news"
    )

    val hapticEngine = LocalHapticEngine.current
    val broadcasters = topOutlets.mapNotNull { id ->
        BroadcasterRegistry.allBroadcasters.find { it.id == id }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GLOBAL BROADCASTERS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Text(
                text = "${favoriteBroadcasters.size} PINNED",
                style = MaterialTheme.typography.labelSmall,
                color = SignalRed,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            broadcasters.forEach { b ->
                val isPinned = favoriteBroadcasters.contains(b.id)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(if (isNothing) 2.dp else 12.dp))
                        .background(
                            if (isPinned) {
                                if (isNothing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                            } else {
                                if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface
                            }
                        )
                        .border(
                            1.dp,
                            if (isPinned) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                            },
                            RoundedCornerShape(if (isNothing) 2.dp else 12.dp)
                        )
                        .clickable {
                            hapticEngine?.trigger(HapticCue.UI_TAP)
                            onToggleFavoriteBroadcaster(b.id)
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("setup_pin_broadcaster_${b.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NewsChannelLogoBadge(
                        channelId = b.id,
                        shortCode = b.shortCode,
                        badgeColorHex = b.badgeColorHex
                    )

                    Text(
                        text = b.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = if (isPinned) {
                            if (isNothing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Icon(
                        imageVector = if (isPinned) Icons.Outlined.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Pin Favorite",
                        tint = if (isPinned) SignalRed else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// STEP 2: CONFIGURE NOTIFICATIONS
// -------------------------------------------------------------------------

@Composable
private fun StepNotificationPreferences(
    notificationPreferences: NotificationPreferences,
    hasNotificationPermission: Boolean,
    onRequestNotificationPermission: () -> Unit,
    onSendTestNotification: () -> Unit,
    onToggleNotification: (String) -> Unit,
    isNothing: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "BREAKING DISPATCH CHANNELS",
            style = MaterialTheme.typography.labelSmall,
            color = SignalRed,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )

        DeviceNotificationAccessBanner(
            hasPermission = hasNotificationPermission,
            isNothing = isNothing,
            onRequestPermission = onRequestNotificationPermission,
            onSendTestNotification = onSendTestNotification
        )

        NotificationRowItem(
            title = "NATURAL DISASTERS ABROAD",
            description = "Earthquakes, tsunamis, volcanic eruptions, cyclones.",
            icon = Icons.Outlined.Tsunami,
            checked = notificationPreferences.naturalDisasters,
            onCheckedChange = { onToggleNotification("natural_disasters") },
            isNothing = isNothing,
            testTag = "setup_notif_disasters"
        )

        NotificationRowItem(
            title = "HUMANITARIAN CRISES",
            description = "UN OCHA, UNRWA, Palestine & Gaza conflict dispatches.",
            icon = Icons.Outlined.Security,
            checked = notificationPreferences.humanitarianCrisis,
            onCheckedChange = { onToggleNotification("humanitarian_crisis") },
            isNothing = isNothing,
            testTag = "setup_notif_humanitarian"
        )

        NotificationRowItem(
            title = "HEALTH EMERGENCIES",
            description = "WHO pathogen bulletins and quarantine levels.",
            icon = Icons.Outlined.HealthAndSafety,
            checked = notificationPreferences.healthPandemic,
            onCheckedChange = { onToggleNotification("health_pandemic") },
            isNothing = isNothing,
            testTag = "setup_notif_health"
        )

        NotificationRowItem(
            title = "SCIENCE & CLIMATE",
            description = "NASA telemetry, magnetic storms, extreme weather.",
            icon = Icons.Outlined.Science,
            checked = notificationPreferences.scienceClimate,
            onCheckedChange = { onToggleNotification("science_climate") },
            isNothing = isNothing,
            testTag = "setup_notif_science"
        )

        NotificationRowItem(
            title = "TRADE & SUPPLY CHAINS",
            description = "Corridor closures and energy transit disruptions.",
            icon = Icons.Outlined.ElectricBolt,
            checked = notificationPreferences.economyEnergy,
            onCheckedChange = { onToggleNotification("economy_energy") },
            isNothing = isNothing,
            testTag = "setup_notif_economy"
        )
    }
}

@Composable
private fun NotificationRowItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    isNothing: Boolean,
    testTag: String
) {
    val hapticEngine = LocalHapticEngine.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (isNothing) 4.dp else 12.dp))
            .background(if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                if (checked) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                } else {
                    if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                },
                RoundedCornerShape(if (isNothing) 4.dp else 12.dp)
            )
            .clickable {
                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                onCheckedChange()
            }
            .padding(horizontal = 7.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (checked) SignalRed else MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(16.dp)
                .padding(end = 3.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (checked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.secondary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 8.5.sp,
                lineHeight = 10.5.sp
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        if (isNothing) {
            MechanicalSwitch(
                checked = checked,
                onCheckedChange = { onCheckedChange() }
            )
        } else {
            Switch(
                checked = checked,
                onCheckedChange = {
                    hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                    onCheckedChange()
                },
                modifier = Modifier.scale(0.82f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

// -------------------------------------------------------------------------
// STEP 3: ZERO-DATA PRIVACY PLEDGE
// -------------------------------------------------------------------------

@Composable
private fun StepZeroDataPrivacy(
    isNothing: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (isNothing) "LOCAL STORAGE // ZERO TELEMETRY" else "Privacy Guarantee",
            style = MaterialTheme.typography.labelSmall,
            color = SignalRed,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )

        MarinPrivacyStatementCard(
            isNothing = isNothing,
            compact = false
        )

        Text(
            text = "• Zero Account Required: Open the wire immediately—never login or hand over an email.\n\n• 100% On-Device Room & Prefs: Saved articles, country selection, and broadcaster stars stay strictly inside your phone.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 10.sp,
            lineHeight = 14.sp
        )
    }
}

// -------------------------------------------------------------------------
// STEP 4: 3-SOURCE AUDITING
// -------------------------------------------------------------------------

@Composable
private fun StepTriSourceVerification() {
    val isNothing = LocalDesignStyle.current == DesignStyle.NOTHING_OS
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 76.dp)
                .clip(RoundedCornerShape(if (isNothing) 4.dp else 16.dp))
                .background(if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface)
                .border(
                    1.dp,
                    if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(if (isNothing) 4.dp else 16.dp)
                )
                .padding(vertical = 8.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedTriSourceGraphic()
        }

        Text(
            text = "3-SOURCE FACT AUDITING",
            style = MaterialTheme.typography.labelSmall,
            color = SignalRed,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )

        Text(
            text = "• Tri-Source Mandate: Every wire reports at least 3 verified international networks (UN OCHA, WHO, Reuters, BBC, AP).\n\n• Variance Tags: When casualty counts or claims contradict, NewsThing highlights the discrepancy in Signal Red.\n\n• Zero Sensationalism: 100% factual wire ingestion checked every 60 minutes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 10.sp,
            lineHeight = 14.sp
        )
    }
}

// -------------------------------------------------------------------------
// STEP 8: SUMMARY & GITHUB REPOSITORY LINK
// -------------------------------------------------------------------------

@Composable
private fun StepSetupSummary(
    currentDesignStyle: DesignStyle,
    appFontScale: Float,
    notificationPreferences: NotificationPreferences,
    favoriteCount: Int,
    userFeedProfile: UserFeedProfile,
    backgroundSyncPreferences: BackgroundSyncPreferences,
    isNothing: Boolean
) {
    val context = LocalContext.current
    val hapticEngine = LocalHapticEngine.current
    val countryName = PersonalizationCatalog.findCountry(userFeedProfile.countryCode).name
    val alignmentLabel = when (userFeedProfile.alignmentMode) {
        AlignmentMode.PARTIES -> "${userFeedProfile.selectedParties.size} PARTIES"
        AlignmentMode.SPECTRUM -> userFeedProfile.selectedSpectrum.uppercase()
        AlignmentMode.NON_PARTISAN -> "NON-PARTISAN"
    }
    val textSizePercent = (appFontScale * 100f).roundToInt().coerceIn(100, 150)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Summary details card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(if (isNothing) 4.dp else 14.dp))
                .background(if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface)
                .border(
                    1.dp,
                    if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(if (isNothing) 4.dp else 14.dp)
                )
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "INTERFACE ENGINE:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 9.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (currentDesignStyle == DesignStyle.NOTHING_OS) "NOTHING OS" else "MATERIAL YOU",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    textAlign = TextAlign.End
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "MY FEED COUNTRY & LENS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 9.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$countryName • $alignmentLabel",
                    style = MaterialTheme.typography.labelSmall,
                    color = SignalRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    textAlign = TextAlign.End
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "TOPICS & PINNED:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 9.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${userFeedProfile.selectedTopics.size} TOPICS • $favoriteCount OUTLETS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    textAlign = TextAlign.End
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "TEXT SIZE:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 9.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$textSizePercent%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (textSizePercent > 100) SignalRed else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    textAlign = TextAlign.End
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "BACKGROUND & ALERTS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 9.sp,
                    modifier = Modifier.weight(1f)
                )
                val enabledCount = listOf(
                    notificationPreferences.naturalDisasters,
                    notificationPreferences.humanitarianCrisis,
                    notificationPreferences.healthPandemic,
                    notificationPreferences.scienceClimate,
                    notificationPreferences.economyEnergy
                ).count { it }
                val bgLabel = if (backgroundSyncPreferences.liveServiceEnabled) "LIVE SVC" else if (backgroundSyncPreferences.workManagerEnabled) "WORKMGR" else "MANUAL"
                Text(
                    text = "$bgLabel • $enabledCount ALERTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = SignalRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    textAlign = TextAlign.End
                )
            }
        }

        // GitHub Repository Link Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .clip(RoundedCornerShape(if (isNothing) 4.dp else 14.dp))
                .clickable {
                    hapticEngine?.trigger(HapticCue.UI_TAP)
                    openGitHubRepository(context)
                }
                .testTag("setup_github_repo_button"),
            shape = RoundedCornerShape(if (isNothing) 4.dp else 14.dp),
            color = if (isNothing) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.secondaryContainer,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isNothing) SignalRed.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Code,
                        contentDescription = "GitHub Repository",
                        tint = if (isNothing) SignalRed else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = if (isNothing) "GITHUB REPO // UPDATES" else "Check GitHub Repository",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isNothing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "DemonCodes-tech/Newsthing",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isNothing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = "Open GitHub Repository in browser",
                    tint = if (isNothing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// REUSABLE GRAPHICS & MECHANICAL SWITCH
// -------------------------------------------------------------------------

@Composable
private fun AnimatedTriSourceGraphic() {
    val infiniteTransition = rememberInfiniteTransition(label = "tri_anim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "tri_pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("UN OCHA", "REUTERS", "WHO").forEach { name ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = pulse), RoundedCornerShape(3.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "✓ $name",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 8.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(SignalRed)
                .padding(horizontal = 7.dp, vertical = 2.dp)
        ) {
            Text(
                text = "100% FACT CONSENSUS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontSize = 8.sp
            )
        }
    }
}

@Composable
fun MechanicalSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isNothing = LocalDesignStyle.current == DesignStyle.NOTHING_OS
    val hapticEngine = LocalHapticEngine.current

    Box(
        modifier = modifier
            .width(38.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(if (isNothing) 2.dp else 12.dp))
            .background(
                if (checked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .border(
                1.dp,
                if (checked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                RoundedCornerShape(if (isNothing) 2.dp else 12.dp)
            )
            .clickable {
                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                onCheckedChange(!checked)
            }
            .padding(2.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(width = 14.dp, height = 14.dp)
                .clip(RoundedCornerShape(if (isNothing) 1.dp else 10.dp))
                .background(
                    if (checked) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(if (isNothing) SignalRed else MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

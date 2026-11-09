package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.PublicOff
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Tsunami
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.R
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.SignalRed
import com.example.ui.viewmodel.NotificationPreferences
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TutorialDialog(
    visible: Boolean,
    currentStep: Int,
    currentDesignStyle: DesignStyle,
    notificationPreferences: NotificationPreferences,
    onSelectDesignStyle: (DesignStyle) -> Unit,
    onToggleNotification: (String) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .clip(RoundedCornerShape(if (isNothing) 4.dp else 28.dp))
                .border(
                    1.dp,
                    if (isNothing) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(if (isNothing) 4.dp else 28.dp)
                )
                .testTag("tutorial_setup_dialog"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // --- Top Header with Step Tracker & Close ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isNothing) "SYSTEM ONBOARDING SETUP" else "Welcome to Nothing News",
                            style = if (isNothing) MaterialTheme.typography.labelSmall else MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (i in 0 until 5) {
                                val isActive = i <= currentStep
                                Box(
                                    modifier = Modifier
                                        .size(width = 24.dp, height = 4.dp)
                                        .clip(RoundedCornerShape(if (isNothing) 1.dp else 2.dp))
                                        .background(
                                            if (isActive) {
                                                if (isNothing) Color.White else MaterialTheme.colorScheme.primary
                                            } else {
                                                if (isNothing) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            }
                                        )
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isNothing) "[SKIP SETUP]" else "Skip",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clickable { onDismiss() }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("setup_skip_button")
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Close Setup",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- Main Step Content ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                        label = "setup_step_content"
                    ) { step ->
                        when (step) {
                            0 -> StepSelectOperatingSystem(
                                currentDesignStyle = currentDesignStyle,
                                onSelectDesignStyle = onSelectDesignStyle
                            )
                            1 -> StepFactArchitecture()
                            2 -> StepTriSourceVerification()
                            3 -> StepNotificationPreferences(
                                notificationPreferences = notificationPreferences,
                                onToggleNotification = onToggleNotification,
                                isNothing = isNothing
                            )
                            4 -> StepSetupSummary(
                                currentDesignStyle = currentDesignStyle,
                                notificationPreferences = notificationPreferences,
                                isNothing = isNothing
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- Bottom Navigation Actions ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = onPrev,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("setup_prev_button"),
                            shape = RoundedCornerShape(if (isNothing) 2.dp else 24.dp)
                        ) {
                            Text(
                                text = if (isNothing) "<- BACK" else "Back",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Button(
                        onClick = onNext,
                        modifier = Modifier
                            .weight(if (currentStep > 0) 2f else 1f)
                            .height(48.dp)
                            .testTag("setup_next_button"),
                        shape = RoundedCornerShape(if (isNothing) 2.dp else 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isNothing) Color.White else MaterialTheme.colorScheme.primary,
                            contentColor = if (isNothing) Color.Black else MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = when (currentStep) {
                                0 -> if (isNothing) "CONFIRM OS // STEP 2 ->" else "Continue to Features"
                                3 -> if (isNothing) "SAVE NOTIFS // STEP 5 ->" else "Review & Finish"
                                4 -> if (isNothing) "INITIALIZE NOTHING NEWS // [LAUNCH]" else "Start Reading News"
                                else -> if (isNothing) "CONTINUE // STEP ${currentStep + 1}/5 ->" else "Next Step"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// STEP 0: SPLIT SCREEN OS CHOOSER WITH SHAPES ON EACH SIDE
// -------------------------------------------------------------------------

@Composable
private fun StepSelectOperatingSystem(
    currentDesignStyle: DesignStyle,
    onSelectDesignStyle: (DesignStyle) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CHOOSE YOUR INTERFACE",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "TAP A SIDE TO PREVIEW LIVE OS EXPERIENCE",
            style = MaterialTheme.typography.labelSmall,
            color = SignalRed
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Split Container with distinct shapes on each side
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF333333), RoundedCornerShape(8.dp))
        ) {
            // LEFT SIDE: NOTHING OS (Industrial Monochrome & Sharp Micro-LED Shapes)
            val isNothingActive = currentDesignStyle == DesignStyle.NOTHING_OS
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF0D0D0D))
                    .border(
                        width = if (isNothingActive) 2.dp else 0.dp,
                        color = if (isNothingActive) Color.White else Color.Transparent
                    )
                    .clickable { onSelectDesignStyle(DesignStyle.NOTHING_OS) }
                    .padding(10.dp)
                    .testTag("setup_choose_nothing_os"),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(SignalRed)
                            )
                            Text(
                                text = "NOTHING OS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "MONOCHROME",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF888888)
                        )
                    }

                    // Nothing OS Shapes Showcase
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        // Shape 1: 5x5 Micro-LED Glyph Grid
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF161616))
                                .border(1.dp, Color(0xFF2E2E2E), RoundedCornerShape(2.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(4) { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        repeat(4) { col ->
                                            val isLit = (row == col) || (row + col == 3)
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isLit) Color.White else Color(0xFF2B2B2B)
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Shape 2: Sharp Rectangular 1px Border Card
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(26.dp)
                                .clip(RoundedCornerShape(0.dp))
                                .background(Color(0xFF1A1A1A))
                                .border(1.dp, Color(0xFF3E3E3E), RoundedCornerShape(0.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SHARP 0PX",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }

                        // Shape 3: Segmented Micro-Ticks
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(8) { idx ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 8.dp, height = 12.dp)
                                        .background(if (idx < 5) Color.White else Color(0xFF2E2E2E))
                                )
                            }
                        }

                        // Shape 4: Mechanical Rectangular Switch
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(16.dp)
                                .background(Color(0xFF262626))
                                .border(1.dp, Color(0xFF444444)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(1.dp)
                                    .size(width = 18.dp, height = 14.dp)
                                    .background(Color.White)
                            )
                        }
                    }

                    // Selection Badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isNothingActive) Color.White else Color(0xFF1E1E1E))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isNothingActive) "ACTIVE ✓" else "TAP TO SELECT",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isNothingActive) Color.Black else Color(0xFFAAAAAA)
                        )
                    }
                }
            }

            // Divider Line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF333333))
            )

            // RIGHT SIDE: MATERIAL YOU (Google Dynamic Colors & Organic Rounded Shapes)
            val isMaterialYouActive = currentDesignStyle == DesignStyle.MATERIAL_YOU
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFFF3EDF7))
                    .border(
                        width = if (isMaterialYouActive) 2.dp else 0.dp,
                        color = if (isMaterialYouActive) Color(0xFF6750A4) else Color.Transparent
                    )
                    .clickable { onSelectDesignStyle(DesignStyle.MATERIAL_YOU) }
                    .padding(10.dp)
                    .testTag("setup_choose_material_you"),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6750A4))
                            )
                            Text(
                                text = "MATERIAL YOU",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1D1B20)
                            )
                        }
                        Text(
                            text = "DYNAMIC COLOR",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF49454F)
                        )
                    }

                    // Material You Shapes Showcase
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        // Shape 1: Organic 24dp Rounded Squircle Card
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color(0xFFE8DEF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6750A4))
                            )
                        }

                        // Shape 2: Google Search Pill Shape
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(26.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFD0BCFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PILL 28DP",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF381E72)
                            )
                        }

                        // Shape 3: Dynamic Palette Color Discs
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            listOf(Color(0xFF6750A4), Color(0xFF7D5260), Color(0xFF625B71), Color(0xFF386A20)).forEach { dotColor ->
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                )
                            }
                        }

                        // Shape 4: Smooth M3 Switch Silhouette
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFF6750A4)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }

                    // Selection Badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isMaterialYouActive) Color(0xFF6750A4) else Color(0xFFE8DEF8))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isMaterialYouActive) "ACTIVE ✓" else "TAP TO SELECT",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isMaterialYouActive) Color.White else Color(0xFF49454F)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (currentDesignStyle == DesignStyle.NOTHING_OS)
                "Selected: NOTHING OS — Monochrome contrast, Space Mono dot typography, mechanical toggles, and Signal Red accents."
            else
                "Selected: GOOGLE MATERIAL YOU — Soft rounded containers, Google Sans typography, dynamic wallpaper hues, and organic shapes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

// -------------------------------------------------------------------------
// STEP 1: FACTUAL ARCHITECTURE & REAL HOURLY OUTLETS
// -------------------------------------------------------------------------

@Composable
private fun StepFactArchitecture() {
    val isNothing = LocalDesignStyle.current == DesignStyle.NOTHING_OS
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(RoundedCornerShape(if (isNothing) 4.dp else 24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(if (isNothing) 4.dp else 24.dp)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedRadarGraphic()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "REAL OUTLETS CHECKED HOURLY",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "ZERO EDITORIAL ADJECTIVES // 0% BIAS",
            style = MaterialTheme.typography.labelSmall,
            color = SignalRed
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "• Real-Time Wire Ingestion: The app connects directly to international wire feeds (UN News, NPR International, Reuters, Associated Press) every 60 minutes.\n\n• Zero Sensationalism: All stories are automatically parsed to remove clickbait adjectives, hyperbole, and emotional commentary, leaving pure chronological facts.\n\n• No Algorithms or Ads: No tracking cookies, no accounts, and no AI-generated hallucinated opinions.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )
    }
}

// -------------------------------------------------------------------------
// STEP 2: TRI-SOURCE VERIFICATION & SIGNAL RED DISCREPANCIES
// -------------------------------------------------------------------------

@Composable
private fun StepTriSourceVerification() {
    val isNothing = LocalDesignStyle.current == DesignStyle.NOTHING_OS
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(RoundedCornerShape(if (isNothing) 4.dp else 24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(if (isNothing) 4.dp else 24.dp)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedTriSourceGraphic()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "3-SOURCE FACT AUDITING",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "SIGNAL RED INLINE DISCREPANCY TAGS",
            style = MaterialTheme.typography.labelSmall,
            color = SignalRed
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "• Mandatory Tri-Source Rule: Every article details at least three independent institutions (e.g. UN OCHA, WHO, ICRC, AP, Reuters) with credibility scores, timestamps, and quotes.\n\n• Contested Claims Highlighted: When casualty numbers, blast origins, or government claims disagree, Nothing News highlights the variance with a prominent Signal Red warning callout so you are never misled by premature reports.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )
    }
}

// -------------------------------------------------------------------------
// STEP 3: CONFIGURE NOTIFICATIONS (NATURAL DISASTERS, ETC.)
// -------------------------------------------------------------------------

@Composable
private fun StepNotificationPreferences(
    notificationPreferences: NotificationPreferences,
    onToggleNotification: (String) -> Unit,
    isNothing: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "CHOOSE YOUR NOTIFICATIONS",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "SELECT WHICH BREAKING DISPATCHES TRIGGER ALERTS",
            style = MaterialTheme.typography.labelSmall,
            color = SignalRed
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Option 1: Natural Disasters in Other Countries (Specifically requested!)
        NotificationRowItem(
            title = "NATURAL DISASTERS IN OTHER COUNTRIES",
            description = "Earthquakes, tsunamis, volcanic eruptions, tropical cyclones, and flash floods worldwide.",
            icon = Icons.Outlined.Tsunami,
            checked = notificationPreferences.naturalDisasters,
            onCheckedChange = { onToggleNotification("natural_disasters") },
            isNothing = isNothing,
            testTag = "setup_notif_disasters"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Option 2: Humanitarian & Conflict Wires
        NotificationRowItem(
            title = "HUMANITARIAN & CONFLICT CRISIS",
            description = "UN OCHA, UNRWA, and ICRC dispatches from Palestine, Gaza, and global conflict zones.",
            icon = Icons.Outlined.Security,
            checked = notificationPreferences.humanitarianCrisis,
            onCheckedChange = { onToggleNotification("humanitarian_crisis") },
            isNothing = isNothing,
            testTag = "setup_notif_humanitarian"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Option 3: Global Health Emergencies
        NotificationRowItem(
            title = "GLOBAL HEALTH EMERGENCIES",
            description = "WHO pathogen outbreak bulletins, quarantine zones, and biological alert levels.",
            icon = Icons.Outlined.HealthAndSafety,
            checked = notificationPreferences.healthPandemic,
            onCheckedChange = { onToggleNotification("health_pandemic") },
            isNothing = isNothing,
            testTag = "setup_notif_health"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Option 4: Science & Climate Dispatches
        NotificationRowItem(
            title = "SCIENCE & CLIMATE ANOMALIES",
            description = "ESA/NASA telemetry, magnetic storm alerts, and global temperature/ocean readings.",
            icon = Icons.Outlined.Science,
            checked = notificationPreferences.scienceClimate,
            onCheckedChange = { onToggleNotification("science_climate") },
            isNothing = isNothing,
            testTag = "setup_notif_science"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Option 5: Economic & Supply Chain Shocks
        NotificationRowItem(
            title = "TRADE & SUPPLY CHAIN DISRUPTIONS",
            description = "Global trade corridor shutdowns, vital transit choke-points, and sovereign dispatches.",
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (isNothing) 4.dp else 16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                if (checked) {
                    if (isNothing) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                } else {
                    if (isNothing) Color(0xFF262626) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                },
                RoundedCornerShape(if (isNothing) 4.dp else 16.dp)
            )
            .clickable { onCheckedChange() }
            .padding(12.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (checked) SignalRed else MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(26.dp)
                .padding(end = 6.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = if (checked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (isNothing) {
            MechanicalSwitch(
                checked = checked,
                onCheckedChange = { onCheckedChange() }
            )
        } else {
            Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

// -------------------------------------------------------------------------
// STEP 4: SETUP SUMMARY & CONFIRMATION
// -------------------------------------------------------------------------

@Composable
private fun StepSetupSummary(
    currentDesignStyle: DesignStyle,
    notificationPreferences: NotificationPreferences,
    isNothing: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Developer Avatar (Marin Claus)
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .border(2.dp, SignalRed, CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.marin_claus),
                contentDescription = "Developer Profile",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "This was coded by 1 guy and I promise Ill update the app just tell me whats wrong and ill fix it.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "ALL SYSTEM PARAMETERS CONFIGURED",
            style = MaterialTheme.typography.labelSmall,
            color = SignalRed
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Summary details card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(if (isNothing) 4.dp else 18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    1.dp,
                    if (isNothing) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    RoundedCornerShape(if (isNothing) 4.dp else 18.dp)
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "INTERFACE ENGINE:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = if (currentDesignStyle == DesignStyle.NOTHING_OS) "NOTHING OS // MONOCHROME" else "MATERIAL YOU // DYNAMIC",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DISASTER & CRISIS ALERTS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                val enabledCount = listOf(
                    notificationPreferences.naturalDisasters,
                    notificationPreferences.humanitarianCrisis,
                    notificationPreferences.healthPandemic,
                    notificationPreferences.scienceClimate,
                    notificationPreferences.economyEnergy
                ).count { it }
                Text(
                    text = "$enabledCount TOPICS ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = SignalRed
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "FACT AUDIT MANDATE:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = ">= 3 REPUTABLE SOURCES",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "REAL OUTLET CHECKS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "HOURLY // 60 MIN INTERVAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "You can re-adjust your interface style and notification subscriptions at any time from System Settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

// -------------------------------------------------------------------------
// REUSABLE ANIMATIONS
// -------------------------------------------------------------------------

@Composable
private fun AnimatedRadarGraphic() {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
        label = "radar_angle"
    )

    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.minDimension / 2.2f

        drawCircle(Color.White.copy(alpha = 0.15f), radius = maxRadius * 0.33f, center = center, style = Stroke(1.dp.toPx()))
        drawCircle(Color.White.copy(alpha = 0.2f), radius = maxRadius * 0.66f, center = center, style = Stroke(1.dp.toPx()))
        drawCircle(Color.White.copy(alpha = 0.25f), radius = maxRadius, center = center, style = Stroke(1.dp.toPx()))

        val rad = Math.toRadians(angle.toDouble())
        val endX = center.x + maxRadius * cos(rad).toFloat()
        val endY = center.y + maxRadius * sin(rad).toFloat()
        drawLine(
            color = Color.White,
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 2.dp.toPx()
        )

        drawCircle(SignalRed, radius = 4.dp.toPx(), center = center)
    }
}

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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("UN OCHA", "REUTERS", "WHO").forEach { name ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF1E1E1E))
                        .border(1.dp, Color.White.copy(alpha = pulse), RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✓ $name",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(SignalRed)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = "100% FACT CONSENSUS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

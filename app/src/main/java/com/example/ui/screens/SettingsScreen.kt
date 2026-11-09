package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.MechanicalSwitch
import com.example.ui.components.NothingCard
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.SignalRed
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

@Composable
fun SettingsScreen(
    viewModel: NewsViewModel,
    designStyle: DesignStyle,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.FEED)
    }

    val isNothing = designStyle == DesignStyle.NOTHING_OS

    Column(modifier = modifier.fillMaxSize()) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.FEED) },
                modifier = Modifier.testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = if (isNothing) "SYSTEM CONFIGURATION" else "Settings",
                style = if (isNothing) MaterialTheme.typography.labelLarge else MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visual Theme Engine Selection
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_design_style_card",
                contentPadding = 16.dp
            ) {
                Column {
                    Text(
                        text = if (isNothing) "VISUAL DESIGN ENGINE" else "Design System",
                        style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Option 1: Nothing OS Monochrome
                    val isNothingSelected = designStyle == DesignStyle.NOTHING_OS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(if (isNothing) 4.dp else 18.dp))
                            .background(
                                if (isNothingSelected) {
                                    if (isNothing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .then(
                                if (isNothing) {
                                    Modifier.border(
                                        1.dp,
                                        if (isNothingSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(4.dp)
                                    )
                                } else Modifier
                            )
                            .clickable { viewModel.setDesignStyle(DesignStyle.NOTHING_OS) }
                            .testTag("theme_option_nothing_os")
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NOTHING OS (MONOCHROME)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Stark industrial contrast, Space Mono dot typography, 1px borders, and Signal Red accents.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (isNothingSelected) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Active",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: Material You (Dynamic Wallpaper)
                    val isMaterialYouSelected = designStyle == DesignStyle.MATERIAL_YOU
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(if (isNothing) 4.dp else 18.dp))
                            .background(
                                if (isMaterialYouSelected) {
                                    if (isNothing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .then(
                                if (isNothing) {
                                    Modifier.border(
                                        1.dp,
                                        if (isMaterialYouSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(4.dp)
                                    )
                                } else Modifier
                            )
                            .clickable { viewModel.setDesignStyle(DesignStyle.MATERIAL_YOU) }
                            .testTag("theme_option_material_you")
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MATERIAL YOU (DYNAMIC COLOR)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Extracts dynamic palette from user wallpaper, with 24dp rounded cards, Google typography, and soft shadows.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (isMaterialYouSelected) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Active",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            // Dark / Light Mode Switch (Mechanical for Nothing OS, Smooth M3 Switch for Material You)
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_dark_mode_card",
                contentPadding = 16.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isNothing) "DARK THEME" else "Dark Theme",
                            style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isDarkMode) {
                                if (isNothing) "OLED Pure Black (#0D0D0D)" else "Dark dynamic surface tones"
                            } else {
                                if (isNothing) "Monochrome Light (#F5F5F5)" else "Light dynamic surface tones"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    if (isNothing) {
                        MechanicalSwitch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            testTag = "theme_dark_mode_switch"
                        )
                    } else {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            modifier = Modifier.testTag("theme_dark_mode_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            // Notification Dispatch Preferences Card
            val notificationPreferences by viewModel.notificationPreferences.collectAsStateWithLifecycle()
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_notifications_card",
                contentPadding = 16.dp
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SignalRed)
                        )
                        Text(
                            text = if (isNothing) "BREAKING DISPATCH NOTIFICATIONS" else "Notification Channels",
                            style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Configure which emergency categories trigger high-priority alerts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 1: Natural Disasters in Other Countries
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleNotification("natural_disasters") }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NATURAL DISASTERS IN OTHER COUNTRIES",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Earthquakes, tsunamis, volcanic eruptions, and typhoons worldwide.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (isNothing) {
                            MechanicalSwitch(
                                checked = notificationPreferences.naturalDisasters,
                                onCheckedChange = { viewModel.toggleNotification("natural_disasters") }
                            )
                        } else {
                            Switch(
                                checked = notificationPreferences.naturalDisasters,
                                onCheckedChange = { viewModel.toggleNotification("natural_disasters") },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 2: Humanitarian & Conflict Crisis
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleNotification("humanitarian_crisis") }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "HUMANITARIAN & CONFLICT CRISIS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "UN OCHA, UNRWA, and ICRC dispatches from Palestine & Gaza.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (isNothing) {
                            MechanicalSwitch(
                                checked = notificationPreferences.humanitarianCrisis,
                                onCheckedChange = { viewModel.toggleNotification("humanitarian_crisis") }
                            )
                        } else {
                            Switch(
                                checked = notificationPreferences.humanitarianCrisis,
                                onCheckedChange = { viewModel.toggleNotification("humanitarian_crisis") },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 3: Global Health Emergencies
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleNotification("health_pandemic") }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GLOBAL HEALTH EMERGENCIES",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "WHO pathogen outbreaks and biological emergency bulletins.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (isNothing) {
                            MechanicalSwitch(
                                checked = notificationPreferences.healthPandemic,
                                onCheckedChange = { viewModel.toggleNotification("health_pandemic") }
                            )
                        } else {
                            Switch(
                                checked = notificationPreferences.healthPandemic,
                                onCheckedChange = { viewModel.toggleNotification("health_pandemic") },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            // 3-Source Fact Checking Methodology Card
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_fact_methodology_card",
                contentPadding = 16.dp
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SignalRed)
                        )
                        Text(
                            text = if (isNothing) "3-SOURCE FACT AUDIT PROTOCOL" else "Tri-Source Verification Protocol",
                            style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Tri-Source Mandate: No report is published without independent confirmation across at least three verified institutions (UN OCHA, WHO, ICRC, AP, Reuters, ESA, or BIS).\n\n2. Discrepancy Auditing: When conflicting figures or rumors emerge, claims are flagged with prominent Signal Red callouts detailing the variance and established consensus.\n\n3. Source Registry Transparency: Every article provides direct excerpts, credibility tiers, and publication timestamps.\n\n4. Zero Editorial Adjectives: Automated sentiment and bias stripping guarantees distraction-free, 100% factual reporting.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Interactive Tutorial Replay Card
            NothingCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openTutorial() },
                testTag = "settings_tutorial_card",
                contentPadding = 16.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isNothing) "INTERACTIVE ANIMATED TUTORIAL" else "App Tutorial & Animations",
                            style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Walk through the 3-source fact-checking protocol, discrepancy alerts, and Nothing OS design language.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Text(
                        text = if (isNothing) "[REPLAY]" else "Play",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // App Version Badge (Hidden Easter Egg Trigger: Triple-tap reveals "Made By Demon")
            NothingCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onVersionTapped() },
                testTag = "version_footer_card",
                contentPadding = 14.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isNothing) "NOTHING NEWS // KERNEL v1.0.4" else "Nothing News v1.0.4",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "BUILD 2026.11 // 3-SOURCE ENGINE ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

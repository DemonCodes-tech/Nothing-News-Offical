package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AccessibilityTextScaleControl
import com.example.ui.components.BackgroundSyncControlPanel
import com.example.ui.components.CURRENT_RELEASE_VERSION
import com.example.ui.components.ContributorsOfNewsThingCard
import com.example.ui.components.DeviceNotificationAccessBanner
import com.example.ui.components.ExpressiveAnimatedShapeBadge
import com.example.ui.components.ExpressiveShapeCluster
import com.example.ui.components.ExpressiveShapeKind
import com.example.ui.components.FeedPersonalizationEditor
import com.example.ui.components.MarinPrivacyStatementCard
import com.example.ui.components.MechanicalSwitch
import com.example.ui.components.NEWSTHING_GITHUB_URL
import com.example.ui.components.NEWSTHING_RELEASE_LOG
import com.example.ui.components.NothingCard
import com.example.ui.components.marinColorFilter
import com.example.ui.components.openGitHubRepository
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalDeviceProfile
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NothingRedTintDark
import com.example.ui.theme.NothingRedTintLight
import com.example.ui.theme.ScallopedSquircleShape
import com.example.ui.theme.SignalRed
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

@Composable
fun SettingsScreen(
    viewModel: NewsViewModel,
    designStyle: DesignStyle,
    isDarkMode: Boolean,
    hasNotificationPermission: Boolean = false,
    isBatteryExempted: Boolean = false,
    onRequestNotificationPermission: () -> Unit = {},
    onRequestBatteryExemption: () -> Unit = {},
    onSendTestNotification: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.FEED)
    }

    val isNothing = designStyle == DesignStyle.NOTHING_OS
    val isDark = LocalIsDarkTheme.current
    val deviceProfile = LocalDeviceProfile.current
    val hapticEngine = LocalHapticEngine.current
    val horizontalPad = deviceProfile.contentPadding

    Column(modifier = modifier.fillMaxSize()) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPad, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    hapticEngine?.trigger(HapticCue.UI_NAV)
                    viewModel.navigateTo(AppScreen.FEED)
                },
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPad, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(deviceProfile.itemSpacing)
        ) {
            // Visual Theme Engine Selection
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_design_style_card",
                contentPadding = deviceProfile.cardPadding
            ) {
                Column {
                    Text(
                        text = if (isNothing) "VISUAL DESIGN ENGINE" else "Design System",
                        style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

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
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                viewModel.setDesignStyle(DesignStyle.NOTHING_OS)
                            }
                            .testTag("theme_option_nothing_os")
                            .padding(10.dp),
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
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (isNothingSelected) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Active",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 6.dp).size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 2: Material You (Dynamic Wallpaper)
                    val isMaterialYouSelected = designStyle == DesignStyle.MATERIAL_YOU
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(if (isNothing) 4.dp else 14.dp))
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
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                viewModel.setDesignStyle(DesignStyle.MATERIAL_YOU)
                            }
                            .testTag("theme_option_material_you")
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "MATERIAL YOU (DYNAMIC COLOR)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isMaterialYouSelected) {
                                    ExpressiveShapeCluster()
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Extracts dynamic palette from user wallpaper, with animated M3 expressive flowers, scalloped squircles, and soft elevation.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (isMaterialYouSelected) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Active",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 6.dp).size(18.dp)
                            )
                        }
                    }
                }
            }

            // Dark / Light Mode Switch (Mechanical for Nothing OS, Smooth M3 Switch for Material You)
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_dark_mode_card",
                contentPadding = deviceProfile.cardPadding
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
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
                            onCheckedChange = {
                                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                viewModel.toggleDarkMode()
                            },
                            modifier = Modifier
                                .scale(0.84f)
                                .testTag("theme_dark_mode_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            // Tactile Haptic Feedback Switch Card
            val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val turningOn = !hapticFeedbackEnabled
                    viewModel.toggleHapticFeedback()
                    if (turningOn) {
                        hapticEngine?.trigger(HapticCue.UI_TOGGLE, force = true)
                    }
                },
                testTag = "settings_haptics_card",
                contentPadding = deviceProfile.cardPadding
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
                        Text(
                            text = if (isNothing) "TACTILE HAPTICS" else "Haptic Feedback",
                            style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (hapticFeedbackEnabled) {
                                if (isNothing) {
                                    "Mechanical clicks on pulsing Signal Red dots and controls"
                                } else {
                                    "Expressive pulses on roaming shapes and controls"
                                }
                            } else {
                                "Vibration responses muted across shapes, dots, and buttons"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    if (isNothing) {
                        MechanicalSwitch(
                            checked = hapticFeedbackEnabled,
                            onCheckedChange = {
                                val turningOn = !hapticFeedbackEnabled
                                viewModel.toggleHapticFeedback()
                                if (turningOn) {
                                    hapticEngine?.trigger(HapticCue.UI_TOGGLE, force = true)
                                }
                            },
                            testTag = "settings_haptics_switch"
                        )
                    } else {
                        Switch(
                            checked = hapticFeedbackEnabled,
                            onCheckedChange = {
                                val turningOn = !hapticFeedbackEnabled
                                viewModel.toggleHapticFeedback()
                                hapticEngine?.trigger(HapticCue.UI_TOGGLE, force = turningOn)
                            },
                            modifier = Modifier
                                .scale(0.84f)
                                .testTag("settings_haptics_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            // Accessibility Text Size Slider Card (100% Standard to 150% Extra Large)
            val appFontScale by viewModel.appFontScale.collectAsStateWithLifecycle()
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_accessibility_card",
                contentPadding = deviceProfile.cardPadding
            ) {
                AccessibilityTextScaleControl(
                    appFontScale = appFontScale,
                    isNothing = isNothing,
                    compact = false,
                    showPreview = true,
                    onFontScaleChange = { viewModel.setAppFontScale(it) },
                    onResetFontScale = { viewModel.resetAppFontScale() }
                )
            }

            // Personal Feed Profile Card (Country, Political Alignment, All-Sides, Topics)
            val userFeedProfile by viewModel.userFeedProfile.collectAsStateWithLifecycle()
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_personal_feed_card",
                contentPadding = deviceProfile.cardPadding
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
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
                                    size = 16.dp,
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
                                text = if (isNothing) "MY FEED // PERSONALIZATION" else "My Feed Profile",
                                style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = if (isNothing) "[WIZARD]" else "Wizard",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    hapticEngine?.trigger(HapticCue.UI_NAV)
                                    viewModel.openTutorial(startStep = 5)
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("settings_open_personalize_wizard")
                        )
                    }

                    FeedPersonalizationEditor(
                        profile = userFeedProfile,
                        isNothing = isNothing,
                        compact = false,
                        onSelectCountry = { viewModel.setCountry(it) },
                        onSelectAlignmentMode = { viewModel.setAlignmentMode(it) },
                        onToggleParty = { viewModel.toggleParty(it) },
                        onSelectSpectrum = { viewModel.setSpectrum(it) },
                        onToggleAllSides = { viewModel.toggleAllSides() },
                        onToggleTopic = { viewModel.toggleTopic(it) },
                        onSelectAllTopics = { viewModel.selectAllTopics() },
                        onClearTopics = { viewModel.clearAllTopics() }
                    )
                }
            }

            // Notification Dispatch Preferences Card
            val notificationPreferences by viewModel.notificationPreferences.collectAsStateWithLifecycle()
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_notifications_card",
                contentPadding = deviceProfile.cardPadding
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Configure which emergency categories trigger high-priority alerts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DeviceNotificationAccessBanner(
                        hasPermission = hasNotificationPermission,
                        isNothing = isNothing,
                        onRequestPermission = onRequestNotificationPermission,
                        onSendTestNotification = onSendTestNotification
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 1: Natural Disasters in Other Countries
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                viewModel.toggleNotification("natural_disasters")
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
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
                                onCheckedChange = {
                                    hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                    viewModel.toggleNotification("natural_disasters")
                                },
                                modifier = Modifier.scale(0.84f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Row 2: Humanitarian & Conflict Crisis
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                viewModel.toggleNotification("humanitarian_crisis")
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
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
                                onCheckedChange = {
                                    hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                    viewModel.toggleNotification("humanitarian_crisis")
                                },
                                modifier = Modifier.scale(0.84f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Row 3: Global Health Emergencies
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                viewModel.toggleNotification("health_pandemic")
                            }
                            .padding(vertical = 4.dp)
                            .testTag("settings_notif_health_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
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
                                onCheckedChange = {
                                    hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                    viewModel.toggleNotification("health_pandemic")
                                },
                                modifier = Modifier.scale(0.84f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Row 4: Science & Climate
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                viewModel.toggleNotification("science_climate")
                            }
                            .padding(vertical = 4.dp)
                            .testTag("settings_notif_science_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = "SCIENCE & CLIMATE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "NASA telemetry, magnetic storms, and extreme weather bulletins.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (isNothing) {
                            MechanicalSwitch(
                                checked = notificationPreferences.scienceClimate,
                                onCheckedChange = { viewModel.toggleNotification("science_climate") }
                            )
                        } else {
                            Switch(
                                checked = notificationPreferences.scienceClimate,
                                onCheckedChange = {
                                    hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                    viewModel.toggleNotification("science_climate")
                                },
                                modifier = Modifier.scale(0.84f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Row 5: Trade & Supply Chains
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                viewModel.toggleNotification("economy_energy")
                            }
                            .padding(vertical = 4.dp)
                            .testTag("settings_notif_economy_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = "TRADE & SUPPLY CHAINS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Corridor closures and global energy transit disruptions.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (isNothing) {
                            MechanicalSwitch(
                                checked = notificationPreferences.economyEnergy,
                                onCheckedChange = { viewModel.toggleNotification("economy_energy") }
                            )
                        } else {
                            Switch(
                                checked = notificationPreferences.economyEnergy,
                                onCheckedChange = {
                                    hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                                    viewModel.toggleNotification("economy_energy")
                                },
                                modifier = Modifier.scale(0.84f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            // Background Execution & Live Sync Permissions Card
            val backgroundSyncPreferences by viewModel.backgroundSyncPreferences.collectAsStateWithLifecycle()
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_background_sync_card",
                contentPadding = deviceProfile.cardPadding
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                kind = ExpressiveShapeKind.SCALLOPED_SQUIRCLE,
                                size = 16.dp,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onTertiaryContainer)
                                )
                            }
                        }
                        Text(
                            text = if (isNothing) "BACKGROUND ENGINE & PERMISSIONS" else "Background Updates & Sync",
                            style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    BackgroundSyncControlPanel(
                        syncPrefs = backgroundSyncPreferences,
                        hasNotificationPermission = hasNotificationPermission,
                        isBatteryExempted = isBatteryExempted,
                        isNothing = isNothing,
                        compact = false,
                        onToggleWorkManager = { viewModel.toggleWorkManagerSync() },
                        onToggleLiveService = { viewModel.toggleLiveSyncService() },
                        onRequestNotificationPermission = onRequestNotificationPermission,
                        onRequestBatteryExemption = onRequestBatteryExemption
                    )
                }
            }

            // Marin Zero-Data Privacy Guarantee Card
            MarinPrivacyStatementCard(isNothing = isNothing)

            // 3-Source Fact Checking Methodology Card
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_fact_methodology_card",
                contentPadding = deviceProfile.cardPadding
            ) {
                Column {
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
                                kind = ExpressiveShapeKind.CLOVER,
                                size = 16.dp,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                )
                            }
                        }
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
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.openTutorial() },
                testTag = "settings_tutorial_card",
                contentPadding = deviceProfile.cardPadding
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

            // Update Log & GitHub Updates Card (with Marin keeping watch)
            val context = LocalContext.current
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_update_log_card",
                contentPadding = deviceProfile.cardPadding
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f).padding(end = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(SignalRed)
                            )
                            Text(
                                text = if (isNothing) "UPDATE LOG & RELEASES" else "Update Log",
                                style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(if (isNothing) 2.dp else 999.dp))
                                .background(if (isNothing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LATEST $CURRENT_RELEASE_VERSION",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isNothing) SignalRed else MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Marin Portrait Watch Strip (B&W in Nothing OS, Full Color in Material You)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(if (isNothing) 4.dp else 14.dp),
                        color = if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = BorderStroke(
                            1.dp,
                            if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isNothing) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline,
                                            RoundedCornerShape(4.dp)
                                        ),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.marin_update_watch),
                                        contentDescription = "Marin Kitagawa Update Watch",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = 2.dp)
                                            .testTag("settings_update_log_marin_image"),
                                        contentScale = ContentScale.Fit,
                                        alignment = Alignment.BottomCenter,
                                        colorFilter = marinColorFilter(true)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier.size(68.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ExpressiveAnimatedShapeBadge(
                                        kind = ExpressiveShapeKind.ORGANIC_FLOWER,
                                        size = 68.dp,
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f),
                                        rotationDurationMs = 16000
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(58.dp)
                                            .clip(ScallopedSquircleShape(scallops = 10, scallopDepth = 0.08f))
                                            .background(MaterialTheme.colorScheme.surface),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.marin_update_watch),
                                            contentDescription = "Marin Kitagawa Update Watch",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(top = 2.dp)
                                                .testTag("settings_update_log_marin_image"),
                                            contentScale = ContentScale.Fit,
                                            alignment = Alignment.BottomCenter,
                                            colorFilter = marinColorFilter(false)
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = if (isNothing) "MARIN // UPDATE WATCH" else "Marin's Update Watch",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.marin_update_watch_note),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Chronological Release History Entries
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        NEWSTHING_RELEASE_LOG.forEach { release ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(if (isNothing) 4.dp else 12.dp))
                                    .background(if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface)
                                    .border(
                                        1.dp,
                                        if (release.isCurrent && isNothing) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                                        } else if (isNothing) {
                                            MaterialTheme.colorScheme.outline
                                        } else {
                                            MaterialTheme.colorScheme.outlineVariant
                                        },
                                        RoundedCornerShape(if (isNothing) 4.dp else 12.dp)
                                    )
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${release.version} • ${release.title}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (release.isCurrent) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(if (isNothing) 2.dp else 999.dp))
                                                .background(SignalRed)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "CURRENT",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = release.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )

                                if (release.oldHighlights.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(if (isNothing) 3.dp else 10.dp))
                                            .background(if (isNothing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceContainerLow)
                                            .border(
                                                1.dp,
                                                if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                                                RoundedCornerShape(if (isNothing) 3.dp else 10.dp)
                                            )
                                            .padding(8.dp)
                                            .testTag("release_old_nothing_news_section"),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = if (isNothing) "WHAT WAS OLD // NOTHING NEWS ERA" else "What Was Old (Nothing News Era)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        )
                                        release.oldHighlights.forEach { bullet ->
                                            Text(
                                                text = "• $bullet",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontSize = 10.5.sp,
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }
                                }

                                if (release.newHighlights.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(if (isNothing) 3.dp else 10.dp))
                                            .background(
                                                if (isNothing) {
                                                    if (isDark) NothingRedTintDark else NothingRedTintLight
                                                } else {
                                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                if (isNothing) SignalRed.copy(alpha = 0.55f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                                RoundedCornerShape(if (isNothing) 3.dp else 10.dp)
                                            )
                                            .padding(8.dp)
                                            .testTag("release_new_newsthing_section"),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = if (isNothing) "WHAT'S NEW // SINCE NOTHING NEWS" else "What Came Out New (NewsThing Evolution)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        )
                                        release.newHighlights.forEach { bullet ->
                                            Text(
                                                text = "• $bullet",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 10.5.sp,
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // GitHub Repo Action Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .clip(RoundedCornerShape(if (isNothing) 4.dp else 16.dp))
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TAP)
                                openGitHubRepository(context)
                            }
                            .testTag("settings_github_repo_button"),
                        shape = RoundedCornerShape(if (isNothing) 4.dp else 16.dp),
                        color = if (isNothing) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.secondaryContainer,
                        border = BorderStroke(
                            1.dp,
                            if (isNothing) SignalRed.copy(alpha = 0.75f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Code,
                                    contentDescription = "GitHub Repository",
                                    tint = if (isNothing) SignalRed else MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isNothing) "OPEN GITHUB REPO // UPDATES" else "Open GitHub Repository",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isNothing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = NEWSTHING_GITHUB_URL,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isNothing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                contentDescription = "Open GitHub link",
                                tint = if (isNothing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Contributors of NewsThing ("The Name Giver": Fall_Spezialgelagert on Reddit + Marin "Thanks Pookies!")
            ContributorsOfNewsThingCard(
                isNothing = isNothing,
                contentPadding = deviceProfile.cardPadding
            )

            // App Version Badge (Hidden Easter Egg Trigger: Triple-tap reveals "Made By Demon")
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.onVersionTapped() },
                testTag = "version_footer_card",
                contentPadding = deviceProfile.cardPadding
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isNothing) "NEWSTHING // KERNEL $CURRENT_RELEASE_VERSION" else "NewsThing $CURRENT_RELEASE_VERSION",
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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

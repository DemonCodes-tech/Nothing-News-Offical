package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AlignmentMode
import com.example.data.model.BackgroundSyncPreferences
import com.example.data.model.PersonalizationCatalog
import com.example.data.model.UserFeedProfile
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NothingGreenBorderDark
import com.example.ui.theme.NothingGreenBorderLight
import com.example.ui.theme.NothingGreenTintDark
import com.example.ui.theme.NothingGreenTintLight
import com.example.ui.theme.NothingRedTintDark
import com.example.ui.theme.NothingRedTintLight
import com.example.ui.theme.SignalRed
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedPersonalizationEditor(
    profile: UserFeedProfile,
    isNothing: Boolean,
    compact: Boolean = false,
    onSelectCountry: (String) -> Unit,
    onSelectAlignmentMode: (AlignmentMode) -> Unit,
    onToggleParty: (String) -> Unit,
    onSelectSpectrum: (String) -> Unit,
    onToggleAllSides: () -> Unit,
    onToggleTopic: (String) -> Unit,
    onSelectAllTopics: (() -> Unit)? = null,
    onClearTopics: (() -> Unit)? = null,
    showTopics: Boolean = true,
    modifier: Modifier = Modifier
) {
    val activeCountry = PersonalizationCatalog.findCountry(profile.countryCode)
    val hapticEngine = LocalHapticEngine.current
    val labelSize = if (compact) 9.5.sp else 10.5.sp
    val chipVert = if (compact) 5.dp else 7.dp
    val chipHoriz = if (compact) 8.dp else 10.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 10.dp)
    ) {
        // 1. Country Selector
        Text(
            text = if (isNothing) "YOUR COUNTRY // ${activeCountry.name.uppercase()}" else "Your Country: ${activeCountry.name}",
            style = MaterialTheme.typography.labelSmall,
            color = SignalRed,
            fontWeight = FontWeight.Bold,
            fontSize = labelSize
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            PersonalizationCatalog.countries.forEach { country ->
                val selected = country.code.equals(profile.countryCode, ignoreCase = true)
                SelectablePillChip(
                    text = "${country.code} • ${country.name}",
                    selected = selected,
                    isNothing = isNothing,
                    compact = compact,
                    vertPad = chipVert,
                    horizPad = chipHoriz,
                    testTag = "country_chip_${country.code.lowercase()}",
                    onClick = { onSelectCountry(country.code) }
                )
            }
        }

        // 2. Political Alignment Mode
        Text(
            text = if (isNothing) "POLITICAL LENS" else "Political Alignment",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = labelSize
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            listOf(
                AlignmentMode.PARTIES to if (isNothing) "PARTIES" else "Parties",
                AlignmentMode.SPECTRUM to if (isNothing) "SPECTRUM" else "Spectrum",
                AlignmentMode.NON_PARTISAN to if (isNothing) "NON-PARTISAN" else "Non-Partisan"
            ).forEach { (mode, title) ->
                val selected = profile.alignmentMode == mode
                SelectablePillChip(
                    text = title,
                    selected = selected,
                    isNothing = isNothing,
                    compact = compact,
                    vertPad = chipVert,
                    horizPad = chipHoriz,
                    testTag = "alignment_mode_${mode.name.lowercase()}",
                    onClick = { onSelectAlignmentMode(mode) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Mode-specific choices
        when (profile.alignmentMode) {
            AlignmentMode.PARTIES -> {
                Text(
                    text = "${activeCountry.name} parties (multi-select):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = if (compact) 9.sp else 10.sp
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    activeCountry.parties.forEach { party ->
                        val selected = profile.selectedParties.contains(party)
                        SelectablePillChip(
                            text = if (selected) "✓ $party" else party,
                            selected = selected,
                            isNothing = isNothing,
                            compact = compact,
                            vertPad = chipVert,
                            horizPad = chipHoriz,
                            testTag = "party_chip_${party.lowercase().replace(" ", "_")}",
                            onClick = { onToggleParty(party) }
                        )
                    }
                }
            }
            AlignmentMode.SPECTRUM -> {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    PersonalizationCatalog.spectrumOptions.forEach { spec ->
                        val selected = profile.selectedSpectrum.equals(spec.id, ignoreCase = true)
                        SelectablePillChip(
                            text = spec.label,
                            selected = selected,
                            isNothing = isNothing,
                            compact = compact,
                            vertPad = chipVert,
                            horizPad = chipHoriz,
                            testTag = "spectrum_chip_${spec.id.lowercase()}",
                            onClick = { onSelectSpectrum(spec.id) }
                        )
                    }
                }
            }
            AlignmentMode.NON_PARTISAN -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(if (isNothing) 4.dp else 10.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    border = BorderStroke(
                        1.dp,
                        if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Text(
                        text = "Prioritizing primary records, neutral wire bureaus, and non-partisan factual audits.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = if (compact) 9.sp else 10.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // 3. All-Sides Viewpoint Balance Switch
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(if (isNothing) 4.dp else 12.dp))
                .clickable {
                    hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                    onToggleAllSides()
                }
                .testTag("all_sides_toggle_row"),
            shape = RoundedCornerShape(if (isNothing) 4.dp else 12.dp),
            color = if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                if (profile.allSidesEnabled) {
                    if (isNothing) SignalRed.copy(alpha = 0.75f) else MaterialTheme.colorScheme.primary
                } else {
                    if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                    Text(
                        text = if (isNothing) "ALL-SIDES MODE" else "All-Sides Viewpoints",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = labelSize
                    )
                    Text(
                        text = "Include contrasting viewpoints alongside your alignment so nothing stays hidden.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = if (compact) 8.5.sp else 9.5.sp,
                        lineHeight = if (compact) 11.sp else 13.sp
                    )
                }

                if (isNothing) {
                    MechanicalSwitch(
                        checked = profile.allSidesEnabled,
                        onCheckedChange = { onToggleAllSides() }
                    )
                } else {
                    Switch(
                        checked = profile.allSidesEnabled,
                        onCheckedChange = {
                            hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                            onToggleAllSides()
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

        // 4. News Categories You Want to See
        if (showTopics) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isNothing) {
                        "NEWS CATEGORIES (${profile.selectedTopics.size} ACTIVE)"
                    } else {
                        "News Categories (${profile.selectedTopics.size} active)"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = labelSize,
                    modifier = Modifier.weight(1f)
                )
                if (onSelectAllTopics != null && onClearTopics != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isNothing) "[ALL]" else "Select All",
                            style = MaterialTheme.typography.labelSmall,
                            color = SignalRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    hapticEngine?.trigger(HapticCue.UI_TAP)
                                    onSelectAllTopics()
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("topics_select_all_button")
                        )
                        Text(
                            text = if (isNothing) "[CLEAR]" else "Clear",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clickable {
                                    hapticEngine?.trigger(HapticCue.UI_TAP)
                                    onClearTopics()
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("topics_clear_all_button")
                        )
                    }
                }
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                PersonalizationCatalog.topicOptions.forEach { topic ->
                    val selected = profile.selectedTopics.contains(topic.id)
                    SelectablePillChip(
                        text = if (selected) "✓ ${topic.label}" else topic.label,
                        selected = selected,
                        isNothing = isNothing,
                        compact = compact,
                        vertPad = chipVert,
                        horizPad = chipHoriz,
                        testTag = "topic_chip_${topic.id.lowercase()}",
                        onClick = { onToggleTopic(topic.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepChooseNewsCategories(
    profile: UserFeedProfile,
    isNothing: Boolean,
    onToggleTopic: (String) -> Unit,
    onSelectAllTopics: () -> Unit,
    onClearTopics: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header Strip with Select All / Clear
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isNothing) "CHOOSE NEWS GENRES" else "Choose Your News Genres",
                    style = MaterialTheme.typography.labelMedium,
                    color = SignalRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = "${profile.selectedTopics.size} of ${PersonalizationCatalog.topicOptions.size} active in My Feed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 9.5.sp
                )
            }

            val hapticEngine = LocalHapticEngine.current
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isNothing) "[SELECT ALL]" else "Select All",
                    style = MaterialTheme.typography.labelSmall,
                    color = SignalRed,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            hapticEngine?.trigger(HapticCue.UI_TAP)
                            onSelectAllTopics()
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("step_categories_select_all")
                )
                Text(
                    text = if (isNothing) "[CLEAR]" else "Clear",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .clickable {
                            hapticEngine?.trigger(HapticCue.UI_TAP)
                            onClearTopics()
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("step_categories_clear")
                )
            }
        }

        // Category Chips Flow
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PersonalizationCatalog.topicOptions.forEach { topic ->
                val selected = profile.selectedTopics.contains(topic.id)
                SelectablePillChip(
                    text = if (selected) "✓ ${topic.label}" else topic.label,
                    selected = selected,
                    isNothing = isNothing,
                    compact = false,
                    vertPad = 7.dp,
                    horizPad = 10.dp,
                    testTag = "step_category_chip_${topic.id.lowercase()}",
                    onClick = { onToggleTopic(topic.id) }
                )
            }
        }

        // Informational Note Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(if (isNothing) 4.dp else 12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(
                1.dp,
                if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Text(
                text = "My Feed prioritizes these categories while keeping contrasting political angles visible under All-Sides. The Planet World tab always shows every wire globally.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 9.5.sp,
                lineHeight = 13.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsCategoryPickerDialog(
    selectedTopics: Set<String>,
    isNothing: Boolean,
    onToggleTopic: (String) -> Unit,
    onSelectAllTopics: () -> Unit,
    onClearTopics: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("category_picker_dialog"),
            shape = RoundedCornerShape(if (isNothing) 4.dp else 20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = if (isNothing) 0.dp else 6.dp,
            border = BorderStroke(
                1.dp,
                if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isNothing) "CHOOSE NEWS CATEGORIES" else "Choose News Categories",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedTopics.size} active in My Feed (Sports, Tech, Cars, AI & more)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    val hapticEngine = LocalHapticEngine.current
                    IconButton(
                        onClick = {
                            hapticEngine?.trigger(HapticCue.UI_TAP)
                            onDismiss()
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("category_picker_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close category picker",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PersonalizationCatalog.topicOptions.forEach { topic ->
                        val selected = selectedTopics.contains(topic.id)
                        SelectablePillChip(
                            text = if (selected) "✓ ${topic.label}" else topic.label,
                            selected = selected,
                            isNothing = isNothing,
                            compact = false,
                            vertPad = 8.dp,
                            horizPad = 10.dp,
                            testTag = "dialog_topic_chip_${topic.id.lowercase()}",
                            onClick = { onToggleTopic(topic.id) }
                        )
                    }
                }

                val dialogHaptic = LocalHapticEngine.current
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (isNothing) "[SELECT ALL]" else "Select All",
                            style = MaterialTheme.typography.labelMedium,
                            color = SignalRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    dialogHaptic?.trigger(HapticCue.UI_TAP)
                                    onSelectAllTopics()
                                }
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                                .testTag("dialog_topics_select_all")
                        )
                        Text(
                            text = if (isNothing) "[CLEAR]" else "Clear",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clickable {
                                    dialogHaptic?.trigger(HapticCue.UI_TAP)
                                    onClearTopics()
                                }
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                                .testTag("dialog_topics_clear")
                        )
                    }

                    Button(
                        onClick = {
                            dialogHaptic?.trigger(HapticCue.UI_TAP)
                            onDismiss()
                        },
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("category_picker_done_button"),
                        shape = RoundedCornerShape(if (isNothing) 4.dp else 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = if (isNothing) "APPLY CATEGORIES" else "Done",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BackgroundSyncControlPanel(
    syncPrefs: BackgroundSyncPreferences,
    hasNotificationPermission: Boolean,
    isBatteryExempted: Boolean,
    isNothing: Boolean,
    compact: Boolean = false,
    onToggleWorkManager: () -> Unit,
    onToggleLiveService: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestBatteryExemption: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val hapticEngine = LocalHapticEngine.current
    val nothingGreenBg = if (isDark) NothingGreenTintDark else NothingGreenTintLight
    val nothingGreenBorder = if (isDark) NothingGreenBorderDark else NothingGreenBorderLight
    val nothingRedBg = if (isDark) NothingRedTintDark else NothingRedTintLight

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Battery Exemption Prompt Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("battery_exemption_banner"),
            shape = RoundedCornerShape(if (isNothing) 4.dp else 12.dp),
            color = if (isBatteryExempted) {
                if (isNothing) nothingGreenBg else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            } else {
                if (isNothing) nothingRedBg else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
            },
            border = BorderStroke(
                1.dp,
                if (isBatteryExempted) {
                    if (isNothing) nothingGreenBorder else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                } else {
                    if (isNothing) SignalRed.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.BatteryChargingFull,
                    contentDescription = "Background battery exemption status",
                    tint = if (isBatteryExempted) MaterialTheme.colorScheme.primary else SignalRed,
                    modifier = Modifier.size(18.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBatteryExempted) {
                            if (isNothing) "BACKGROUND ACCESS // UNRESTRICTED" else "Background Run Allowed"
                        } else {
                            if (isNothing) "ALLOW BACKGROUND RUNNING" else "Allow Background Execution"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isBatteryExempted) MaterialTheme.colorScheme.onSurface else SignalRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (compact) 9.5.sp else 10.sp
                    )
                    Text(
                        text = if (isBatteryExempted) {
                            "Android won't freeze NewsThing's live background wire checks."
                        } else {
                            "Grant unrestricted background battery access so dispatches stay live."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = if (compact) 8.5.sp else 9.5.sp,
                        lineHeight = if (compact) 11.sp else 13.sp
                    )
                }

                Button(
                    onClick = {
                        hapticEngine?.trigger(HapticCue.UI_TAP)
                        onRequestBatteryExemption()
                    },
                    enabled = !isBatteryExempted,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 36.dp)
                        .testTag("grant_background_permission_button"),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(if (isNothing) 2.dp else 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBatteryExempted) MaterialTheme.colorScheme.surfaceVariant else SignalRed,
                        contentColor = if (isBatteryExempted) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = if (isBatteryExempted) "OK ✓" else if (isNothing) "ALLOW BG" else "Allow",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        // Notification Permission Row (compact status)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(if (isNothing) 4.dp else 12.dp),
            color = if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                if (hasNotificationPermission) {
                    if (isNothing) nothingGreenBorder else MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                } else {
                    SignalRed.copy(alpha = 0.75f)
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (hasNotificationPermission) Icons.Outlined.NotificationsActive else Icons.Outlined.NotificationsOff,
                    contentDescription = "Notification permission",
                    tint = if (hasNotificationPermission) MaterialTheme.colorScheme.primary else SignalRed,
                    modifier = Modifier.size(16.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (hasNotificationPermission) "NOTIFICATION ACCESS GRANTED" else "ALLOW NOTIFICATIONS FOR SYNC",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (compact) 9.sp else 9.5.sp
                    )
                    Text(
                        text = "Required to post live updates & foreground sync indicator.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = if (compact) 8.5.sp else 9.sp
                    )
                }
                if (!hasNotificationPermission) {
                    Button(
                        onClick = {
                            hapticEngine?.trigger(HapticCue.UI_TAP)
                            onRequestNotificationPermission()
                        },
                        modifier = Modifier
                            .defaultMinSize(minHeight = 34.dp)
                            .testTag("bg_panel_allow_notif_button"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(if (isNothing) 2.dp else 14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SignalRed,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (isNothing) "ALLOW" else "Allow",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Granted",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Toggle 1: WorkManager Periodic Sync
        BackgroundToggleRow(
            title = if (isNothing) "WORKMANAGER PERIODIC SYNC" else "Periodic Background Updates",
            description = "Battery-friendly WorkManager refresh every 30 minutes when online.",
            checked = syncPrefs.workManagerEnabled,
            isNothing = isNothing,
            compact = compact,
            testTag = "toggle_workmanager_sync",
            onClick = onToggleWorkManager
        )

        // Toggle 2: Visible Live Foreground Sync Service
        BackgroundToggleRow(
            title = if (isNothing) "LIVE BACKGROUND NOTIFICATION SERVICE" else "Live Sync Service Notification",
            description = "Keeps an ongoing data-sync notification running continuously in the background.",
            checked = syncPrefs.liveServiceEnabled,
            isNothing = isNothing,
            compact = compact,
            testTag = "toggle_live_sync_service",
            onClick = onToggleLiveService
        )
    }
}

@Composable
private fun BackgroundToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    isNothing: Boolean,
    compact: Boolean,
    testTag: String,
    onClick: () -> Unit
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
                    if (isNothing) MaterialTheme.colorScheme.primary.copy(alpha = 0.65f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                } else {
                    if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                },
                RoundedCornerShape(if (isNothing) 4.dp else 12.dp)
            )
            .clickable {
                hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = if (compact) 5.dp else 7.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Sync,
            contentDescription = title,
            tint = if (checked) SignalRed else MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(16.dp)
                .padding(end = 3.dp)
        )

        Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 9.sp else 9.5.sp
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = if (compact) 8.5.sp else 9.sp,
                lineHeight = if (compact) 11.sp else 12.5.sp
            )
        }

        if (isNothing) {
            MechanicalSwitch(
                checked = checked,
                onCheckedChange = { onClick() }
            )
        } else {
            Switch(
                checked = checked,
                onCheckedChange = {
                    hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                    onClick()
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

@Composable
private fun SelectablePillChip(
    text: String,
    selected: Boolean,
    isNothing: Boolean,
    compact: Boolean,
    vertPad: androidx.compose.ui.unit.Dp,
    horizPad: androidx.compose.ui.unit.Dp,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(if (isNothing) 2.dp else 12.dp)
    val hapticEngine = LocalHapticEngine.current
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (selected) {
                    if (isNothing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                } else {
                    if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface
                }
            )
            .border(
                1.dp,
                if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                },
                shape
            )
            .clickable {
                hapticEngine?.trigger(HapticCue.UI_TAP)
                onClick()
            }
            .padding(horizontal = horizPad, vertical = vertPad)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) {
                if (isNothing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (compact) 9.sp else 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccessibilityTextScaleControl(
    appFontScale: Float,
    isNothing: Boolean,
    compact: Boolean = false,
    showPreview: Boolean = true,
    onFontScaleChange: (Float) -> Unit,
    onResetFontScale: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percent = (appFontScale * 100f).roundToInt().coerceIn(100, 150)
    val hapticEngine = LocalHapticEngine.current
    val lastStepBucket = remember {
        mutableIntStateOf(((appFontScale.coerceIn(1.0f, 1.5f) - 1.0f) / 0.05f).roundToInt())
    }
    val scaleLabel = when {
        percent >= 140 -> if (isNothing) "EXTRA LARGE" else "Extra Large"
        percent >= 120 -> if (isNothing) "LARGE" else "Large"
        percent >= 108 -> if (isNothing) "MEDIUM" else "Medium"
        else -> if (isNothing) "STANDARD" else "Standard"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 8.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FormatSize,
                    contentDescription = "Accessibility text size",
                    tint = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isNothing) "TEXT SIZE // ACCESSIBILITY" else "Text Size",
                    style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (percent > 100) {
                    Row(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(if (isNothing) 2.dp else 999.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(if (isNothing) 2.dp else 999.dp)
                            )
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TAP)
                                lastStepBucket.intValue = 0
                                onResetFontScale()
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("accessibility_text_size_reset"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RestartAlt,
                            contentDescription = "Reset text size to 100%",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (isNothing) "RESET" else "Reset",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 9.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(if (isNothing) 2.dp else 999.dp))
                        .background(
                            if (isNothing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("accessibility_text_size_badge")
                ) {
                    Text(
                        text = "$percent% • $scaleLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNothing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        Text(
            text = "Scale headlines, wire summaries, tabs, and setup text smoothly up to 150%.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = if (compact) 9.sp else 10.sp
        )

        Slider(
            value = appFontScale.coerceIn(1.0f, 1.5f),
            onValueChange = { newValue ->
                val bucket = ((newValue.coerceIn(1.0f, 1.5f) - 1.0f) / 0.05f).roundToInt()
                if (bucket != lastStepBucket.intValue) {
                    lastStepBucket.intValue = bucket
                    hapticEngine?.trigger(HapticCue.UI_TAP)
                }
                onFontScaleChange(newValue)
            },
            valueRange = 1.0f..1.5f,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("accessibility_text_size_slider"),
            colors = SliderDefaults.colors(
                thumbColor = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                activeTrackColor = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "100% Standard",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 9.sp
            )
            Text(
                text = "125% Large",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 9.sp
            )
            Text(
                text = "150% Max",
                style = MaterialTheme.typography.labelSmall,
                color = if (percent >= 145) SignalRed else MaterialTheme.colorScheme.secondary,
                fontWeight = if (percent >= 145) FontWeight.Bold else FontWeight.Normal,
                fontSize = 9.sp
            )
        }

        if (showPreview) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("accessibility_text_size_preview"),
                shape = RoundedCornerShape(if (isNothing) 4.dp else 12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(
                    1.dp,
                    if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = if (isNothing) "LIVE READABILITY PREVIEW // $percent%" else "Live Readability Preview • $percent%",
                        style = MaterialTheme.typography.labelSmall,
                        color = SignalRed,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "3-Source Verified Dispatch Title Scales Here",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Slide left or right to make all words across NewsThing larger and easier to read.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

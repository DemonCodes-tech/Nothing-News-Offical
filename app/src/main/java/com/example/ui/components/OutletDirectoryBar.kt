package com.example.ui.components

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.core.view.WindowCompat
import com.example.data.model.BroadcasterRegistry
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.LocalDeviceProfile
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.SignalRed

@Composable
fun OutletDirectoryBar(
    selectedOutlet: String?,
    onSelectOutlet: (String?) -> Unit,
    favoriteBroadcasters: Set<String> = emptySet(),
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    val hapticEngine = LocalHapticEngine.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS
    var showFullDirectory by remember { mutableStateOf(false) }

    val featuredOutlets = remember(favoriteBroadcasters) {
        val baseList = listOf(
            Triple("ALL", "ALL CHANNELS", 0xFF666666),
            Triple("CNN", "CNN International", 0xFFCC0000),
            Triple("BBC", "BBC News", 0xFFBB1919),
            Triple("SKY", "Sky News", 0xFFFF0000),
            Triple("AJE", "Al Jazeera English", 0xFFE25A00),
            Triple("DW", "Deutsche Welle", 0xFF002D5A),
            Triple("F24", "France 24", 0xFF0080C9),
            Triple("REUTERS", "Reuters", 0xFFFF8000),
            Triple("BLOOM", "Bloomberg", 0xFF1853FF),
            Triple("CNBC", "CNBC", 0xFF002F6C),
            Triple("NHK", "NHK World", 0xFF0072BC),
            Triple("CNA", "Channel NewsAsia", 0xFFC8102E),
            Triple("CBC", "CBC News", 0xFFE0001B),
            Triple("NDTV", "NDTV 24x7", 0xFFED1C24),
            Triple("TRT", "TRT World", 0xFF0072BC),
            Triple("UN", "UN Global Wire", 0xFF009EDB)
        )
        if (favoriteBroadcasters.isEmpty()) {
            baseList
        } else {
            val allObj = baseList.first()
            val rest = baseList.drop(1).toMutableList()
            val favShortCodes = favoriteBroadcasters.mapNotNull { favKey ->
                BroadcasterRegistry.findBroadcaster(favKey)?.shortCode?.uppercase() ?: favKey.uppercase()
            }.toSet()

            // Add any pinned broadcasters not already in baseList
            favoriteBroadcasters.forEach { favKey ->
                val b = BroadcasterRegistry.findBroadcaster(favKey)
                if (b != null && rest.none { it.first.equals(b.shortCode, ignoreCase = true) }) {
                    rest.add(Triple(b.shortCode, b.name, b.badgeColorHex))
                }
            }

            val favs = rest.filter { favShortCodes.contains(it.first.uppercase()) || favoriteBroadcasters.contains(it.second.lowercase()) }
            val nonFavs = rest.filterNot { favShortCodes.contains(it.first.uppercase()) || favoriteBroadcasters.contains(it.second.lowercase()) }
            listOf(allObj) + favs + nonFavs
        }
    }

    val horizontalPad = LocalDeviceProfile.current.contentPadding

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPad, vertical = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(SignalRed)
                )
                Text(
                    text = if (isNothing) "GLOBAL OUTLETS // 100+" else "Global Broadcasters",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = if (isNothing) "[A-Z DIRECTORY]" else "View A-Z",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        hapticEngine?.trigger(HapticCue.UI_TAP)
                        showFullDirectory = true
                    }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .testTag("open_broadcaster_directory")
            )
        }

        // Horizontal Carousel of Channel Logos & Badges
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = horizontalPad, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            featuredOutlets.forEach { (code, name, brandColorHex) ->
                val isSelected = if (code == "ALL") selectedOutlet == null else selectedOutlet.equals(code, ignoreCase = true)

                ChannelLogoBadge(
                    code = code,
                    name = name,
                    brandColor = Color(brandColorHex),
                    isSelected = isSelected,
                    isNothing = isNothing,
                    onClick = {
                        if (code == "ALL") onSelectOutlet(null)
                        else onSelectOutlet(code)
                    }
                )
            }
        }
    }

    if (showFullDirectory) {
        FullBroadcasterDirectoryDialog(
            selectedOutlet = selectedOutlet,
            onSelectOutlet = {
                onSelectOutlet(it)
                showFullDirectory = false
            },
            onDismiss = { showFullDirectory = false }
        )
    }
}

@Composable
fun ChannelLogoBadge(
    code: String,
    name: String,
    brandColor: Color,
    isSelected: Boolean,
    isNothing: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(if (isNothing) 2.dp else 16.dp)
    val hapticEngine = LocalHapticEngine.current

    Row(
        modifier = Modifier
            .clip(shape)
            .background(
                if (isSelected) {
                    if (isNothing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .then(
                if (isNothing) {
                    Modifier.border(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape
                    )
                } else {
                    Modifier.border(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        shape
                    )
                }
            )
            .clickable {
                hapticEngine?.trigger(HapticCue.UI_TAP)
                onClick()
            }
            .padding(horizontal = 6.dp, vertical = 3.dp)
            .testTag("channel_badge_$code"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Channel Emblem
        if (code == "ALL") {
            Box(
                modifier = Modifier
                    .size(width = 22.dp, height = 18.dp)
                    .clip(RoundedCornerShape(if (isNothing) 1.dp else 4.dp))
                    .background(if (isNothing) Color(0xFF222222) else brandColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ALL",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        } else {
            NewsChannelLogoBadge(
                channelId = code,
                shortCode = code
            )
        }

        Text(
            text = code,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) {
                if (isNothing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
fun FullBroadcasterDirectoryDialog(
    selectedOutlet: String?,
    onSelectOutlet: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    val designStyle = LocalDesignStyle.current
    val hapticEngine = LocalHapticEngine.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("ALL OUTLETS") }
    val focusManager = LocalFocusManager.current

    val filteredBroadcasters = remember(searchQuery, selectedRegion) {
        BroadcasterRegistry.allBroadcasters.filter { b ->
            val matchesSearch = searchQuery.isBlank() ||
                    b.name.contains(searchQuery, ignoreCase = true) ||
                    b.shortCode.contains(searchQuery, ignoreCase = true) ||
                    b.country.contains(searchQuery, ignoreCase = true)

            val matchesRegion = selectedRegion == "ALL OUTLETS" || b.region.equals(selectedRegion, ignoreCase = true)

            matchesSearch && matchesRegion
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect {
            dialogWindow?.let { window ->
                WindowCompat.setDecorFitsSystemWindows(window, false)
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                window.setDimAmount(0.65f)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .clip(RoundedCornerShape(if (isNothing) 4.dp else 18.dp))
                    .border(
                        1.dp,
                        if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(if (isNothing) 4.dp else 18.dp)
                    ),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isNothing) "A-Z BROADCASTER DIRECTORY" else "Global News Networks",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${BroadcasterRegistry.allBroadcasters.size} VERIFIED GLOBAL CHANNELS",
                            style = MaterialTheme.typography.labelSmall,
                            color = SignalRed,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isNothing) "SEARCH CNN, BBC, SKY..." else "Search news channels...",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.testTag("directory_search_clear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp),
                    shape = RoundedCornerShape(if (isNothing) 2.dp else 16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Region Filter Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BroadcasterRegistry.regions.forEach { region ->
                        val isSelected = selectedRegion == region
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(if (isNothing) 2.dp else 12.dp))
                                .background(
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .clickable {
                                    hapticEngine?.trigger(HapticCue.UI_TAP)
                                    selectedRegion = region
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = region,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // All Broadcasters List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        // Quick "Show All Feeds" option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(if (isNothing) 2.dp else 14.dp))
                                .background(
                                    if (selectedOutlet == null) {
                                        if (isNothing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
                                .clickable {
                                    hapticEngine?.trigger(HapticCue.UI_TAP)
                                    onSelectOutlet(null)
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isNothing) "COMBINED GLOBAL WIRE" else "All Broadcasters Combined",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Ingest dispatches from all 100+ global channels",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            if (selectedOutlet == null) {
                                Text(text = "ACTIVE", style = MaterialTheme.typography.labelSmall, color = SignalRed)
                            }
                        }
                    }

                    items(filteredBroadcasters, key = { it.id }) { broadcaster ->
                        val isSelected = selectedOutlet.equals(broadcaster.shortCode, ignoreCase = true) || selectedOutlet.equals(broadcaster.id, ignoreCase = true)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(if (isNothing) 2.dp else 14.dp))
                                .background(
                                    if (isSelected) {
                                        if (isNothing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
                                .then(
                                    if (isNothing) Modifier.border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(2.dp))
                                    else Modifier
                                )
                                .clickable {
                                    hapticEngine?.trigger(HapticCue.UI_TAP)
                                    onSelectOutlet(broadcaster.shortCode)
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                // Official Channel Logo Badge
                                NewsChannelLogoBadge(
                                    channelId = broadcaster.id,
                                    shortCode = broadcaster.shortCode,
                                    badgeColorHex = broadcaster.badgeColorHex
                                )

                                Column {
                                    Text(
                                        text = broadcaster.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${broadcaster.country} • ${broadcaster.region} • ${broadcaster.language}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            if (isSelected) {
                                Text(text = "SELECTED", style = MaterialTheme.typography.labelSmall, color = SignalRed)
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

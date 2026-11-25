package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.NewsArticle
import com.example.ui.components.ExpressiveAnimatedShapeBadge
import com.example.ui.components.ExpressiveEmptyStateMotif
import com.example.ui.components.ExpressiveShapeKind
import com.example.ui.components.NewsImageViewer
import com.example.ui.components.NothingCard
import com.example.ui.components.SegmentedProgressBar
import com.example.ui.components.SquiggleProgressBar
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.LocalDeviceProfile
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.SignalRed
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

@Composable
fun SavedLibraryScreen(
    savedArticles: List<NewsArticle>,
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.FEED)
    }

    val designStyle = LocalDesignStyle.current
    val deviceProfile = LocalDeviceProfile.current
    val hapticEngine = LocalHapticEngine.current
    val horizontalPad = deviceProfile.contentPadding
    val isNothing = designStyle == DesignStyle.NOTHING_OS
    val focusManager = LocalFocusManager.current

    val savedSearchQuery by viewModel.savedSearchQuery.collectAsStateWithLifecycle()
    val filteredSavedArticles by viewModel.filteredSavedArticles.collectAsStateWithLifecycle()
    var showClearConfirmDialog by remember { mutableStateOf(false) }

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
                modifier = Modifier.testTag("saved_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back to Feed",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = if (isNothing) "OFFLINE ARCHIVE" else "Saved Offline Library",
                    style = if (isNothing) MaterialTheme.typography.labelLarge else MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isNothing) "${savedArticles.size} DISPATCHES CACHED LOCALLY" else "${savedArticles.size} articles available offline",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (savedArticles.isNotEmpty()) {
                TextButton(
                    onClick = {
                        hapticEngine?.trigger(HapticCue.UI_TAP)
                        showClearConfirmDialog = true
                    },
                    modifier = Modifier.testTag("saved_clear_all_button")
                ) {
                    Text(
                        text = if (isNothing) "[CLEAR ALL]" else "Clear All",
                        style = MaterialTheme.typography.labelSmall,
                        color = SignalRed
                    )
                }
            }
        }

        // Offline Status Banner
        NothingCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPad, vertical = 4.dp),
            testTag = "offline_status_card",
            contentPadding = deviceProfile.cardPadding
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isNothing) {
                    ExpressiveAnimatedShapeBadge(
                        kind = ExpressiveShapeKind.SCALLOPED_SQUIRCLE,
                        size = 36.dp,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Bookmarks,
                            contentDescription = "Offline Archive",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isNothing) "ROOM SQLITE PERSISTENCE // ACTIVE" else "Offline Storage Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Full articles, 3-source registries, and discrepancy audits saved locally.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        if (savedArticles.isNotEmpty() || savedSearchQuery.isNotBlank()) {
            OutlinedTextField(
                value = savedSearchQuery,
                onValueChange = { viewModel.setSavedSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPad, vertical = 4.dp)
                    .testTag("saved_search_input"),
                placeholder = {
                    Text(
                        text = if (isNothing) "SEARCH SAVED ARCHIVE..." else "Search saved articles...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search saved archive",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (savedSearchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.setSavedSearchQuery("") },
                            modifier = Modifier.testTag("saved_search_clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear archive search",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(if (isNothing) 2.dp else 20.dp)
            )
        }

        if (savedArticles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!isNothing) {
                        ExpressiveEmptyStateMotif(
                            icon = Icons.Outlined.Bookmarks,
                            iconContentDescription = "Empty offline archive"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Text(
                        text = if (isNothing) "[OFFLINE ARCHIVE EMPTY]" else "No Saved Articles",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the bookmark icon on any factual dispatch to download it for offline distraction-free reading.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            hapticEngine?.trigger(HapticCue.UI_NAV)
                            viewModel.navigateTo(AppScreen.FEED)
                        },
                        modifier = Modifier.testTag("browse_wire_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = if (isNothing) "BROWSE FACTUAL WIRES" else "Browse Wires",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        } else if (filteredSavedArticles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!isNothing) {
                        ExpressiveEmptyStateMotif(
                            icon = Icons.Outlined.Search,
                            iconContentDescription = "No matching saved articles"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Text(
                        text = if (isNothing) "[NO MATCHING SAVED DISPATCHES]" else "No Matching Saved Articles",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.setSavedSearchQuery("") },
                        modifier = Modifier.testTag("saved_clear_search_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = if (isNothing) "CLEAR SEARCH" else "Clear Search",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = horizontalPad, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(deviceProfile.itemSpacing)
            ) {
                items(filteredSavedArticles, key = { it.id }) { article ->
                    NothingCard(
                        onClick = { viewModel.openArticle(article) },
                        testTag = "saved_card_${article.id}",
                        contentPadding = deviceProfile.cardPadding
                    ) {
                        Column {
                            // Thumbnail
                            NewsImageViewer(
                                imageType = article.imageType,
                                caption = article.imageCaption,
                                imageUrl = article.imageUrl,
                                height = deviceProfile.feedImageHeight,
                                showCaption = false
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isNothing) article.category else article.category.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (article.category == "PALESTINE & GAZA") SignalRed else MaterialTheme.colorScheme.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = {
                                        hapticEngine?.trigger(HapticCue.UI_TAP)
                                        viewModel.deleteSavedArticle(article.id)
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("delete_saved_${article.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteOutline,
                                        contentDescription = "Remove from offline storage",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = article.title,
                                style = if (isNothing) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Reading Progress
                            if (isNothing) {
                                SegmentedProgressBar(
                                    progress = article.readingProgress,
                                    modifier = Modifier.testTag("saved_dots_progress_${article.id}"),
                                    totalSegments = 10,
                                    labelPrefix = "SAVED READING PROGRESS"
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Reading progress",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "${(article.readingProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                SquiggleProgressBar(
                                    progress = article.readingProgress,
                                    scrollPhaseFraction = article.readingProgress,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("saved_squiggle_progress_${article.id}"),
                                    trackHeight = 12.dp,
                                    waveAmplitude = 2.6.dp,
                                    wavelength = 20.dp,
                                    strokeWidth = 2.6.dp,
                                    dotRadius = 3.8.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isNothing) "SRC // ${article.source.uppercase()}" else article.source,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "${article.readTimeMinutes}M READ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = if (isNothing) "CLEAR OFFLINE ARCHIVE?" else "Clear Offline Archive?",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = "This removes all ${savedArticles.size} saved dispatches and reading progress stored locally on your device.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                        showClearConfirmDialog = false
                        viewModel.clearAllSavedArticles()
                    },
                    modifier = Modifier.testTag("saved_confirm_clear_all_button")
                ) {
                    Text(
                        text = if (isNothing) "CLEAR" else "Clear All",
                        color = SignalRed
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text(text = if (isNothing) "CANCEL" else "Cancel")
                }
            }
        )
    }
}


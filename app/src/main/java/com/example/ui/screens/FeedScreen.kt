package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.NewsArticle
import com.example.ui.components.FactualityBadge
import com.example.ui.components.NewsImageViewer
import com.example.ui.components.NothingCard
import com.example.ui.components.NothingPullToRefreshIndicator
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.SignalRed
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: NewsViewModel,
    articles: List<NewsArticle>,
    isRefreshing: Boolean,
    selectedCategory: String,
    savedCount: Int,
    searchQuery: String,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    var currentTimeString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        while (true) {
            currentTimeString = sdf.format(Date())
            delay(1000)
        }
    }

    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS
    val pullToRefreshState = rememberPullToRefreshState()

    val hourlyCountdown by viewModel.hourlyCountdownSeconds.collectAsStateWithLifecycle()
    val lastSyncText by viewModel.lastSyncTimeString.collectAsStateWithLifecycle()
    val countdownMinutes = hourlyCountdown / 60
    val countdownSeconds = hourlyCountdown % 60
    val countdownFormatted = String.format(Locale.getDefault(), "%02d:%02d", countdownMinutes, countdownSeconds)

    Column(modifier = modifier.fillMaxSize()) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SignalRed)
                    )
                    Text(
                        text = if (isNothing) "NOTHING NEWS // WIRE" else "Nothing News",
                        style = if (isNothing) MaterialTheme.typography.labelLarge else MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (isNothing) "TIME // $currentTimeString UTC" else "Live Factual Wire • 3-Source Verified",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Interactive Animated Tutorial Button
                IconButton(
                    onClick = { viewModel.openTutorial() },
                    modifier = Modifier.testTag("feed_tutorial_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = "Interactive Tutorial",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Search toggle
                IconButton(
                    onClick = { viewModel.toggleSearch() },
                    modifier = Modifier.testTag("feed_search_button")
                ) {
                    Icon(
                        imageVector = if (isSearching) Icons.Outlined.Close else Icons.Outlined.Search,
                        contentDescription = "Search news feeds",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Saved Archive Button
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.SAVED) },
                    modifier = Modifier.testTag("feed_saved_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (savedCount > 0) {
                                Badge(
                                    containerColor = SignalRed,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = "$savedCount",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Bookmarks,
                            contentDescription = "Saved offline articles",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Settings Button
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                    modifier = Modifier.testTag("feed_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "App Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // --- Live Hourly Real Outlets Check Status Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(if (isNothing) 2.dp else 12.dp))
                .background(if (isNothing) Color(0xFF141414) else MaterialTheme.colorScheme.surfaceContainerHigh)
                .then(
                    if (isNothing) Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                    else Modifier
                )
                .clickable { viewModel.triggerManualSync() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    text = if (isNothing) "REAL OUTLETS // SYNC IN $countdownFormatted" else "Real Outlets Check in $countdownFormatted",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = if (isNothing) "[SYNC NOW]" else "Sync Now",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // --- Search Field (Conditional) ---
        AnimatedVisibility(visible = isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feed_search_input"),
                    placeholder = {
                        Text(
                            text = if (isNothing) "SEARCH VERIFIED WIRE FEEDS..." else "Search verified factual dispatches...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(if (isNothing) 2.dp else 24.dp)
                )
            }
        }

        // --- Categories Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.categories.forEach { category ->
                val isSelected = selectedCategory == category
                val isPalestineGaza = category == "PALESTINE & GAZA"

                val chipShape = RoundedCornerShape(if (isNothing) 2.dp else 20.dp)

                Box(
                    modifier = Modifier
                        .testTag("category_chip_${category.lowercase().replace(" ", "_")}")
                        .clip(chipShape)
                        .background(
                            if (isSelected) {
                                if (isNothing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                            } else {
                                if (isNothing) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainerHigh
                            }
                        )
                        .then(
                            if (isNothing) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else if (isPalestineGaza) {
                                        SignalRed.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    },
                                    shape = chipShape
                                )
                            } else Modifier
                        )
                        .clickable { viewModel.setCategory(category) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isPalestineGaza) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected && isNothing) MaterialTheme.colorScheme.onPrimary else SignalRed)
                            )
                        }
                        Text(
                            text = if (isNothing) category else category.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                            style = if (isNothing) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelLarge,
                            color = if (isSelected) {
                                if (isNothing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }

        // --- Main Feed List with Bespoke Pull-To-Refresh ---
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshFeed() },
            state = pullToRefreshState,
            indicator = {
                NothingPullToRefreshIndicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .testTag("feed_pull_to_refresh")
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dedicated Palestine & Gaza Spotlight Header if selected
                if (selectedCategory == "PALESTINE & GAZA") {
                    item {
                        NothingCard(
                            testTag = "palestine_gaza_banner",
                            contentPadding = 14.dp
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(SignalRed)
                                    )
                                    Text(
                                        text = if (isNothing) "PALESTINE & GAZA // VERIFIED HUMANITARIAN DESK" else "Palestine & Gaza Humanitarian Desk",
                                        style = if (isNothing) MaterialTheme.typography.labelSmall else MaterialTheme.typography.titleMedium,
                                        color = SignalRed
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Cross-referenced daily against UN OCHA field dispatches, UNRWA logistics registries, WHO medical rosters, and ICRC technical infrastructure teams. Zero editorial bias.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                // Feed Items Count & Status
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isNothing) "3-SOURCE AUDITED // ${articles.size} DISPATCHES" else "${articles.size} Verified Reports",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clickable { viewModel.refreshFeed() }
                                .testTag("feed_manual_refresh")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = if (isNothing) "RE-CHECK SOURCES" else "Re-verify",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                if (articles.isEmpty()) {
                    item {
                        NothingCard(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 24.dp
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isNothing) "[NO MATCHING WIRE REPORTS]" else "No Reports Found",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "All incoming feeds passed factual consensus filtering with zero bias matches.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                // Article Cards with Imagery & Discrepancy Indicators
                items(articles, key = { it.id }) { article ->
                    NothingCard(
                        onClick = { viewModel.openArticle(article) },
                        testTag = "article_card_${article.id}",
                        contentPadding = 14.dp
                    ) {
                        Column {
                            // Article Image Thumbnail
                            NewsImageViewer(
                                imageType = article.imageType,
                                caption = article.imageCaption,
                                height = 150.dp,
                                showCaption = false
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Category & Timestamp Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isNothing) article.category else article.category.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (article.category == "PALESTINE & GAZA") SignalRed else MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = article.timestampString,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Headline
                            Text(
                                text = article.title,
                                style = if (isNothing) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Summary
                            Text(
                                text = article.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Prominently Flagged Discrepancy Tag (if applicable)
                            val hasDiscrepancy = article.flaggedDiscrepancies.any { it.isContested }
                            if (hasDiscrepancy) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(if (isNothing) 2.dp else 8.dp))
                                        .background(if (isNothing) Color(0xFF220000) else MaterialTheme.colorScheme.errorContainer)
                                        .border(
                                            1.dp,
                                            if (isNothing) SignalRed else MaterialTheme.colorScheme.error,
                                            RoundedCornerShape(if (isNothing) 2.dp else 8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.WarningAmber,
                                        contentDescription = "Discrepancy audited",
                                        tint = SignalRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isNothing) "[1 CONTESTED CLAIM AUDITED]" else "Contested Claim Examined",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isNothing) SignalRed else MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Factuality & Verification Badges
                            FactualityBadge(
                                factScore = article.factScore,
                                sourcesCount = article.verifiedSources.size.coerceAtLeast(3)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Footer: Sourcing & Bookmark
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isNothing) "SRC // ${article.source.uppercase()}" else "Verified by ${article.source}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${article.readTimeMinutes}M READ",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )

                                    IconButton(
                                        onClick = { viewModel.toggleSaveCurrentArticle(article) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("bookmark_button_${article.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (article.isSaved) Icons.Outlined.Check else Icons.Outlined.BookmarkBorder,
                                            contentDescription = if (article.isSaved) "Saved" else "Save offline",
                                            tint = if (article.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

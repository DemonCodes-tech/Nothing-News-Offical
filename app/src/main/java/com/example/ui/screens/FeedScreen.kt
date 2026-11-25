package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.outlined.Feed
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AlignmentMode
import com.example.data.model.FeedTab
import com.example.data.model.NewsArticle
import com.example.data.model.PersonalizationCatalog
import com.example.data.model.UserFeedProfile
import com.example.ui.components.ExpressiveAnimatedShapeBadge
import com.example.ui.components.ExpressiveEmptyStateMotif
import com.example.ui.components.ExpressiveShapeKind
import com.example.ui.components.FactualityBadge
import com.example.ui.components.NewsCategoryPickerDialog
import com.example.ui.components.NewsChannelLogoBadge
import com.example.ui.components.NewsImageViewer
import com.example.ui.components.NothingCard
import com.example.ui.components.NothingPullToRefreshIndicator
import com.example.ui.components.OutletDirectoryBar
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.LocalDeviceProfile
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NothingRedTintDark
import com.example.ui.theme.NothingRedTintLight
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
    val isDark = LocalIsDarkTheme.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS
    val nothingRedTint = if (isDark) NothingRedTintDark else NothingRedTintLight
    val pullToRefreshState = rememberPullToRefreshState()

    val hourlyCountdown by viewModel.hourlyCountdownSeconds.collectAsStateWithLifecycle()
    val selectedOutlet by viewModel.selectedOutlet.collectAsStateWithLifecycle()
    val activeArticle by viewModel.activeArticle.collectAsStateWithLifecycle()
    val favoriteBroadcasters by viewModel.favoriteBroadcasters.collectAsStateWithLifecycle()
    val activeFeedTab by viewModel.activeFeedTab.collectAsStateWithLifecycle()
    val userFeedProfile by viewModel.userFeedProfile.collectAsStateWithLifecycle()
    val readerTextScale by viewModel.readerTextScale.collectAsStateWithLifecycle()
    val hapticEngine = LocalHapticEngine.current
    val focusManager = LocalFocusManager.current
    var showCategoryPicker by remember { mutableStateOf(false) }
    val hasActiveFilters = selectedCategory != "ALL WIRES" || selectedOutlet != null || searchQuery.isNotBlank()

    val countdownMinutes = hourlyCountdown / 60
    val countdownSeconds = hourlyCountdown % 60
    val countdownFormatted = String.format(Locale.getDefault(), "%02d:%02d", countdownMinutes, countdownSeconds)

    val deviceProfile = LocalDeviceProfile.current
    val horizontalPad = deviceProfile.contentPadding

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 720.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // --- Top Bar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPad, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(if (isNothing) 4.dp else 12.dp))
                        .clickable {
                            hapticEngine?.trigger(HapticCue.UI_TAP)
                            viewModel.onAppNameTapped()
                        }
                        .padding(vertical = 2.dp, horizontal = 2.dp)
                        .testTag("feed_app_name_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
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
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                        Text(
                            text = if (isNothing) "NEWSTHING // WIRE" else "NewsThing",
                            style = if (isNothing) MaterialTheme.typography.labelLarge else MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = if (isNothing) "LIVE $currentTimeString • 3-SRC AUDIT" else "Live • 3-Source Verified",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Tutorial Replay Button
                    IconButton(
                        onClick = {
                            hapticEngine?.trigger(HapticCue.UI_NAV)
                            viewModel.openTutorial()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("feed_tutorial_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = "Interactive Tutorial",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Search toggle
                    IconButton(
                        onClick = {
                            hapticEngine?.trigger(HapticCue.UI_TAP)
                            viewModel.toggleSearch()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("feed_search_button")
                    ) {
                        Icon(
                            imageVector = if (isSearching) Icons.Outlined.Close else Icons.Outlined.Search,
                            contentDescription = "Search news feeds",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Saved Archive Button
                    IconButton(
                        onClick = {
                            hapticEngine?.trigger(HapticCue.UI_NAV)
                            viewModel.navigateTo(AppScreen.SAVED)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("feed_saved_button")
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
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Settings Button
                    IconButton(
                        onClick = {
                            hapticEngine?.trigger(HapticCue.UI_NAV)
                            viewModel.navigateTo(AppScreen.SETTINGS)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("feed_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "App Settings",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // --- Live Hourly Real Outlets Check Status Bar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPad, vertical = 1.dp)
                    .clip(RoundedCornerShape(if (isNothing) 2.dp else 8.dp))
                    .background(if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainerHigh)
                    .then(
                        if (isNothing) Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        else Modifier
                    )
                    .clickable {
                        hapticEngine?.trigger(HapticCue.UI_TAP)
                        viewModel.triggerManualSync()
                    }
                    .padding(horizontal = 8.dp, vertical = 3.dp),
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
                        text = if (isNothing) "OUTLETS SYNC // IN $countdownFormatted" else "Outlets check in $countdownFormatted",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = if (isNothing) "[SYNC NOW]" else "Sync Now",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    softWrap = false
                )
            }

            // --- Search Field (Conditional) ---
            AnimatedVisibility(visible = isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad, vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("feed_search_input"),
                        placeholder = {
                            Text(
                                text = if (isNothing) "SEARCH VERIFIED WIRE FEEDS..." else "Search verified dispatches...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.setSearchQuery("") },
                                    modifier = Modifier.testTag("feed_search_clear_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Clear search query",
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
            }

            // --- Alphabetical Global Broadcasters Directory Bar ---
            OutletDirectoryBar(
                selectedOutlet = selectedOutlet,
                onSelectOutlet = { viewModel.setSelectedOutlet(it) },
                favoriteBroadcasters = favoriteBroadcasters
            )

            // --- Categories Bar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPad, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val pickerShape = RoundedCornerShape(if (isNothing) 2.dp else 16.dp)
                Row(
                    modifier = Modifier
                        .clip(pickerShape)
                        .background(
                            if (isNothing) nothingRedTint
                            else MaterialTheme.colorScheme.secondaryContainer
                        )
                        .border(
                            width = 1.dp,
                            color = if (isNothing) SignalRed.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                            shape = pickerShape
                        )
                        .clickable {
                            hapticEngine?.trigger(HapticCue.UI_TAP)
                            showCategoryPicker = true
                        }
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                        .testTag("feed_choose_categories_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Choose news categories",
                        tint = if (isNothing) SignalRed else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isNothing) {
                            "CATEGORIES (${userFeedProfile.selectedTopics.size})"
                        } else {
                            "My Categories (${userFeedProfile.selectedTopics.size})"
                        },
                        style = if (isNothing) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                        color = if (isNothing) SignalRed else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                viewModel.categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    val isPalestineGaza = category == "PALESTINE & GAZA"
                    val chipShape = RoundedCornerShape(if (isNothing) 2.dp else 16.dp)

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
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TAP)
                                viewModel.setCategory(category)
                            }
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
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
                                style = if (isNothing) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
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

            // --- Adaptive Layout (Two-Pane on Wide Screens / Single-Pane on Compact) ---
            if (isWide) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp)
                ) {
                    // Left Pane: Article List (44% width)
                    Box(
                        modifier = Modifier
                            .weight(0.44f)
                            .fillMaxHeight()
                    ) {
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
                            modifier = Modifier.fillMaxSize()
                        ) {
                            FeedArticlesList(
                                articles = articles,
                                selectedCategory = selectedCategory,
                                selectedOutlet = selectedOutlet,
                                searchQuery = searchQuery,
                                hasActiveFilters = hasActiveFilters,
                                activeFeedTab = activeFeedTab,
                                userFeedProfile = userFeedProfile,
                                isNothing = isNothing,
                                onEditFeedProfile = { viewModel.openTutorial(startStep = 5) },
                                onResetFilters = { viewModel.resetFeedFilters() },
                                onArticleClick = { article -> viewModel.openArticle(article) },
                                onToggleSave = { article -> viewModel.toggleSaveCurrentArticle(article) },
                                onRefresh = { viewModel.refreshFeed() }
                            )
                        }
                    }

                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.outline)
                    )

                    // Right Pane: Active Article Detail (56% width)
                    val displayArticle = activeArticle ?: articles.firstOrNull()
                    Box(
                        modifier = Modifier
                            .weight(0.56f)
                            .fillMaxHeight()
                    ) {
                        if (displayArticle != null) {
                            ArticleDetailContent(
                                article = displayArticle,
                                onBack = null,
                                onToggleSave = { viewModel.toggleSaveCurrentArticle(displayArticle) },
                                onUpdateProgress = { progress -> viewModel.updateReadingProgress(displayArticle.id, progress) },
                                readerTextScale = readerTextScale,
                                onCycleTextScale = { viewModel.cycleReaderTextScale() },
                                bottomContentPadding = 92.dp
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isNothing) "[SELECT A DISPATCH TO READ]" else "Select an article from the feed",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            } else {
                // Compact Single-Column Layout
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
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
                        FeedArticlesList(
                            articles = articles,
                            selectedCategory = selectedCategory,
                            selectedOutlet = selectedOutlet,
                            searchQuery = searchQuery,
                            hasActiveFilters = hasActiveFilters,
                            activeFeedTab = activeFeedTab,
                            userFeedProfile = userFeedProfile,
                            isNothing = isNothing,
                            onEditFeedProfile = { viewModel.openTutorial(startStep = 5) },
                            onResetFilters = { viewModel.resetFeedFilters() },
                            onArticleClick = { article -> viewModel.openArticle(article) },
                            onToggleSave = { article -> viewModel.toggleSaveCurrentArticle(article) },
                            onRefresh = { viewModel.refreshFeed() }
                        )
                    }
                }
            }
        }

        if (showCategoryPicker) {
            NewsCategoryPickerDialog(
                selectedTopics = userFeedProfile.selectedTopics,
                isNothing = isNothing,
                onToggleTopic = { viewModel.toggleTopic(it) },
                onSelectAllTopics = { viewModel.selectAllTopics() },
                onClearTopics = { viewModel.clearAllTopics() },
                onDismiss = { showCategoryPicker = false }
            )
        }
    }
}

@Composable
private fun FeedArticlesList(
    articles: List<NewsArticle>,
    selectedCategory: String,
    selectedOutlet: String?,
    searchQuery: String,
    hasActiveFilters: Boolean,
    activeFeedTab: FeedTab,
    userFeedProfile: UserFeedProfile,
    isNothing: Boolean,
    onEditFeedProfile: () -> Unit,
    onResetFilters: () -> Unit,
    onArticleClick: (NewsArticle) -> Unit,
    onToggleSave: (NewsArticle) -> Unit,
    onRefresh: () -> Unit
) {
    val deviceProfile = LocalDeviceProfile.current
    val hapticEngine = LocalHapticEngine.current
    val activeCountry = remember(userFeedProfile.countryCode) {
        PersonalizationCatalog.findCountry(userFeedProfile.countryCode)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = deviceProfile.contentPadding, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(deviceProfile.itemSpacing)
    ) {
        // Active Feed Lens Strip (My Feed vs World)
        item {
            NothingCard(
                testTag = if (activeFeedTab == FeedTab.MY_FEED) "my_feed_lens_banner" else "world_feed_lens_banner",
                contentPadding = 10.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isNothing) {
                        ExpressiveAnimatedShapeBadge(
                            kind = ExpressiveShapeKind.SCALLOPED_SQUIRCLE,
                            size = 22.dp,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onTertiaryContainer)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        if (activeFeedTab == FeedTab.MY_FEED) {
                            val lensLabel = when (userFeedProfile.alignmentMode) {
                                AlignmentMode.PARTIES -> {
                                    if (userFeedProfile.selectedParties.isEmpty()) "ALL PARTIES"
                                    else "${userFeedProfile.selectedParties.size} PARTIES"
                                }
                                AlignmentMode.SPECTRUM -> userFeedProfile.selectedSpectrum.uppercase()
                                AlignmentMode.NON_PARTISAN -> "NON-PARTISAN"
                            }
                            val allSidesTag = if (userFeedProfile.allSidesEnabled) " • ALL-SIDES" else ""
                            Text(
                                text = if (isNothing) {
                                    "MY FEED // ${activeCountry.code} • $lensLabel$allSidesTag"
                                } else {
                                    "My Feed • ${activeCountry.name} • $lensLabel$allSidesTag"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = SignalRed,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${userFeedProfile.selectedTopics.size} active categories • Crown-curated priority ranking",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = if (isNothing) "WORLD WIRE // EVERYTHING ABOUT EVERYTHING" else "World Wire • Everything About Everything",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Unfiltered global dispatches across all countries, parties & wires.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (activeFeedTab == FeedTab.MY_FEED) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(if (isNothing) 2.dp else 12.dp))
                                .background(if (isNothing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    hapticEngine?.trigger(HapticCue.UI_NAV)
                                    onEditFeedProfile()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("feed_edit_personalization_button")
                        ) {
                            Text(
                                text = if (isNothing) "[EDIT]" else "Edit",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isNothing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Dedicated Palestine & Gaza Spotlight Header
        if (selectedCategory == "PALESTINE & GAZA") {
            item {
                NothingCard(
                    testTag = "palestine_gaza_banner",
                    contentPadding = 12.dp
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

        // Active Filter Indicator Strip with 1-Tap Reset
        if (hasActiveFilters) {
            item {
                val activeTags = buildList {
                    if (selectedOutlet != null) add("OUTLET: $selectedOutlet")
                    if (selectedCategory != "ALL WIRES") add(selectedCategory)
                    if (searchQuery.isNotBlank()) add("\"${searchQuery.trim()}\"")
                }.joinToString(" • ")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(if (isNothing) 2.dp else 10.dp))
                        .background(if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
                        .border(
                            1.dp,
                            if (isNothing) SignalRed.copy(alpha = 0.65f) else MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(if (isNothing) 2.dp else 10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isNothing) "FILTERED // $activeTags" else "Filtered: $activeTags",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    Text(
                        text = if (isNothing) "[RESET]" else "Reset Filters",
                        style = MaterialTheme.typography.labelSmall,
                        color = SignalRed,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TAP)
                                onResetFilters()
                            }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("feed_reset_filters_chip")
                    )
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
                        .clickable {
                            hapticEngine?.trigger(HapticCue.UI_TAP)
                            onRefresh()
                        }
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
                        if (!isNothing) {
                            ExpressiveEmptyStateMotif(
                                icon = Icons.Outlined.Feed,
                                iconContentDescription = "No reports matching filters"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        Text(
                            text = if (isNothing) "[NO MATCHING WIRE REPORTS]" else "No Reports Found",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Check the Broadcaster Directory or search query to view other dispatches.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        if (hasActiveFilters) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onResetFilters,
                                modifier = Modifier.testTag("empty_state_reset_filters_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(if (isNothing) 2.dp else 16.dp)
                            ) {
                                Text(
                                    text = if (isNothing) "RESET ALL FILTERS" else "Reset Filters",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Article Cards with Imagery & Discrepancy Indicators
        items(articles, key = { it.id }) { article ->
            FeedArticleCard(
                article = article,
                isNothing = isNothing,
                onClick = { onArticleClick(article) },
                onToggleSave = { onToggleSave(article) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(92.dp))
        }
    }
}

@Composable
fun FeedArticleCard(
    article: NewsArticle,
    isNothing: Boolean,
    onClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    val deviceProfile = LocalDeviceProfile.current
    val isDark = LocalIsDarkTheme.current
    val hapticEngine = LocalHapticEngine.current
    val nothingRedTint = if (isDark) NothingRedTintDark else NothingRedTintLight

    NothingCard(
        onClick = onClick,
        testTag = "article_card_${article.id}",
        contentPadding = deviceProfile.cardPadding
    ) {
        Column {
            // Article Image with Real Site Image Loading
            NewsImageViewer(
                imageType = article.imageType,
                caption = article.imageCaption,
                imageUrl = article.imageUrl,
                height = deviceProfile.feedImageHeight,
                showCaption = false
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Category & Timestamp Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    NewsChannelLogoBadge(
                        channelId = article.outletCode ?: article.source,
                        shortCode = article.outletCode ?: article.source.take(4)
                    )
                    Text(
                        text = if (isNothing) "${article.source.uppercase()} // ${article.category}" else "${article.source} • ${article.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (article.category == "PALESTINE & GAZA") SignalRed else MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = article.timestampString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Headline
            Text(
                text = article.title,
                style = if (isNothing) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Summary
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Prominently Flagged Discrepancy Tag (if applicable)
            val hasDiscrepancy = article.flaggedDiscrepancies.any { it.isContested }
            if (hasDiscrepancy) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(if (isNothing) 2.dp else 8.dp))
                        .background(if (isNothing) nothingRedTint else MaterialTheme.colorScheme.errorContainer)
                        .border(
                            1.dp,
                            if (isNothing) SignalRed else MaterialTheme.colorScheme.error,
                            RoundedCornerShape(if (isNothing) 2.dp else 8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = "Discrepancy audited",
                        tint = SignalRed,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isNothing) "[CONTESTED CLAIM AUDITED]" else "Contested Claim Examined",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNothing) SignalRed else MaterialTheme.colorScheme.onErrorContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
            }

            if (article.matchBadges.isNotEmpty()) {
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    article.matchBadges.forEach { badge ->
                        val chipShape = RoundedCornerShape(if (isNothing) 2.dp else 8.dp)
                        Box(
                            modifier = Modifier
                                .clip(chipShape)
                                .background(
                                    if (badge == "ALL-SIDES") {
                                        if (isNothing) nothingRedTint else MaterialTheme.colorScheme.secondaryContainer
                                    } else {
                                        if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainerHigh
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (badge == "ALL-SIDES") SignalRed.copy(alpha = 0.65f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                                    chipShape
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (badge == "ALL-SIDES") SignalRed else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
            }

            // Factuality & Verification Badges
            FactualityBadge(
                factScore = article.factScore,
                sourcesCount = article.verifiedSources.size.coerceAtLeast(3)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Footer: Sourcing & Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${article.readTimeMinutes}M READ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                IconButton(
                    onClick = {
                        hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                        onToggleSave()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (article.isSaved) Icons.Outlined.Check else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (article.isSaved) "Saved" else "Save offline",
                        tint = if (article.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.NewsArticle
import com.example.ui.components.DiscrepancyCallout
import com.example.ui.components.ExpressiveAnimatedShapeBadge
import com.example.ui.components.ExpressiveShapeKind
import com.example.ui.components.FactualityBadge
import com.example.ui.components.NewsChannelLogoBadge
import com.example.ui.components.NewsImageViewer
import com.example.ui.components.NothingCard
import com.example.ui.components.SegmentedProgressBar
import com.example.ui.components.SourceAuditCard
import com.example.ui.components.SquiggleProgressBar
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.LocalDeviceProfile
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.SignalRed
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel
import com.example.ui.viewmodel.ReaderTextScale

@Composable
fun ArticleDetailScreen(
    article: NewsArticle,
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier,
    isTwoPane: Boolean = false
) {
    val readerTextScale by viewModel.readerTextScale.collectAsStateWithLifecycle()

    if (!isTwoPane) {
        BackHandler {
            viewModel.navigateTo(AppScreen.FEED)
        }
    }

    ArticleDetailContent(
        article = article,
        onBack = if (!isTwoPane) { { viewModel.navigateTo(AppScreen.FEED) } } else null,
        onToggleSave = { viewModel.toggleSaveCurrentArticle(article) },
        onUpdateProgress = { progress -> viewModel.updateReadingProgress(article.id, progress) },
        modifier = modifier,
        readerTextScale = readerTextScale,
        onCycleTextScale = { viewModel.cycleReaderTextScale() }
    )
}

@Composable
fun ArticleDetailContent(
    article: NewsArticle,
    onBack: (() -> Unit)?,
    onToggleSave: () -> Unit,
    onUpdateProgress: (Float) -> Unit,
    modifier: Modifier = Modifier,
    readerTextScale: ReaderTextScale = ReaderTextScale.STANDARD,
    onCycleTextScale: (() -> Unit)? = null,
    bottomContentPadding: Dp = 18.dp
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val designStyle = LocalDesignStyle.current
    val deviceProfile = LocalDeviceProfile.current
    val hapticEngine = LocalHapticEngine.current
    val horizontalPad = deviceProfile.contentPadding
    val isNothing = designStyle == DesignStyle.NOTHING_OS

    val maxScroll = scrollState.maxValue
    val liveScrollProgress = if (maxScroll > 0) {
        (scrollState.value.toFloat() / maxScroll.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val displayProgress = maxOf(article.readingProgress, liveScrollProgress)

    LaunchedEffect(liveScrollProgress) {
        if (article.isSaved && liveScrollProgress > 0.05f && liveScrollProgress > article.readingProgress) {
            onUpdateProgress(liveScrollProgress)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // --- Navigation & Action Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPad, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = {
                        hapticEngine?.trigger(HapticCue.UI_NAV)
                        onBack()
                    },
                    modifier = Modifier.testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Navigate Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(start = 6.dp)
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
                        text = if (isNothing) "READING PANE // VERIFIED WIRE" else "Verified Wire Dispatch",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onCycleTextScale != null) {
                    val isScaled = readerTextScale != ReaderTextScale.STANDARD
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                            .clip(RoundedCornerShape(if (isNothing) 6.dp else 12.dp))
                            .clickable {
                                hapticEngine?.trigger(HapticCue.UI_TAP)
                                onCycleTextScale()
                            }
                            .semantics {
                                contentDescription = "Reader text size: ${readerTextScale.label}"
                            }
                            .testTag("detail_text_scale_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(if (isNothing) 4.dp else 10.dp))
                                .background(
                                    if (isScaled) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isScaled) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    },
                                    shape = RoundedCornerShape(if (isNothing) 4.dp else 10.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = readerTextScale.shortCode,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isScaled) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }

                // Share action
                IconButton(
                    onClick = {
                        hapticEngine?.trigger(HapticCue.UI_TAP)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TITLE, article.title)
                            putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.summary}\n\n[3-Source Verified via NewsThing Factual Wire]")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share verified factual report")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.testTag("detail_share_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share article",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Bookmark toggle
                IconButton(
                    onClick = {
                        hapticEngine?.trigger(HapticCue.UI_TOGGLE)
                        onToggleSave()
                    },
                    modifier = Modifier.testTag("detail_save_button")
                ) {
                    Icon(
                        imageVector = if (article.isSaved) Icons.Outlined.Check else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (article.isSaved) "Saved to offline archive" else "Save offline",
                        tint = if (article.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // --- Reading Progress: Black-to-Red Dots (Nothing) vs Expressive Squiggle (Material You) ---
        if (isNothing) {
            SegmentedProgressBar(
                progress = displayProgress,
                modifier = Modifier
                    .padding(horizontal = horizontalPad, vertical = 4.dp)
                    .testTag("reader_nothing_dots_progress_bar"),
                totalSegments = 14
            )
        } else {
            SquiggleProgressBar(
                progress = displayProgress,
                scrollPhaseFraction = liveScrollProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPad, vertical = 2.dp)
                    .testTag("reader_squiggle_progress_bar"),
                trackHeight = 14.dp,
                waveAmplitude = 3.2.dp,
                wavelength = 22.dp,
                strokeWidth = 3.dp,
                dotRadius = 4.4.dp
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )

        // --- Article Reading Content ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = horizontalPad, vertical = 8.dp)
        ) {
            // Category & Sourcing Header
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
                Text(
                    text = "${article.timestampString} // ${article.readTimeMinutes}M",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val headlineBase = if (isNothing) {
                MaterialTheme.typography.headlineLarge
            } else {
                MaterialTheme.typography.headlineMedium
            }
            val scaledHeadlineStyle = headlineBase.copy(
                fontSize = (headlineBase.fontSize.value * readerTextScale.multiplier).sp,
                lineHeight = (headlineBase.lineHeight.value * readerTextScale.multiplier).sp
            )
            val bodyBase = MaterialTheme.typography.bodyLarge
            val scaledBodyStyle = bodyBase.copy(
                fontSize = (bodyBase.fontSize.value * readerTextScale.multiplier).sp,
                lineHeight = (bodyBase.lineHeight.value * readerTextScale.multiplier).sp
            )

            // Headline
            Text(
                text = article.title,
                style = scaledHeadlineStyle,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Factuality Badge
            FactualityBadge(
                factScore = article.factScore,
                sourcesCount = article.verifiedSources.size.coerceAtLeast(3)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Editorial Image with Dither/Natural Treatment & Real Site Image Loading
            NewsImageViewer(
                imageType = article.imageType,
                caption = article.imageCaption,
                imageUrl = article.imageUrl,
                height = deviceProfile.detailImageHeight,
                showCaption = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Main Text Body
            Text(
                text = article.content,
                style = scaledBodyStyle,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = scaledBodyStyle.lineHeight
            )

            // Prominent Inline Discrepancy Callouts (if any)
            if (article.flaggedDiscrepancies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                article.flaggedDiscrepancies.forEach { discrepancy ->
                    DiscrepancyCallout(
                        discrepancy = discrepancy,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 3-Source Independent Verification Registry ---
            if (article.verifiedSources.isNotEmpty()) {
                SourceAuditCard(
                    sources = article.verifiedSources,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Key Chronological Facts Card
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "detail_key_facts_card",
                contentPadding = deviceProfile.cardPadding
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (!isNothing) {
                            ExpressiveAnimatedShapeBadge(
                                kind = ExpressiveShapeKind.ORGANIC_FLOWER,
                                size = 18.dp,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onTertiaryContainer)
                                )
                            }
                        }
                        Text(
                            text = if (isNothing) "VERIFIED CHRONOLOGICAL RECORD" else "Verified Chronological Record",
                            style = if (isNothing) MaterialTheme.typography.labelSmall else MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    article.keyFacts.forEach { fact ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "• ",
                                color = SignalRed,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = fact,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(bottomContentPadding))
        }
    }
}

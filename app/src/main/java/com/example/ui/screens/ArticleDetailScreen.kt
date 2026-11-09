package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.NewsArticle
import com.example.ui.components.DiscrepancyCallout
import com.example.ui.components.FactualityBadge
import com.example.ui.components.NewsImageViewer
import com.example.ui.components.NothingCard
import com.example.ui.components.SegmentedProgressBar
import com.example.ui.components.SourceAuditCard
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.SignalRed
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

@Composable
fun ArticleDetailScreen(
    article: NewsArticle,
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    BackHandler {
        viewModel.navigateTo(AppScreen.FEED)
    }

    val designStyle = LocalDesignStyle.current
    val isNothing = designStyle == DesignStyle.NOTHING_OS

    val maxScroll = scrollState.maxValue
    val progress = if (maxScroll > 0) {
        (scrollState.value.toFloat() / maxScroll.toFloat()).coerceIn(0f, 1f)
    } else 0f

    LaunchedEffect(progress) {
        if (article.isSaved && progress > 0.05f) {
            viewModel.updateReadingProgress(article.id, progress)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // --- Navigation & Action Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.FEED) },
                modifier = Modifier.testTag("detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Navigate Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Share action
                IconButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TITLE, article.title)
                            putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.summary}\n\n[3-Source Verified via Nothing News Factual Wire]")
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
                    onClick = { viewModel.toggleSaveCurrentArticle(article) },
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

        // --- Reading Progress: Segmented Ticks (Nothing) vs Smooth Bar (Material You) ---
        if (isNothing) {
            SegmentedProgressBar(
                progress = progress,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                totalSegments = 14,
                labelPrefix = "SCROLL POSITION"
            )
        } else {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 6.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )

        // --- Article Reading Content ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Category & Sourcing Header
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
                    text = "${article.timestampString} // ${article.readTimeMinutes}M READ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Headline
            Text(
                text = article.title,
                style = if (isNothing) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Factuality Badge
            FactualityBadge(
                factScore = article.factScore,
                sourcesCount = article.verifiedSources.size.coerceAtLeast(3)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Editorial Image with Dither/Natural Treatment
            NewsImageViewer(
                imageType = article.imageType,
                caption = article.imageCaption,
                height = 200.dp,
                showCaption = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Main Text Body
            Text(
                text = article.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )

            // Prominent Inline Discrepancy Callouts (if any)
            if (article.flaggedDiscrepancies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                article.flaggedDiscrepancies.forEach { discrepancy ->
                    DiscrepancyCallout(
                        discrepancy = discrepancy,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3-Source Independent Verification Registry ---
            if (article.verifiedSources.isNotEmpty()) {
                SourceAuditCard(
                    sources = article.verifiedSources,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Confirmed Facts Checklist Panel
            NothingCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "detail_fact_audit_card",
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
                            text = if (isNothing) "CONFIRMED FACTUAL ASSERTIONS" else "Confirmed Primary Assertions",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    article.keyFacts.forEach { fact ->
                        Text(
                            text = "• $fact",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

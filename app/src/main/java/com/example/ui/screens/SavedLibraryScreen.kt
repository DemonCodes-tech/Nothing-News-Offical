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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.NewsArticle
import com.example.ui.components.NewsImageViewer
import com.example.ui.components.NothingCard
import com.example.ui.components.SegmentedProgressBar
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
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
                modifier = Modifier.testTag("saved_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back to Feed",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = if (isNothing) "OFFLINE ARCHIVE" else "Saved Offline Library",
                    style = if (isNothing) MaterialTheme.typography.labelLarge else MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isNothing) "${savedArticles.size} DISPATCHES CACHED IN LOCAL STORAGE" else "${savedArticles.size} articles available offline",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Offline Status Banner
        NothingCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            testTag = "offline_status_card",
            contentPadding = 12.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
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
                        onClick = { viewModel.navigateTo(AppScreen.FEED) },
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(savedArticles, key = { it.id }) { article ->
                    NothingCard(
                        onClick = { viewModel.openArticle(article) },
                        testTag = "saved_card_${article.id}",
                        contentPadding = 14.dp
                    ) {
                        Column {
                            // Thumbnail
                            NewsImageViewer(
                                imageType = article.imageType,
                                caption = article.imageCaption,
                                height = 130.dp,
                                showCaption = false
                            )

                            Spacer(modifier = Modifier.height(8.dp))

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

                                IconButton(
                                    onClick = { viewModel.deleteSavedArticle(article.id) },
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
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { article.readingProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

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
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

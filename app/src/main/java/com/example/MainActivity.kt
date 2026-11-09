package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.NewsRepository
import com.example.ui.components.DotFieldBackground
import com.example.ui.components.EasterEggOverlay
import com.example.ui.components.TutorialDialog
import com.example.ui.screens.ArticleDetailScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.SavedLibraryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.LocalDesignStyle
import com.example.ui.theme.NothingNewsTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel
import com.example.ui.viewmodel.NewsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val database = remember { AppDatabase.getDatabase(context) }
            val repository = remember { NewsRepository(database.articleDao()) }
            val viewModel: NewsViewModel = viewModel(
                factory = NewsViewModelFactory(repository, context.applicationContext)
            )

            val designStyle by viewModel.designStyle.collectAsStateWithLifecycle()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            NothingNewsTheme(
                designStyle = designStyle,
                darkTheme = isDarkMode
            ) {
                NothingNewsApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NothingNewsApp(viewModel: NewsViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val articles by viewModel.filteredFeedArticles.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val savedArticles by viewModel.savedArticles.collectAsStateWithLifecycle()
    val activeArticle by viewModel.activeArticle.collectAsStateWithLifecycle()
    val isEasterEggVisible by viewModel.isEasterEggVisible.collectAsStateWithLifecycle()
    val showTutorial by viewModel.showTutorial.collectAsStateWithLifecycle()
    val tutorialStep by viewModel.tutorialStep.collectAsStateWithLifecycle()
    val designStyle by viewModel.designStyle.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val notificationPreferences by viewModel.notificationPreferences.collectAsStateWithLifecycle()

    val isNothing = designStyle == DesignStyle.NOTHING_OS

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Dot Field Canvas Underlay (active in Nothing OS mode)
        DotFieldBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    AppScreen.FEED -> {
                        FeedScreen(
                            viewModel = viewModel,
                            articles = articles,
                            isRefreshing = isRefreshing,
                            selectedCategory = selectedCategory,
                            savedCount = savedArticles.size,
                            searchQuery = searchQuery,
                            isSearching = isSearching
                        )
                    }
                    AppScreen.DETAIL -> {
                        activeArticle?.let { article ->
                            ArticleDetailScreen(
                                article = article,
                                viewModel = viewModel
                            )
                        } ?: run {
                            viewModel.navigateTo(AppScreen.FEED)
                        }
                    }
                    AppScreen.SAVED -> {
                        SavedLibraryScreen(
                            savedArticles = savedArticles,
                            viewModel = viewModel
                        )
                    }
                    AppScreen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            designStyle = designStyle,
                            isDarkMode = isDarkMode
                        )
                    }
                }

                // Hidden Easter Egg Animated Banner Overlay ("Made By Demon")
                EasterEggOverlay(
                    visible = isEasterEggVisible,
                    onDismiss = { viewModel.dismissEasterEgg() },
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                // Interactive Animated Setup Wizard Dialog
                TutorialDialog(
                    visible = showTutorial,
                    currentStep = tutorialStep,
                    currentDesignStyle = designStyle,
                    notificationPreferences = notificationPreferences,
                    onSelectDesignStyle = { viewModel.setDesignStyle(it) },
                    onToggleNotification = { viewModel.toggleNotification(it) },
                    onNext = { viewModel.nextTutorialStep() },
                    onPrev = { viewModel.prevTutorialStep() },
                    onDismiss = { viewModel.dismissTutorial() }
                )
            }
        }
    }
}

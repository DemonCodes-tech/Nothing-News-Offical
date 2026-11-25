package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Feed
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.background.NewsSyncPermissions
import com.example.data.local.AppDatabase
import com.example.data.model.FeedTab
import com.example.data.notifications.NewsNotificationHelper
import com.example.data.repository.NewsRepository
import com.example.ui.components.AmbientTouchBus
import com.example.ui.components.BottomPillNavBar
import com.example.ui.components.DotFieldBackground
import com.example.ui.components.EasterEggOverlay
import com.example.ui.components.LocalAmbientTouchBus
import com.example.ui.components.MarinNotificationPermissionOverlay
import com.example.ui.components.MarinOriginStoryOverlay
import com.example.ui.components.MaterialYouExpressiveBackground
import com.example.ui.components.MaterialYouForegroundShapesOverlay
import com.example.ui.components.TutorialDialog
import com.example.ui.components.findActivity
import com.example.ui.components.observeAmbientTouchPassThrough
import com.example.ui.components.openAppNotificationSettings
import com.example.ui.screens.ArticleDetailScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.SavedLibraryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.HapticCue
import com.example.ui.theme.LocalDeviceProfile
import com.example.ui.theme.LocalHapticEngine
import com.example.ui.theme.NothingNewsTheme
import com.example.ui.theme.calculateDeviceProfile
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel
import com.example.ui.viewmodel.NewsViewModelFactory

class MainActivity : ComponentActivity() {

    private var newsViewModel: NewsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = NewsRepository(database.articleDao())
        val viewModel = ViewModelProvider(
            this,
            NewsViewModelFactory(repository, applicationContext)
        )[NewsViewModel::class.java]
        newsViewModel = viewModel

        if (savedInstanceState == null) {
            viewModel.handleNotificationIntent(intent)
        }

        setContent {
            val designStyle by viewModel.designStyle.collectAsStateWithLifecycle()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val appFontScale by viewModel.appFontScale.collectAsStateWithLifecycle()
            val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()

            NothingNewsTheme(
                designStyle = designStyle,
                darkTheme = isDarkMode,
                appFontScale = appFontScale,
                hapticFeedbackEnabled = hapticFeedbackEnabled
            ) {
                NewsThingApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        newsViewModel?.handleNotificationIntent(intent)
    }
}

@Composable
fun NewsThingApp(viewModel: NewsViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val articles by viewModel.filteredFeedArticles.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val savedArticles by viewModel.savedArticles.collectAsStateWithLifecycle()
    val activeArticle by viewModel.activeArticle.collectAsStateWithLifecycle()
    val isEasterEggVisible by viewModel.isEasterEggVisible.collectAsStateWithLifecycle()
    val isMarinOriginEasterEggVisible by viewModel.isMarinOriginEasterEggVisible.collectAsStateWithLifecycle()
    val showTutorial by viewModel.showTutorial.collectAsStateWithLifecycle()
    val tutorialStep by viewModel.tutorialStep.collectAsStateWithLifecycle()
    val designStyle by viewModel.designStyle.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val appFontScale by viewModel.appFontScale.collectAsStateWithLifecycle()
    val notificationPreferences by viewModel.notificationPreferences.collectAsStateWithLifecycle()
    val favoriteBroadcasters by viewModel.favoriteBroadcasters.collectAsStateWithLifecycle()
    val activeFeedTab by viewModel.activeFeedTab.collectAsStateWithLifecycle()
    val userFeedProfile by viewModel.userFeedProfile.collectAsStateWithLifecycle()
    val backgroundSyncPreferences by viewModel.backgroundSyncPreferences.collectAsStateWithLifecycle()
    val isUpgradingOldUser by viewModel.isUpgradingOldUser.collectAsStateWithLifecycle()

    val isNothing = designStyle == DesignStyle.NOTHING_OS
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasNotificationPermission by remember {
        mutableStateOf(NewsNotificationHelper.hasNotificationPermission(context))
    }
    var hasDismissedStandalonePrompt by remember {
        mutableStateOf(NewsNotificationHelper.hasDismissedStandalonePrompt(context))
    }
    var isBatteryExempted by remember {
        mutableStateOf(NewsSyncPermissions.isBatteryOptimizationIgnored(context))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasNotificationPermission = NewsNotificationHelper.hasNotificationPermission(context)
                isBatteryExempted = NewsSyncPermissions.isBatteryOptimizationIgnored(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted && NewsNotificationHelper.hasNotificationPermission(context)
        if (hasNotificationPermission) {
            NewsNotificationHelper.dismissStandalonePrompt(context)
            hasDismissedStandalonePrompt = true
            viewModel.onNotificationPermissionGranted()
        }
    }

    val requestNotificationAccess: () -> Unit = {
        NewsNotificationHelper.ensureNotificationChannels(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val activity = context.findActivity()
            val alreadyPrompted = NewsNotificationHelper.hasPromptedPermissionOnce(context)
            val shouldShowRationale = activity != null &&
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)
            if (alreadyPrompted && !shouldShowRationale && !NewsNotificationHelper.hasNotificationPermission(context)) {
                openAppNotificationSettings(context)
            } else {
                NewsNotificationHelper.markPromptedPermissionOnce(context)
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (!NewsNotificationHelper.hasNotificationPermission(context)) {
                openAppNotificationSettings(context)
            } else {
                hasNotificationPermission = true
                viewModel.onNotificationPermissionGranted()
            }
        }
    }

    val requestBatteryExemption: () -> Unit = {
        NewsSyncPermissions.requestBatteryOptimizationExemption(context)
    }

    val ambientTouchBus = remember { AmbientTouchBus() }
    val hapticEngine = LocalHapticEngine.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val deviceProfile = calculateDeviceProfile(maxWidth, maxHeight, isNothing)

        CompositionLocalProvider(
            LocalDeviceProfile provides deviceProfile,
            LocalAmbientTouchBus provides ambientTouchBus
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .observeAmbientTouchPassThrough(
                        bus = if (!showTutorial && !isMarinOriginEasterEggVisible) ambientTouchBus else null,
                        inTutorial = false
                    )
            ) {
                // Dot Field Canvas Underlay (Nothing OS) & Animated Expressive Shapes (Material You)
                DotFieldBackground()
                MaterialYouExpressiveBackground(activeScreen = currentScreen)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Side Navigation Rail on Expanded Tablet Screens
                        if (deviceProfile.isExpanded && !deviceProfile.isSmallScreen) {
                            NavigationRail(
                                modifier = Modifier.fillMaxHeight(),
                                containerColor = if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ) {
                                NavigationRailItem(
                                    selected = currentScreen == AppScreen.FEED,
                                    onClick = {
                                        hapticEngine?.trigger(HapticCue.UI_NAV)
                                        viewModel.navigateTo(AppScreen.FEED)
                                    },
                                    icon = { Icon(Icons.Outlined.Feed, contentDescription = "Feed") },
                                    label = { Text("Feed", style = MaterialTheme.typography.labelSmall) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = if (isNothing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedTextColor = if (isNothing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                                        indicatorColor = if (isNothing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                                NavigationRailItem(
                                    selected = currentScreen == AppScreen.SAVED,
                                    onClick = {
                                        hapticEngine?.trigger(HapticCue.UI_NAV)
                                        viewModel.navigateTo(AppScreen.SAVED)
                                    },
                                    icon = { Icon(Icons.Outlined.Bookmarks, contentDescription = "Saved") },
                                    label = { Text("Saved (${savedArticles.size})", style = MaterialTheme.typography.labelSmall) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = if (isNothing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedTextColor = if (isNothing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                                        indicatorColor = if (isNothing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                                NavigationRailItem(
                                    selected = currentScreen == AppScreen.SETTINGS,
                                    onClick = {
                                        hapticEngine?.trigger(HapticCue.UI_NAV)
                                        viewModel.navigateTo(AppScreen.SETTINGS)
                                    },
                                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                                    label = { Text("Settings", style = MaterialTheme.typography.labelSmall) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = if (isNothing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedTextColor = if (isNothing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                                        indicatorColor = if (isNothing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }

                        // Main Screen Body
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
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
                                            viewModel = viewModel,
                                            isTwoPane = false
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
                                        isDarkMode = isDarkMode,
                                        hasNotificationPermission = hasNotificationPermission,
                                        isBatteryExempted = isBatteryExempted,
                                        onRequestNotificationPermission = requestNotificationAccess,
                                        onRequestBatteryExemption = requestBatteryExemption,
                                        onSendTestNotification = { viewModel.sendTestNotification() }
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
                                appFontScale = appFontScale,
                                notificationPreferences = notificationPreferences,
                                favoriteBroadcasters = favoriteBroadcasters,
                                userFeedProfile = userFeedProfile,
                                backgroundSyncPreferences = backgroundSyncPreferences,
                                isUpgradingOldUser = isUpgradingOldUser,
                                hasNotificationPermission = hasNotificationPermission,
                                isBatteryExempted = isBatteryExempted,
                                onRequestNotificationPermission = requestNotificationAccess,
                                onRequestBatteryExemption = requestBatteryExemption,
                                onSendTestNotification = { viewModel.sendTestNotification() },
                                onToggleFavoriteBroadcaster = { viewModel.toggleFavoriteBroadcaster(it) },
                                onSelectDesignStyle = { viewModel.setDesignStyle(it) },
                                onAppFontScaleChange = { viewModel.setAppFontScale(it) },
                                onResetAppFontScale = { viewModel.resetAppFontScale() },
                                onToggleNotification = { viewModel.toggleNotification(it) },
                                onSelectCountry = { viewModel.setCountry(it) },
                                onSelectAlignmentMode = { viewModel.setAlignmentMode(it) },
                                onToggleParty = { viewModel.toggleParty(it) },
                                onSelectSpectrum = { viewModel.setSpectrum(it) },
                                onToggleAllSides = { viewModel.toggleAllSides() },
                                onToggleTopic = { viewModel.toggleTopic(it) },
                                onSelectAllTopics = { viewModel.selectAllTopics() },
                                onClearTopics = { viewModel.clearAllTopics() },
                                onToggleWorkManagerSync = { viewModel.toggleWorkManagerSync() },
                                onToggleLiveSyncService = { viewModel.toggleLiveSyncService() },
                                onNext = { viewModel.nextTutorialStep() },
                                onPrev = { viewModel.prevTutorialStep() },
                                onDismiss = { viewModel.dismissTutorial() }
                            )

                            // Standalone Marin (Overshoulder Sweater) Notif Prompt for returning users
                            MarinNotificationPermissionOverlay(
                                visible = !showTutorial && !hasNotificationPermission && !hasDismissedStandalonePrompt,
                                isNothing = isNothing,
                                onRequestPermission = requestNotificationAccess,
                                onDismiss = {
                                    NewsNotificationHelper.dismissStandalonePrompt(context)
                                    hasDismissedStandalonePrompt = true
                                }
                            )
                        }
                    }
                }

                // Floating Pill Bottom Navigation Bar (Crown: My Feed | Planet: World)
                if (currentScreen == AppScreen.FEED && !showTutorial && !isMarinOriginEasterEggVisible) {
                    BottomPillNavBar(
                        activeTab = activeFeedTab,
                        isNothing = isNothing,
                        onSelectTab = { tab: FeedTab -> viewModel.setActiveFeedTab(tab) },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                // Subtle non-blocking foreground floating morphing shapes (Material You only)
                if (!showTutorial && !isMarinOriginEasterEggVisible) {
                    MaterialYouForegroundShapesOverlay(activeScreen = currentScreen)
                }

                // Full-Screen Secret "Codename: Marin" Origin Story Easter Egg
                MarinOriginStoryOverlay(
                    visible = isMarinOriginEasterEggVisible,
                    isNothing = isNothing,
                    onDismiss = { viewModel.dismissMarinOriginEasterEgg() }
                )
            }
        }
    }
}

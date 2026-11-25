package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.background.BackgroundSyncManager
import com.example.data.model.AlignmentMode
import com.example.data.model.BackgroundSyncPreferences
import com.example.data.model.FeedTab
import com.example.data.model.NewsArticle
import com.example.data.model.NewsPersonalizationEngine
import com.example.data.model.PersonalizationCatalog
import com.example.data.model.UserFeedProfile
import com.example.data.notifications.NewsNotificationHelper
import com.example.data.repository.NewsRepository
import com.example.ui.theme.DesignStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotificationPreferences(
    val naturalDisasters: Boolean = true,
    val humanitarianCrisis: Boolean = true,
    val healthPandemic: Boolean = true,
    val scienceClimate: Boolean = false,
    val economyEnergy: Boolean = false
)

enum class ReaderTextScale(val label: String, val shortCode: String, val multiplier: Float) {
    COMPACT("Compact", "A-", 0.90f),
    STANDARD("Standard", "Aa", 1.0f),
    LARGE("Large", "A+", 1.18f)
}

enum class AppScreen {
    FEED,
    DETAIL,
    SAVED,
    SETTINGS
}

class NewsViewModel(
    private val repository: NewsRepository,
    private val context: android.content.Context? = null
) : ViewModel() {

    companion object {
        const val CURRENT_TUTORIAL_VERSION = 2
        const val TOTAL_TUTORIAL_STEPS = 9
    }

    private val prefs = context?.getSharedPreferences("nothing_news_prefs", android.content.Context.MODE_PRIVATE)

    val categories = listOf(
        "ALL WIRES",
        "SPORTS",
        "TECH",
        "CARS",
        "AI",
        "ECONOMY",
        "ENTERTAINMENT",
        "HEALTH",
        "CLIMATE",
        "PALESTINE & GAZA",
        "GLOBAL"
    )

    private val _currentScreen = MutableStateFlow(AppScreen.FEED)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeFeedTab = MutableStateFlow(
        try {
            val saved = prefs?.getString("active_feed_tab", FeedTab.MY_FEED.name)
            if (saved != null) FeedTab.valueOf(saved) else FeedTab.MY_FEED
        } catch (_: Exception) {
            FeedTab.MY_FEED
        }
    )
    val activeFeedTab: StateFlow<FeedTab> = _activeFeedTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL WIRES")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedOutlet = MutableStateFlow<String?>(null)
    val selectedOutlet: StateFlow<String?> = _selectedOutlet.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _activeArticle = MutableStateFlow<NewsArticle?>(null)
    val activeArticle: StateFlow<NewsArticle?> = _activeArticle.asStateFlow()

    // Old setting preserved: Nothing OS vs Material You
    private val _designStyle = MutableStateFlow(
        try {
            val savedStyle = prefs?.getString("design_style", DesignStyle.NOTHING_OS.name)
            if (savedStyle != null) DesignStyle.valueOf(savedStyle) else DesignStyle.NOTHING_OS
        } catch (_: Exception) {
            DesignStyle.NOTHING_OS
        }
    )
    val designStyle: StateFlow<DesignStyle> = _designStyle.asStateFlow()

    // Old setting preserved: Dark vs Light Theme
    private val _isDarkMode = MutableStateFlow(prefs?.getBoolean("dark_mode", true) ?: true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Old setting preserved: Notification Alert Toggles
    private val _notificationPreferences = MutableStateFlow(
        NotificationPreferences(
            naturalDisasters = prefs?.getBoolean("notif_natural_disasters", true) ?: true,
            humanitarianCrisis = prefs?.getBoolean("notif_humanitarian_crisis", true) ?: true,
            healthPandemic = prefs?.getBoolean("notif_health_pandemic", true) ?: true,
            scienceClimate = prefs?.getBoolean("notif_science_climate", false) ?: false,
            economyEnergy = prefs?.getBoolean("notif_economy_energy", false) ?: false
        )
    )
    val notificationPreferences: StateFlow<NotificationPreferences> = _notificationPreferences.asStateFlow()

    // Old setting preserved: Pinned Broadcasters
    private val _favoriteBroadcasters = MutableStateFlow<Set<String>>(
        prefs?.getStringSet("favorite_broadcasters", setOf("cnn_int", "bbc_news", "sky_news", "al_jazeera_en", "dw", "reuters"))
            ?: setOf("cnn_int", "bbc_news", "sky_news", "al_jazeera_en", "dw", "reuters")
    )
    val favoriteBroadcasters: StateFlow<Set<String>> = _favoriteBroadcasters.asStateFlow()

    // New v2.2 Setting: User Feed Profile (Country, Parties / Spectrum / Non-Partisan, All-Sides, Topics)
    private val _userFeedProfile = MutableStateFlow(loadUserFeedProfile())
    val userFeedProfile: StateFlow<UserFeedProfile> = _userFeedProfile.asStateFlow()

    // New v2.2 Setting: Background WorkManager + Foreground Live Sync Preferences
    private val _backgroundSyncPreferences = MutableStateFlow(
        BackgroundSyncPreferences(
            workManagerEnabled = prefs?.getBoolean("bg_work_manager_enabled", true) ?: true,
            liveServiceEnabled = prefs?.getBoolean("bg_live_service_enabled", false) ?: false
        )
    )
    val backgroundSyncPreferences: StateFlow<BackgroundSyncPreferences> = _backgroundSyncPreferences.asStateFlow()

    private val _isEasterEggVisible = MutableStateFlow(false)
    val isEasterEggVisible: StateFlow<Boolean> = _isEasterEggVisible.asStateFlow()

    private val _isMarinOriginEasterEggVisible = MutableStateFlow(false)
    val isMarinOriginEasterEggVisible: StateFlow<Boolean> = _isMarinOriginEasterEggVisible.asStateFlow()

    // Versioned onboarding check: re-opens automatically for old users who haven't completed v2
    // without wiping any of their saved theme, broadcasters, alerts, or Room articles.
    private val _isUpgradingOldUser = MutableStateFlow(
        (prefs?.getBoolean("has_completed_tutorial", false) == true) &&
            ((prefs.getInt("tutorial_version_completed", 0)) < CURRENT_TUTORIAL_VERSION)
    )
    val isUpgradingOldUser: StateFlow<Boolean> = _isUpgradingOldUser.asStateFlow()

    private val _showTutorial = MutableStateFlow(
        (prefs?.getInt("tutorial_version_completed", 0) ?: 0) < CURRENT_TUTORIAL_VERSION
    )
    val showTutorial: StateFlow<Boolean> = _showTutorial.asStateFlow()

    private val _tutorialStep = MutableStateFlow(
        if (_isUpgradingOldUser.value) 5 else 0
    )
    val tutorialStep: StateFlow<Int> = _tutorialStep.asStateFlow()

    private val _hourlyCountdownSeconds = MutableStateFlow(3600)
    val hourlyCountdownSeconds: StateFlow<Int> = _hourlyCountdownSeconds.asStateFlow()

    val lastSyncTimeString: StateFlow<String> = repository.lastSyncTimeString.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "JUST NOW"
    )

    // Persisted Reader Typography Scale
    private val _readerTextScale = MutableStateFlow(
        try {
            val savedScale = prefs?.getString("reader_text_scale", ReaderTextScale.STANDARD.name)
            if (savedScale != null) ReaderTextScale.valueOf(savedScale) else ReaderTextScale.STANDARD
        } catch (_: Exception) {
            ReaderTextScale.STANDARD
        }
    )
    val readerTextScale: StateFlow<ReaderTextScale> = _readerTextScale.asStateFlow()

    // Persisted App-Wide Accessibility Text Scale (1.0f .. 1.5f)
    private val _appFontScale = MutableStateFlow(
        (prefs?.getFloat("app_font_scale", 1.0f) ?: 1.0f).coerceIn(1.0f, 1.5f)
    )
    val appFontScale: StateFlow<Float> = _appFontScale.asStateFlow()

    // Persisted Tactile Haptic Feedback Preference (Shapes, Pulsing Dots, UI Controls)
    private val _hapticFeedbackEnabled = MutableStateFlow(
        prefs?.getBoolean("haptic_feedback_enabled", true) ?: true
    )
    val hapticFeedbackEnabled: StateFlow<Boolean> = _hapticFeedbackEnabled.asStateFlow()

    private val _savedSearchQuery = MutableStateFlow("")
    val savedSearchQuery: StateFlow<String> = _savedSearchQuery.asStateFlow()

    private var pendingNotificationArticleId: String? = null
    private var progressUpdateJob: kotlinx.coroutines.Job? = null
    private var versionTapCount = 0
    private var lastTapTimestamp = 0L
    private var appNameTapCount = 0
    private var lastAppNameTapTimestamp = 0L

    init {
        context?.let { ctx ->
            NewsNotificationHelper.ensureNotificationChannels(ctx)
            if (_backgroundSyncPreferences.value.workManagerEnabled) {
                BackgroundSyncManager.schedulePeriodicSync(ctx)
            }
            if (_backgroundSyncPreferences.value.liveServiceEnabled) {
                BackgroundSyncManager.startLiveSyncService(ctx)
            }
        }

        // Start hourly automatic sync loop with real news outlets
        viewModelScope.launch {
            val initialArticles = repository.refresh()
            pendingNotificationArticleId?.let { pendingId ->
                val resolved = initialArticles.find { it.id == pendingId }
                if (resolved != null) {
                    val saved = savedArticleIds.value.contains(resolved.id)
                    _activeArticle.value = resolved.copy(isSaved = saved)
                    _currentScreen.value = AppScreen.DETAIL
                    pendingNotificationArticleId = null
                }
            }
            context?.let { ctx ->
                if (NewsNotificationHelper.hasNotificationPermission(ctx)) {
                    NewsNotificationHelper.dispatchAlertsForArticles(
                        ctx,
                        initialArticles,
                        _notificationPreferences.value
                    )
                }
            }

            while (true) {
                kotlinx.coroutines.delay(1000)
                val currentSec = _hourlyCountdownSeconds.value
                if (currentSec <= 1) {
                    _hourlyCountdownSeconds.value = 3600
                    val updated = repository.refresh()
                    context?.let { ctx ->
                        NewsNotificationHelper.dispatchAlertsForArticles(
                            ctx,
                            updated,
                            _notificationPreferences.value
                        )
                    }
                } else {
                    _hourlyCountdownSeconds.value = currentSec - 1
                }
            }
        }
    }

    val isRefreshing: StateFlow<Boolean> = repository.isRefreshing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val savedArticles: StateFlow<List<NewsArticle>> = repository.savedArticles.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredSavedArticles: StateFlow<List<NewsArticle>> = combine(
        savedArticles,
        _savedSearchQuery
    ) { saved, query ->
        if (query.isBlank()) {
            saved
        } else {
            val q = query.trim()
            saved.filter { article ->
                article.title.contains(q, ignoreCase = true) ||
                    article.summary.contains(q, ignoreCase = true) ||
                    article.source.contains(q, ignoreCase = true) ||
                    article.category.contains(q, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val savedArticleIds: StateFlow<List<String>> = repository.savedArticleIds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val personalizationState = combine(
        _activeFeedTab,
        _userFeedProfile,
        _favoriteBroadcasters
    ) { tab, profile, favs ->
        Triple(tab, profile, favs)
    }

    private val filterCriteriaState = combine(
        _selectedCategory,
        _selectedOutlet,
        _searchQuery,
        savedArticleIds
    ) { category, outlet, query, savedIds ->
        FeedFilterCriteria(category, outlet, query, savedIds.toSet())
    }

    val filteredFeedArticles: StateFlow<List<NewsArticle>> = combine(
        repository.liveArticles,
        personalizationState,
        filterCriteriaState
    ) { articles, (tab, profile, favs), filters ->
        val withSavedState = articles.map { it.copy(isSaved = filters.savedIds.contains(it.id)) }

        val curatedPool = when (tab) {
            FeedTab.MY_FEED -> NewsPersonalizationEngine.curateMyFeed(
                articles = withSavedState,
                profile = profile,
                favoriteBroadcasters = favs
            )
            FeedTab.WORLD -> withSavedState.map { it.copy(matchBadges = emptyList()) }
        }

        curatedPool.filter { article ->
            val matchesCategory = matchesFeedCategory(article, filters.category)

            val matchesOutlet = if (filters.outlet == null) {
                true
            } else {
                article.outletCode?.equals(filters.outlet, ignoreCase = true) == true ||
                    article.source.contains(filters.outlet, ignoreCase = true)
            }

            val matchesQuery = if (filters.query.isBlank()) {
                true
            } else {
                article.title.contains(filters.query, ignoreCase = true) ||
                    article.summary.contains(filters.query, ignoreCase = true) ||
                    article.source.contains(filters.query, ignoreCase = true)
            }

            matchesCategory && matchesOutlet && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun loadUserFeedProfile(): UserFeedProfile {
        val countryCode = prefs?.getString("profile_country_code", "US") ?: "US"
        val alignmentMode = try {
            val savedMode = prefs?.getString("profile_alignment_mode", AlignmentMode.NON_PARTISAN.name)
            if (savedMode != null) AlignmentMode.valueOf(savedMode) else AlignmentMode.NON_PARTISAN
        } catch (_: Exception) {
            AlignmentMode.NON_PARTISAN
        }
        val selectedParties = prefs?.getStringSet("profile_selected_parties", emptySet())?.toSet() ?: emptySet()
        val selectedSpectrum = prefs?.getString("profile_selected_spectrum", "Center") ?: "Center"
        val allSidesEnabled = prefs?.getBoolean("profile_all_sides", true) ?: true
        val defaultTopics = UserFeedProfile().selectedTopics
        val selectedTopics = prefs?.getStringSet(
            "profile_selected_topics",
            defaultTopics
        )?.toSet() ?: defaultTopics

        return UserFeedProfile(
            countryCode = countryCode,
            alignmentMode = alignmentMode,
            selectedParties = selectedParties,
            selectedSpectrum = selectedSpectrum,
            allSidesEnabled = allSidesEnabled,
            selectedTopics = selectedTopics
        )
    }

    private fun saveUserFeedProfile(profile: UserFeedProfile) {
        _userFeedProfile.value = profile
        prefs?.edit()?.apply {
            putString("profile_country_code", profile.countryCode)
            putString("profile_alignment_mode", profile.alignmentMode.name)
            putStringSet("profile_selected_parties", profile.selectedParties)
            putString("profile_selected_spectrum", profile.selectedSpectrum)
            putBoolean("profile_all_sides", profile.allSidesEnabled)
            putStringSet("profile_selected_topics", profile.selectedTopics)
            apply()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setActiveFeedTab(tab: FeedTab) {
        _activeFeedTab.value = tab
        prefs?.edit()?.putString("active_feed_tab", tab.name)?.apply()
    }

    fun setCountry(countryCode: String) {
        val current = _userFeedProfile.value
        val newCountry = PersonalizationCatalog.findCountry(countryCode)
        val retainedParties = current.selectedParties.intersect(newCountry.parties.toSet())
        saveUserFeedProfile(
            current.copy(
                countryCode = newCountry.code,
                selectedParties = retainedParties
            )
        )
    }

    fun setAlignmentMode(mode: AlignmentMode) {
        saveUserFeedProfile(_userFeedProfile.value.copy(alignmentMode = mode))
    }

    fun toggleParty(party: String) {
        val current = _userFeedProfile.value
        val updatedParties = current.selectedParties.toMutableSet().apply {
            if (contains(party)) remove(party) else add(party)
        }
        saveUserFeedProfile(
            current.copy(
                alignmentMode = AlignmentMode.PARTIES,
                selectedParties = updatedParties
            )
        )
    }

    fun setSpectrum(spectrum: String) {
        saveUserFeedProfile(
            _userFeedProfile.value.copy(
                alignmentMode = AlignmentMode.SPECTRUM,
                selectedSpectrum = spectrum
            )
        )
    }

    fun toggleAllSides() {
        val current = _userFeedProfile.value
        saveUserFeedProfile(current.copy(allSidesEnabled = !current.allSidesEnabled))
    }

    fun toggleTopic(topicId: String) {
        val current = _userFeedProfile.value
        val updated = current.selectedTopics.toMutableSet().apply {
            if (contains(topicId)) remove(topicId) else add(topicId)
        }
        saveUserFeedProfile(current.copy(selectedTopics = updated))
    }

    fun selectAllTopics() {
        val allIds = PersonalizationCatalog.topicOptions.map { it.id }.toSet()
        saveUserFeedProfile(_userFeedProfile.value.copy(selectedTopics = allIds))
    }

    fun clearAllTopics() {
        saveUserFeedProfile(_userFeedProfile.value.copy(selectedTopics = emptySet()))
    }

    private fun matchesFeedCategory(article: NewsArticle, category: String): Boolean {
        return when (category.uppercase()) {
            "ALL WIRES" -> true
            "TECH", "TECH & SCIENCE" -> article.category.equals("TECH", ignoreCase = true) ||
                article.category.equals("TECH & SCIENCE", ignoreCase = true) ||
                article.topicTags.contains("TECH_SCIENCE")
            "AI" -> article.category.equals("AI", ignoreCase = true) ||
                article.topicTags.contains("AI_ROBOTICS")
            "CARS" -> article.category.equals("CARS", ignoreCase = true) ||
                article.topicTags.contains("CARS_MOBILITY")
            "SPORTS" -> article.category.equals("SPORTS", ignoreCase = true) ||
                article.topicTags.contains("SPORTS")
            "ENTERTAINMENT" -> article.category.equals("ENTERTAINMENT", ignoreCase = true) ||
                article.topicTags.contains("GAMING_MEDIA") ||
                article.topicTags.contains("CULTURE_SPACE")
            "HEALTH" -> article.category.equals("HEALTH", ignoreCase = true) ||
                article.topicTags.contains("HEALTH_MEDICINE")
            "CLIMATE" -> article.category.equals("CLIMATE", ignoreCase = true) ||
                article.topicTags.contains("CLIMATE_DISASTERS")
            "ECONOMY" -> article.category.equals("ECONOMY", ignoreCase = true) ||
                article.topicTags.contains("ECONOMY_FINANCE")
            "PALESTINE & GAZA" -> article.category.equals("PALESTINE & GAZA", ignoreCase = true) ||
                article.topicTags.contains("HUMANITARIAN")
            "GLOBAL" -> article.category.equals("GLOBAL", ignoreCase = true) ||
                article.topicTags.contains("WORLD_POLITICS") ||
                article.topicTags.contains("NATIONAL_POLICY")
            else -> article.category.equals(category, ignoreCase = true)
        }
    }

    fun toggleWorkManagerSync() {
        val current = _backgroundSyncPreferences.value
        val next = current.copy(workManagerEnabled = !current.workManagerEnabled)
        _backgroundSyncPreferences.value = next
        prefs?.edit()?.putBoolean("bg_work_manager_enabled", next.workManagerEnabled)?.apply()
        context?.let { ctx ->
            if (next.workManagerEnabled) {
                BackgroundSyncManager.schedulePeriodicSync(ctx)
            } else {
                BackgroundSyncManager.cancelPeriodicSync(ctx)
            }
        }
    }

    fun toggleLiveSyncService() {
        val current = _backgroundSyncPreferences.value
        val next = current.copy(liveServiceEnabled = !current.liveServiceEnabled)
        _backgroundSyncPreferences.value = next
        prefs?.edit()?.putBoolean("bg_live_service_enabled", next.liveServiceEnabled)?.apply()
        context?.let { ctx ->
            if (next.liveServiceEnabled) {
                BackgroundSyncManager.startLiveSyncService(ctx)
            } else {
                BackgroundSyncManager.stopLiveSyncService(ctx)
            }
        }
    }

    fun toggleFavoriteBroadcaster(id: String) {
        val current = _favoriteBroadcasters.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _favoriteBroadcasters.value = current
        prefs?.edit()?.putStringSet("favorite_broadcasters", current)?.apply()
    }

    fun openArticle(article: NewsArticle) {
        _activeArticle.value = article
        _currentScreen.value = AppScreen.DETAIL
    }

    fun handleNotificationIntent(intent: android.content.Intent?) {
        val articleId = intent?.getStringExtra(NewsNotificationHelper.EXTRA_ARTICLE_ID)
            ?.takeIf { it.isNotBlank() } ?: return
        val fallbackArticle = NewsNotificationHelper.extractArticleFromIntent(intent)
        openArticleById(articleId, fallbackArticle)
    }

    fun openArticleById(articleId: String, fallbackArticle: NewsArticle? = null) {
        val isSaved = savedArticleIds.value.contains(articleId)
        val liveMatch = repository.findArticleById(articleId)
            ?: savedArticles.value.find { it.id == articleId }

        _showTutorial.value = false
        _isMarinOriginEasterEggVisible.value = false
        _isEasterEggVisible.value = false

        if (liveMatch != null) {
            pendingNotificationArticleId = null
            openArticle(liveMatch.copy(isSaved = isSaved))
            return
        }

        if (fallbackArticle != null) {
            openArticle(fallbackArticle.copy(isSaved = isSaved))
        }
        pendingNotificationArticleId = articleId

        viewModelScope.launch {
            val savedMatch = repository.findSavedArticleById(articleId)
            if (savedMatch != null && pendingNotificationArticleId == articleId) {
                pendingNotificationArticleId = null
                openArticle(savedMatch)
            }
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedOutlet(outlet: String?) {
        _selectedOutlet.value = outlet
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun resetFeedFilters() {
        _selectedCategory.value = "ALL WIRES"
        _selectedOutlet.value = null
        _searchQuery.value = ""
        _isSearching.value = false
    }

    fun setSavedSearchQuery(query: String) {
        _savedSearchQuery.value = query
    }

    fun cycleReaderTextScale() {
        val values = ReaderTextScale.entries
        val next = values[(values.indexOf(_readerTextScale.value) + 1) % values.size]
        _readerTextScale.value = next
        prefs?.edit()?.putString("reader_text_scale", next.name)?.apply()
    }

    fun setAppFontScale(scale: Float) {
        val clamped = ((scale.coerceIn(1.0f, 1.5f) * 100f).toInt() / 100f).coerceIn(1.0f, 1.5f)
        _appFontScale.value = clamped
        prefs?.edit()?.putFloat("app_font_scale", clamped)?.apply()
    }

    fun resetAppFontScale() {
        setAppFontScale(1.0f)
    }

    fun toggleHapticFeedback() {
        setHapticFeedbackEnabled(!_hapticFeedbackEnabled.value)
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        _hapticFeedbackEnabled.value = enabled
        prefs?.edit()?.putBoolean("haptic_feedback_enabled", enabled)?.apply()
    }

    fun toggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            _searchQuery.value = ""
        }
    }

    fun setDesignStyle(style: DesignStyle) {
        _designStyle.value = style
        prefs?.edit()?.putString("design_style", style.name)?.apply()
    }

    fun toggleNotification(type: String) {
        val current = _notificationPreferences.value
        val updated = when (type) {
            "natural_disasters" -> current.copy(naturalDisasters = !current.naturalDisasters)
            "humanitarian_crisis" -> current.copy(humanitarianCrisis = !current.humanitarianCrisis)
            "health_pandemic" -> current.copy(healthPandemic = !current.healthPandemic)
            "science_climate" -> current.copy(scienceClimate = !current.scienceClimate)
            "economy_energy" -> current.copy(economyEnergy = !current.economyEnergy)
            else -> current
        }
        _notificationPreferences.value = updated
        prefs?.edit()?.apply {
            putBoolean("notif_natural_disasters", updated.naturalDisasters)
            putBoolean("notif_humanitarian_crisis", updated.humanitarianCrisis)
            putBoolean("notif_health_pandemic", updated.healthPandemic)
            putBoolean("notif_science_climate", updated.scienceClimate)
            putBoolean("notif_economy_energy", updated.economyEnergy)
            apply()
        }
    }

    fun toggleDarkMode() {
        val next = !_isDarkMode.value
        _isDarkMode.value = next
        prefs?.edit()?.putBoolean("dark_mode", next)?.apply()
    }

    fun refreshFeed(notifyIfAllowed: Boolean = true) {
        viewModelScope.launch {
            val articles = repository.refresh()
            if (notifyIfAllowed) {
                context?.let { ctx ->
                    NewsNotificationHelper.dispatchAlertsForArticles(
                        ctx,
                        articles,
                        _notificationPreferences.value
                    )
                }
            }
        }
    }

    fun onNotificationPermissionGranted() {
        context?.let { ctx ->
            NewsNotificationHelper.sendLiveActivationNotification(ctx)
        }
    }

    fun sendTestNotification() {
        context?.let { ctx ->
            val currentArticles = filteredFeedArticles.value
            if (currentArticles.isNotEmpty()) {
                NewsNotificationHelper.dispatchAlertsForArticles(
                    ctx,
                    currentArticles,
                    _notificationPreferences.value,
                    forceAlertIfEmpty = true
                )
            } else {
                NewsNotificationHelper.sendLiveActivationNotification(ctx)
            }
        }
    }

    fun toggleSaveCurrentArticle(article: NewsArticle) {
        viewModelScope.launch {
            val isCurrentlySaved = savedArticleIds.value.contains(article.id)
            repository.toggleSaveArticle(article, isCurrentlySaved)
            if (_activeArticle.value?.id == article.id) {
                _activeArticle.value = _activeArticle.value?.copy(isSaved = !isCurrentlySaved)
            }
        }
    }

    fun updateReadingProgress(articleId: String, progress: Float) {
        val clamped = progress.coerceIn(0f, 1f)
        val currentActive = _activeArticle.value
        val nextProgress = if (currentActive?.id == articleId) {
            maxOf(currentActive.readingProgress, clamped)
        } else {
            clamped
        }
        if (currentActive?.id == articleId && nextProgress > currentActive.readingProgress) {
            _activeArticle.value = currentActive.copy(readingProgress = nextProgress)
        }
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            kotlinx.coroutines.delay(350L)
            repository.updateReadingProgress(articleId, nextProgress)
        }
    }

    fun deleteSavedArticle(id: String) {
        viewModelScope.launch {
            repository.deleteSaved(id)
            if (_activeArticle.value?.id == id) {
                _activeArticle.value = _activeArticle.value?.copy(isSaved = false)
            }
        }
    }

    fun clearAllSavedArticles() {
        viewModelScope.launch {
            repository.clearAllSaved()
            _savedSearchQuery.value = ""
            _activeArticle.value = _activeArticle.value?.copy(isSaved = false)
        }
    }

    fun triggerEasterEgg() {
        _isEasterEggVisible.value = true
    }

    fun dismissEasterEgg() {
        _isEasterEggVisible.value = false
    }

    fun onVersionTapped() {
        val now = System.currentTimeMillis()
        if (now - lastTapTimestamp < 800) {
            versionTapCount++
        } else {
            versionTapCount = 1
        }
        lastTapTimestamp = now

        if (versionTapCount >= 3) {
            versionTapCount = 0
            triggerEasterEgg()
        }
    }

    fun onAppNameTapped(nowMillis: Long = System.currentTimeMillis()) {
        if (lastAppNameTapTimestamp > 0L && nowMillis - lastAppNameTapTimestamp <= 450L) {
            appNameTapCount++
        } else {
            appNameTapCount = 1
        }
        lastAppNameTapTimestamp = nowMillis

        if (appNameTapCount >= 2) {
            appNameTapCount = 0
            lastAppNameTapTimestamp = 0L
            _isMarinOriginEasterEggVisible.value = true
        }
    }

    fun dismissMarinOriginEasterEgg() {
        _isMarinOriginEasterEggVisible.value = false
    }

    fun openTutorial(startStep: Int = 0) {
        _tutorialStep.value = startStep.coerceIn(0, TOTAL_TUTORIAL_STEPS - 1)
        _showTutorial.value = true
    }

    fun dismissTutorial() {
        _showTutorial.value = false
        _isUpgradingOldUser.value = false
        prefs?.edit()?.apply {
            putBoolean("has_completed_tutorial", true)
            putInt("tutorial_version_completed", CURRENT_TUTORIAL_VERSION)
            apply()
        }
    }

    fun nextTutorialStep() {
        if (_tutorialStep.value < TOTAL_TUTORIAL_STEPS - 1) {
            _tutorialStep.value += 1
        } else {
            dismissTutorial()
        }
    }

    fun prevTutorialStep() {
        if (_tutorialStep.value > 0) {
            _tutorialStep.value -= 1
        }
    }

    fun triggerManualSync() {
        _hourlyCountdownSeconds.value = 3600
        refreshFeed()
    }
}

private data class FeedFilterCriteria(
    val category: String,
    val outlet: String?,
    val query: String,
    val savedIds: Set<String>
)

class NewsViewModelFactory(
    private val repository: NewsRepository,
    private val context: android.content.Context? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

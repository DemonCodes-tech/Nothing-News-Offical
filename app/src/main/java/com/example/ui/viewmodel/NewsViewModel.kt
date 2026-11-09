package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.NewsArticle
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

    private val prefs = context?.getSharedPreferences("nothing_news_prefs", android.content.Context.MODE_PRIVATE)

    val categories = listOf(
        "ALL WIRES",
        "PALESTINE & GAZA",
        "GLOBAL",
        "TECH & SCIENCE",
        "ECONOMY"
    )

    private val _currentScreen = MutableStateFlow(AppScreen.FEED)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL WIRES")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _activeArticle = MutableStateFlow<NewsArticle?>(null)
    val activeArticle: StateFlow<NewsArticle?> = _activeArticle.asStateFlow()

    private val _designStyle = MutableStateFlow(
        try {
            val savedStyle = prefs?.getString("design_style", DesignStyle.NOTHING_OS.name)
            if (savedStyle != null) DesignStyle.valueOf(savedStyle) else DesignStyle.NOTHING_OS
        } catch (e: Exception) {
            DesignStyle.NOTHING_OS
        }
    )
    val designStyle: StateFlow<DesignStyle> = _designStyle.asStateFlow()

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

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isEasterEggVisible = MutableStateFlow(false)
    val isEasterEggVisible: StateFlow<Boolean> = _isEasterEggVisible.asStateFlow()

    // Automatically appears for new users who haven't completed onboarding
    private val _showTutorial = MutableStateFlow(
        prefs?.getBoolean("has_completed_tutorial", false)?.not() ?: true
    )
    val showTutorial: StateFlow<Boolean> = _showTutorial.asStateFlow()

    private val _tutorialStep = MutableStateFlow(0)
    val tutorialStep: StateFlow<Int> = _tutorialStep.asStateFlow()

    private val _hourlyCountdownSeconds = MutableStateFlow(3600)
    val hourlyCountdownSeconds: StateFlow<Int> = _hourlyCountdownSeconds.asStateFlow()

    val lastSyncTimeString: StateFlow<String> = repository.lastSyncTimeString.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "JUST NOW"
    )

    private var versionTapCount = 0
    private var lastTapTimestamp = 0L

    init {
        // Start hourly automatic sync loop with real news outlets
        viewModelScope.launch {
            // Initial sync on app start
            repository.refresh()

            while (true) {
                kotlinx.coroutines.delay(1000)
                val currentSec = _hourlyCountdownSeconds.value
                if (currentSec <= 1) {
                    _hourlyCountdownSeconds.value = 3600
                    repository.refresh()
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

    val savedArticleIds: StateFlow<List<String>> = repository.savedArticleIds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredFeedArticles: StateFlow<List<NewsArticle>> = combine(
        repository.liveArticles,
        _selectedCategory,
        _searchQuery,
        savedArticleIds
    ) { articles, category, query, savedIds ->
        val savedSet = savedIds.toSet()
        articles
            .map { it.copy(isSaved = savedSet.contains(it.id)) }
            .filter { article ->
                val matchesCategory = if (category == "ALL WIRES") {
                    true
                } else {
                    article.category.equals(category, ignoreCase = true)
                }

                val matchesQuery = if (query.isBlank()) {
                    true
                } else {
                    article.title.contains(query, ignoreCase = true) ||
                            article.summary.contains(query, ignoreCase = true) ||
                            article.source.contains(query, ignoreCase = true)
                }

                matchesCategory && matchesQuery
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openArticle(article: NewsArticle) {
        _activeArticle.value = article
        _currentScreen.value = AppScreen.DETAIL
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
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
        _isDarkMode.value = !_isDarkMode.value
    }

    fun refreshFeed() {
        viewModelScope.launch {
            repository.refresh()
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
        viewModelScope.launch {
            repository.updateReadingProgress(articleId, progress)
            if (_activeArticle.value?.id == articleId) {
                _activeArticle.value = _activeArticle.value?.copy(readingProgress = progress)
            }
        }
    }

    fun deleteSavedArticle(id: String) {
        viewModelScope.launch {
            repository.deleteSaved(id)
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

    fun openTutorial() {
        _tutorialStep.value = 0
        _showTutorial.value = true
    }

    fun dismissTutorial() {
        _showTutorial.value = false
        prefs?.edit()?.putBoolean("has_completed_tutorial", true)?.apply()
    }

    fun nextTutorialStep() {
        if (_tutorialStep.value < 4) {
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

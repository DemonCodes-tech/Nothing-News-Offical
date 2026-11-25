package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AlignmentMode
import com.example.data.model.BroadcasterRegistry
import com.example.data.model.NewsArticle
import com.example.data.model.NewsPersonalizationEngine
import com.example.data.model.UserFeedProfile
import com.example.data.notifications.NewsNotificationHelper
import com.example.data.repository.NewsRepository
import com.example.ui.components.CURRENT_RELEASE_VERSION
import com.example.ui.components.NEWSTHING_GITHUB_URL
import com.example.ui.components.NEWSTHING_RELEASE_LOG
import com.example.ui.components.marinColorFilter
import com.example.ui.theme.DesignStyle
import com.example.ui.theme.calculateDeviceProfile
import com.example.ui.viewmodel.NewsViewModel
import com.example.ui.viewmodel.NotificationPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context verifies NewsThing app name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NewsThing", appName)
  }

  @Test
  fun `broadcaster registry contains global channels including CNN and BBC`() {
    val cnn = BroadcasterRegistry.findBroadcaster("CNN")
    val bbc = BroadcasterRegistry.findBroadcaster("BBC")
    val dw = BroadcasterRegistry.findBroadcaster("DW")
    val sky = BroadcasterRegistry.findBroadcaster("Sky News")

    assertNotNull(cnn)
    assertNotNull(bbc)
    assertNotNull(dw)
    assertNotNull(sky)
    assertTrue(BroadcasterRegistry.allBroadcasters.size >= 50)
  }

  @Test
  fun `calculateDeviceProfile correctly differentiates compact phone and expanded tablet`() {
    val phoneProfile = calculateDeviceProfile(width = 390.dp, height = 840.dp, isNothing = true)
    val tabletProfile = calculateDeviceProfile(width = 900.dp, height = 1200.dp, isNothing = false)
    val coverProfile = calculateDeviceProfile(width = 320.dp, height = 480.dp, isNothing = true)

    assertTrue(phoneProfile.isCompact)
    assertEquals(false, phoneProfile.isTwoPane)
    assertEquals(10.dp, phoneProfile.contentPadding)
    assertEquals(10.dp, phoneProfile.cardPadding)
    assertEquals(8.dp, phoneProfile.itemSpacing)
    assertEquals(106.dp, phoneProfile.feedImageHeight)
    assertEquals(146.dp, phoneProfile.detailImageHeight)

    assertTrue(tabletProfile.isExpanded)
    assertTrue(tabletProfile.isTwoPane)

    assertTrue(coverProfile.isSmallScreen)
    assertEquals(8.dp, coverProfile.contentPadding)
    assertEquals(8.dp, coverProfile.cardPadding)
    assertEquals(94.dp, coverProfile.feedImageHeight)
  }

  @Test
  fun `tutorial appears by default for new users and remembers completion`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)
    
    // Clear state
    prefs.edit().clear().commit()

    // First time user check
    val isCompletedFirstTime = prefs.getBoolean("has_completed_tutorial", false)
    assertEquals(false, isCompletedFirstTime)

    // Mark completed
    prefs.edit().putBoolean("has_completed_tutorial", true).commit()

    // Returning user check
    val isCompletedReturning = prefs.getBoolean("has_completed_tutorial", false)
    assertEquals(true, isCompletedReturning)
  }

  @Test
  fun `notification preferences and design style persist correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)

    // Set custom notification preference for natural disasters and design style
    prefs.edit()
      .putBoolean("notif_natural_disasters", true)
      .putBoolean("notif_humanitarian_crisis", true)
      .putString("design_style", "MATERIAL_YOU")
      .commit()

    assertEquals(true, prefs.getBoolean("notif_natural_disasters", false))
    assertEquals(true, prefs.getBoolean("notif_humanitarian_crisis", false))
    assertEquals("MATERIAL_YOU", prefs.getString("design_style", "NOTHING_OS"))
  }

  @Test
  fun `marin step drawables and privacy card assets exist and load cleanly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val d1 = context.getDrawable(R.drawable.marin_step1)
    val d2 = context.getDrawable(R.drawable.marin_step2)
    val d3 = context.getDrawable(R.drawable.marin_step3)
    val d4 = context.getDrawable(R.drawable.marin_step4)
    val d5 = context.getDrawable(R.drawable.marin_step5)
    val dPrivacy = context.getDrawable(R.drawable.marin_privacy)
    val dContributors = context.getDrawable(R.drawable.marin_contributors)
    val dPersonalize = context.getDrawable(R.drawable.marin_personalize)
    val dBackgroundSync = context.getDrawable(R.drawable.marin_background_sync)
    val dRacingCategories = context.getDrawable(R.drawable.marin_racing_categories)
    val dOriginSecret = context.getDrawable(R.drawable.marin_origin_secret)
    val dUpdateWatch = context.getDrawable(R.drawable.marin_update_watch)
    val dCrown = context.getDrawable(R.drawable.ic_crown)
    val dPlanet = context.getDrawable(R.drawable.ic_planet)
    val dNotifIcon = context.getDrawable(R.drawable.ic_notification_wire)

    assertNotNull(d1)
    assertNotNull(d2)
    assertNotNull(d3)
    assertNotNull(d4)
    assertNotNull(d5)
    assertNotNull(dPrivacy)
    assertNotNull(dContributors)
    assertNotNull(dPersonalize)
    assertNotNull(dBackgroundSync)
    assertNotNull(dRacingCategories)
    assertNotNull(dOriginSecret)
    assertNotNull(dUpdateWatch)
    assertNotNull(dCrown)
    assertNotNull(dPlanet)
    assertNotNull(dNotifIcon)
    assertEquals("The Name Giver", context.getString(R.string.contributor_name_giver_role))
    assertEquals("u/Fall_Spezialgelagert", context.getString(R.string.contributor_name_giver_handle))
    assertEquals("\"Thanks Pookies!\"", context.getString(R.string.marin_contributors_thanks))
    assertEquals("Beta Testers", context.getString(R.string.contributors_beta_testers_heading_material))
    assertEquals("Beta Tester", context.getString(R.string.contributor_beta_tester_role))
    assertEquals("Tested early builds and helped polish NewsThing.", context.getString(R.string.contributor_beta_tester_desc))
    assertEquals("Adomio", context.getString(R.string.contributor_beta_tester_adomio))
    assertEquals("sharpxii_", context.getString(R.string.contributor_beta_tester_sharpxii))
    assertTrue(context.getString(R.string.marin_update_watch_note).contains("codename", ignoreCase = true))
  }

  @Test
  fun `notification helper creates all required channels and routes alerts by preference`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    NewsNotificationHelper.ensureNotificationChannels(context)

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    assertNotNull(manager.getNotificationChannel(NewsNotificationHelper.CHANNEL_DISASTERS))
    assertNotNull(manager.getNotificationChannel(NewsNotificationHelper.CHANNEL_HUMANITARIAN))
    assertNotNull(manager.getNotificationChannel(NewsNotificationHelper.CHANNEL_HEALTH))
    assertNotNull(manager.getNotificationChannel(NewsNotificationHelper.CHANNEL_WIRE))

    val quakeArticle = NewsArticle(
      id = "q1",
      title = "7.2 Magnitude Earthquake triggers tsunami advisory",
      summary = "Seismic agencies confirm alert.",
      content = "USGS and regional seismic monitoring agencies confirm offshore tremor.",
      source = "USGS",
      category = "NATURAL DISASTERS",
      timestampString = "1m ago",
      factScore = 99,
      primarySourcesCount = 3
    )
    val gazaArticle = NewsArticle(
      id = "g1",
      title = "Humanitarian aid corridor bulletin in Gaza",
      summary = "UNRWA & OCHA verify supply convoy.",
      content = "UNRWA and UN OCHA coordinate verified humanitarian supply dispatches.",
      source = "UN OCHA",
      category = "PALESTINE & GAZA",
      timestampString = "2m ago",
      factScore = 96,
      primarySourcesCount = 3
    )

    val disastersOnly = NotificationPreferences(
      naturalDisasters = true,
      humanitarianCrisis = false,
      healthPandemic = false,
      scienceClimate = false,
      economyEnergy = false
    )
    assertTrue(NewsNotificationHelper.matchesPreferences(quakeArticle, disastersOnly))
    assertFalse(NewsNotificationHelper.matchesPreferences(gazaArticle, disastersOnly))
  }

  @Test
  fun `favorite broadcasters persist in preferences`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)

    val favorites = setOf("cnn_int", "bbc_news", "dw", "al_jazeera_en")
    prefs.edit().putStringSet("favorite_broadcasters", favorites).commit()

    val saved = prefs.getStringSet("favorite_broadcasters", emptySet())
    assertNotNull(saved)
    assertEquals(4, saved?.size)
    assertTrue(saved?.contains("cnn_int") == true)
    assertTrue(saved?.contains("dw") == true)
  }

  @Test
  fun `github repository url and release log entries are configured`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertEquals("https://github.com/DemonCodes-tech/Newsthing", NEWSTHING_GITHUB_URL)
    assertEquals(NEWSTHING_GITHUB_URL, context.getString(R.string.github_repo_url))
    assertTrue(NEWSTHING_RELEASE_LOG.isNotEmpty())
    val latest = NEWSTHING_RELEASE_LOG.first()
    assertTrue(latest.isCurrent)
    assertEquals(CURRENT_RELEASE_VERSION, latest.version)
    assertEquals("v2.2.0", latest.version)
    assertTrue(latest.oldHighlights.any { it.contains("Nothing News", ignoreCase = true) })
    assertTrue(latest.newHighlights.any { it.contains("Light Mode", ignoreCase = true) })
    assertTrue(latest.newHighlights.any { it.contains("150%", ignoreCase = true) })
    assertTrue(latest.newHighlights.any { it.contains("Floating Material You Shapes", ignoreCase = true) })
    assertTrue(latest.newHighlights.any { it.contains("Pulsing Red Dots", ignoreCase = true) })
    assertTrue(latest.newHighlights.any { it.contains("Gentle Vibrations", ignoreCase = true) })
    assertTrue(latest.newHighlights.any { it.contains("Squiggling & Dot Progress Bars", ignoreCase = true) })
    assertTrue(latest.newHighlights.any { it.contains("Saved Library", ignoreCase = true) })
  }

  @Test
  fun `personalization engine ranks matching country party topics and preserves all-sides counterpoints`() {
    val demArticle = NewsArticle(
      id = "us-left",
      title = "Democratic Clean Energy Bill",
      summary = "Senate updates.",
      content = "Clean energy policy bill.",
      source = "AP",
      category = "ECONOMY",
      timestampString = "5m ago",
      factScore = 96,
      primarySourcesCount = 3,
      countryTags = listOf("US"),
      politicalLeaning = "Center-Left",
      partyTags = listOf("Democratic Party"),
      topicTags = listOf("ECONOMY_MARKETS", "NATIONAL_POLICY")
    )
    val gopArticle = NewsArticle(
      id = "us-right",
      title = "Republican Border & Tax Proposal",
      summary = "House proposal.",
      content = "Fiscal and border plan.",
      source = "Reuters",
      category = "GLOBAL",
      timestampString = "10m ago",
      factScore = 95,
      primarySourcesCount = 3,
      countryTags = listOf("US"),
      politicalLeaning = "Center-Right",
      partyTags = listOf("Republican Party"),
      topicTags = listOf("NATIONAL_POLICY")
    )
    val ukArticle = NewsArticle(
      id = "uk-1",
      title = "UK Parliament Transit Review",
      summary = "Westminster transit vote.",
      content = "Rail expansion review.",
      source = "BBC",
      category = "GLOBAL",
      timestampString = "20m ago",
      factScore = 98,
      primarySourcesCount = 3,
      countryTags = listOf("UK"),
      politicalLeaning = "Center",
      partyTags = listOf("Labour Party"),
      topicTags = listOf("LOCAL_CIVIC")
    )

    val profile = UserFeedProfile(
      countryCode = "US",
      alignmentMode = AlignmentMode.PARTIES,
      selectedParties = setOf("Democratic Party"),
      allSidesEnabled = true,
      selectedTopics = setOf("ECONOMY_MARKETS", "NATIONAL_POLICY")
    )

    val curated = NewsPersonalizationEngine.curateMyFeed(
      articles = listOf(ukArticle, gopArticle, demArticle),
      profile = profile,
      favoriteBroadcasters = emptySet()
    )

    assertEquals("us-left", curated.first().id)
    assertTrue(curated.first().matchBadges.contains("PARTY MATCH"))
    val rightArticle = curated.find { it.id == "us-right" }
    assertNotNull(rightArticle)
    assertTrue(rightArticle?.matchBadges?.contains("ALL-SIDES") == true)
  }

  @Test
  fun `returning user triggers v2 tutorial migration without losing old settings`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()

    // Simulate v2.1 returning user with saved settings & incomplete v2 tutorial version
    prefs.edit()
      .putBoolean("has_completed_tutorial", true)
      .putInt("tutorial_version_completed", 1)
      .putString("design_style", DesignStyle.MATERIAL_YOU.name)
      .putBoolean("dark_mode", false)
      .putStringSet("favorite_broadcasters", setOf("bbc_news", "dw"))
      .putBoolean("notif_natural_disasters", false)
      .commit()

    val database = com.example.data.local.AppDatabase.getDatabase(context)
    val vm = NewsViewModel(NewsRepository(database.articleDao()), context)

    assertEquals(9, NewsViewModel.TOTAL_TUTORIAL_STEPS)
    assertTrue(vm.showTutorial.value)
    assertTrue(vm.isUpgradingOldUser.value)
    assertEquals(5, vm.tutorialStep.value)
    assertEquals(DesignStyle.MATERIAL_YOU, vm.designStyle.value)
    assertFalse(vm.isDarkMode.value)
    assertTrue(vm.favoriteBroadcasters.value.contains("dw"))
    assertFalse(vm.notificationPreferences.value.naturalDisasters)

    vm.dismissTutorial()
    assertFalse(vm.showTutorial.value)
    assertEquals(NewsViewModel.CURRENT_TUTORIAL_VERSION, prefs.getInt("tutorial_version_completed", 0))
    assertEquals(DesignStyle.MATERIAL_YOU, vm.designStyle.value)
  }

  @Test
  fun `double tapping app name triggers Marin origin story easter egg`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val database = com.example.data.local.AppDatabase.getDatabase(context)
    val vm = NewsViewModel(NewsRepository(database.articleDao()), context)

    assertFalse(vm.isMarinOriginEasterEggVisible.value)

    // Single tap or slow taps should not open secret lore
    vm.onAppNameTapped(nowMillis = 1000L)
    assertFalse(vm.isMarinOriginEasterEggVisible.value)
    vm.onAppNameTapped(nowMillis = 1600L)
    assertFalse(vm.isMarinOriginEasterEggVisible.value)

    // Rapid second tap within 450ms opens Marin origin story card
    vm.onAppNameTapped(nowMillis = 1850L)
    assertTrue(vm.isMarinOriginEasterEggVisible.value)

    vm.dismissMarinOriginEasterEgg()
    assertFalse(vm.isMarinOriginEasterEggVisible.value)
  }

  @Test
  fun `marin color filter is monochrome in Nothing OS and null in Material You`() {
    assertNotNull(marinColorFilter(isNothing = true))
    assertNull(marinColorFilter(isNothing = false))
  }

  @Test
  fun `clicking article notification intent opens that exact story in detail screen`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val database = com.example.data.local.AppDatabase.getDatabase(context)
    val vm = NewsViewModel(NewsRepository(database.articleDao()), context)

    // Test with a baseline curated wire story
    val baselineLaunchIntent = NewsNotificationHelper.buildArticleLaunchIntent(
      context,
      NewsArticle(
        id = "wire-cnn-001",
        title = "CNN International: Multi-Agency Pacific Tsunami Warning Network Validates Sub-Sea Sensor Buoy Array",
        summary = "NOAA validates DART-4 buoys.",
        content = "Full content",
        source = "CNN International",
        category = "GLOBAL",
        timestampString = "28M AGO",
        factScore = 99,
        primarySourcesCount = 3,
        outletCode = "CNN"
      )
    )

    vm.handleNotificationIntent(baselineLaunchIntent)

    assertEquals(com.example.ui.viewmodel.AppScreen.DETAIL, vm.currentScreen.value)
    assertEquals("wire-cnn-001", vm.activeArticle.value?.id)
    assertFalse(vm.showTutorial.value)

    // Test with a dynamically fetched live story not in baseline cache
    val dynamicArticle = NewsArticle(
      id = "live-rss-999",
      title = "Breaking Live RSS Story",
      summary = "Urgent wire summary",
      content = "Detailed live wire body.",
      source = "Reuters",
      category = "GLOBAL",
      timestampString = "1M AGO",
      factScore = 97,
      primarySourcesCount = 3,
      outletCode = "REUTERS"
    )
    val dynamicLaunchIntent = NewsNotificationHelper.buildArticleLaunchIntent(context, dynamicArticle)
    vm.handleNotificationIntent(dynamicLaunchIntent)

    assertEquals(com.example.ui.viewmodel.AppScreen.DETAIL, vm.currentScreen.value)
    assertEquals("live-rss-999", vm.activeArticle.value?.id)
    assertEquals("Breaking Live RSS Story", vm.activeArticle.value?.title)
  }

  @Test
  fun `cleanRssText strips html tags and decodes named and numeric entities`() {
    val raw = "<p>UN&rsquo;s OCHA &amp; WHO report &#8217;record&#x2014;level&#8217;&nbsp;relief &hellip; &lt;urgent&gt;</p>"
    val cleaned = com.example.data.remote.LiveNewsFeedService.cleanRssText(raw)
    assertEquals("UN\u2019s OCHA & WHO report \u2019record\u2014level\u2019 relief \u2026 <urgent>", cleaned)
  }

  @Test
  fun `resetFeedFilters clears category outlet search query and searching state`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val database = com.example.data.local.AppDatabase.getDatabase(context)
    val vm = NewsViewModel(NewsRepository(database.articleDao()), context)

    vm.setCategory("ECONOMY")
    vm.setSelectedOutlet("BBC News")
    vm.toggleSearch()
    vm.setSearchQuery("central bank")

    assertEquals("ECONOMY", vm.selectedCategory.value)
    assertEquals("BBC News", vm.selectedOutlet.value)
    assertTrue(vm.isSearching.value)
    assertEquals("central bank", vm.searchQuery.value)

    vm.resetFeedFilters()

    assertEquals("ALL WIRES", vm.selectedCategory.value)
    assertNull(vm.selectedOutlet.value)
    assertFalse(vm.isSearching.value)
    assertEquals("", vm.searchQuery.value)
  }

  @Test
  fun `cycleReaderTextScale cycles and persists scale preference`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()

    val database = com.example.data.local.AppDatabase.getDatabase(context)
    val vm = NewsViewModel(NewsRepository(database.articleDao()), context)

    assertEquals(com.example.ui.viewmodel.ReaderTextScale.STANDARD, vm.readerTextScale.value)

    vm.cycleReaderTextScale()
    assertEquals(com.example.ui.viewmodel.ReaderTextScale.LARGE, vm.readerTextScale.value)
    assertEquals("LARGE", prefs.getString("reader_text_scale", null))

    // Recreate ViewModel to verify persistence
    val restoredVm = NewsViewModel(NewsRepository(database.articleDao()), context)
    assertEquals(com.example.ui.viewmodel.ReaderTextScale.LARGE, restoredVm.readerTextScale.value)

    restoredVm.cycleReaderTextScale()
    assertEquals(com.example.ui.viewmodel.ReaderTextScale.COMPACT, restoredVm.readerTextScale.value)

    restoredVm.cycleReaderTextScale()
    assertEquals(com.example.ui.viewmodel.ReaderTextScale.STANDARD, restoredVm.readerTextScale.value)
  }

  @Test
  fun `saved archive supports monotonic reading progress and deleteAllSaved`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val database = com.example.data.local.AppDatabase.getDatabase(context)
    val dao = database.articleDao()
    dao.deleteAllSaved()

    val savedItem = com.example.data.local.ArticleEntity.fromDomainModel(
      NewsArticle(
        id = "saved-1",
        title = "Saved Dispatch",
        summary = "Archive summary",
        content = "Archive full text",
        source = "Reuters",
        category = "GLOBAL",
        timestampString = "10m ago",
        factScore = 98,
        primarySourcesCount = 3,
        readingProgress = 0.65f
      )
    )
    dao.insert(savedItem)

    // Lower progress should NOT regress saved high-water progress
    dao.updateProgress("saved-1", 0.20f)
    assertEquals(0.65f, dao.getSavedById("saved-1")?.readingProgress ?: 0f, 0.001f)

    // Higher progress advances it
    dao.updateProgress("saved-1", 0.90f)
    assertEquals(0.90f, dao.getSavedById("saved-1")?.readingProgress ?: 0f, 0.001f)

    dao.deleteAllSaved()
    assertNull(dao.getSavedById("saved-1"))
  }

  @Test
  fun `notification history set is capped at 150 entries`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    org.robolectric.Shadows.shadowOf(app).grantPermissions(android.Manifest.permission.POST_NOTIFICATIONS)

    val prefs = app.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)
    val existingIds = (1..160).map { "old-id-$it" }.toSet()
    prefs.edit().putStringSet("notified_article_ids", existingIds).commit()

    val newArticle = NewsArticle(
      id = "new-quake-alert-999",
      title = "7.1 Earthquake recorded offshore",
      summary = "USGS tsunami advisory.",
      content = "Full seismic bulletin.",
      source = "USGS",
      category = "NATURAL DISASTERS",
      timestampString = "1m ago",
      factScore = 99,
      primarySourcesCount = 3
    )

    val dispatched = NewsNotificationHelper.dispatchAlertsForArticles(
      context = app,
      articles = listOf(newArticle),
      preferences = NotificationPreferences(naturalDisasters = true)
    )

    assertEquals(1, dispatched)
    val storedIds = prefs.getStringSet("notified_article_ids", emptySet()).orEmpty()
    assertTrue(storedIds.size <= 150)
    assertTrue(storedIds.contains("new-quake-alert-999"))
  }

  @Test
  fun `personalization catalog and My Feed curation support Sports, Tech, Cars, AI, and Gaming categories`() {
    val optionIds = com.example.data.model.PersonalizationCatalog.topicOptions.map { it.id }.toSet()
    assertTrue(optionIds.contains("SPORTS"))
    assertTrue(optionIds.contains("TECH_SCIENCE"))
    assertTrue(optionIds.contains("CARS_MOBILITY"))
    assertTrue(optionIds.contains("AI_ROBOTICS"))
    assertTrue(optionIds.contains("GAMING_MEDIA"))

    val sportsArticle = NewsArticle(
      id = "sport-1",
      title = "Championship Final Telemetry Verified",
      summary = "League match log.",
      content = "Full sports dispatch.",
      source = "BBC Sport",
      category = "SPORTS",
      timestampString = "5m ago",
      factScore = 99,
      primarySourcesCount = 3,
      countryTags = listOf("UK", "GLOBAL"),
      politicalLeaning = "Non-Partisan",
      topicTags = listOf("SPORTS")
    )
    val carsArticle = NewsArticle(
      id = "car-1",
      title = "Solid-State EV Track Range Audit",
      summary = "1050km WLTP.",
      content = "Full automotive dispatch.",
      source = "DW Mobility",
      category = "CARS",
      timestampString = "8m ago",
      factScore = 98,
      primarySourcesCount = 3,
      countryTags = listOf("DE", "GLOBAL"),
      politicalLeaning = "Non-Partisan",
      topicTags = listOf("CARS_MOBILITY", "TECH_SCIENCE")
    )
    val politicsArticle = NewsArticle(
      id = "pol-1",
      title = "Senate Appropriations Vote",
      summary = "Fiscal bill.",
      content = "Budget markup.",
      source = "Reuters",
      category = "ECONOMY",
      timestampString = "10m ago",
      factScore = 98,
      primarySourcesCount = 3,
      countryTags = listOf("US", "GLOBAL"),
      politicalLeaning = "Non-Partisan",
      topicTags = listOf("NATIONAL_POLICY")
    )

    val sportsCarsOnly = UserFeedProfile(
      countryCode = "US",
      alignmentMode = AlignmentMode.NON_PARTISAN,
      allSidesEnabled = true,
      selectedTopics = setOf("SPORTS", "CARS_MOBILITY")
    )

    val curated = NewsPersonalizationEngine.curateMyFeed(
      articles = listOf(politicsArticle, carsArticle, sportsArticle),
      profile = sportsCarsOnly,
      favoriteBroadcasters = emptySet()
    )

    assertEquals(2, curated.size)
    assertTrue(curated.any { it.id == "sport-1" })
    assertTrue(curated.any { it.id == "car-1" })
    assertFalse(curated.any { it.id == "pol-1" })
  }

  @Test
  fun `view model exposes Sports Tech Cars AI categories and persists topic selection`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()

    val database = com.example.data.local.AppDatabase.getDatabase(context)
    val vm = NewsViewModel(NewsRepository(database.articleDao()), context)

    assertTrue(vm.categories.containsAll(listOf("ALL WIRES", "SPORTS", "TECH", "CARS", "AI", "ENTERTAINMENT")))

    vm.clearAllTopics()
    assertTrue(vm.userFeedProfile.value.selectedTopics.isEmpty())

    vm.toggleTopic("SPORTS")
    vm.toggleTopic("CARS_MOBILITY")
    vm.toggleTopic("AI_ROBOTICS")
    assertEquals(setOf("SPORTS", "CARS_MOBILITY", "AI_ROBOTICS"), vm.userFeedProfile.value.selectedTopics)

    val restoredVm = NewsViewModel(NewsRepository(database.articleDao()), context)
    assertEquals(setOf("SPORTS", "CARS_MOBILITY", "AI_ROBOTICS"), restoredVm.userFeedProfile.value.selectedTopics)

    restoredVm.selectAllTopics()
    assertEquals(com.example.data.model.PersonalizationCatalog.topicOptions.size, restoredVm.userFeedProfile.value.selectedTopics.size)
  }

  @Test
  fun `material 3 expressive shapes build valid bounded paths and differentiate from Nothing shapes`() {
    val testSize = androidx.compose.ui.geometry.Size(120f, 120f)
    val flowerPath = com.example.ui.theme.buildOrganicFlowerPath(
      size = testSize,
      petals = 10,
      petalDepth = 0.20f,
      rotationDegrees = 45f
    )
    val squirclePath = com.example.ui.theme.buildScallopedSquirclePath(
      size = testSize,
      scallops = 8,
      scallopDepth = 0.11f,
      rotationDegrees = 30f
    )

    val flowerBounds = flowerPath.getBounds()
    val squircleBounds = squirclePath.getBounds()

    assertFalse(flowerPath.isEmpty)
    assertFalse(squirclePath.isEmpty)
    assertTrue(flowerBounds.width > 80f && flowerBounds.width <= 121f)
    assertTrue(flowerBounds.height > 80f && flowerBounds.height <= 121f)
    assertTrue(squircleBounds.width > 70f && squircleBounds.width <= 121f)
    assertTrue(squircleBounds.height > 70f && squircleBounds.height <= 121f)

    assertTrue(
      com.example.ui.theme.MaterialYouShapes.large != com.example.ui.theme.NothingShapes.large
    )
  }

  @Test
  fun `appFontScale clamps between 100 and 150 percent and persists across restarts`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()

    val database = com.example.data.local.AppDatabase.getDatabase(context)
    val vm = NewsViewModel(NewsRepository(database.articleDao()), context)

    // Default scale is 1.0f (100%)
    assertEquals(1.0f, vm.appFontScale.value, 0.001f)

    // Clamps below minimum and above maximum
    vm.setAppFontScale(0.70f)
    assertEquals(1.0f, vm.appFontScale.value, 0.001f)

    vm.setAppFontScale(1.85f)
    assertEquals(1.5f, vm.appFontScale.value, 0.001f)
    assertEquals(1.5f, prefs.getFloat("app_font_scale", 1.0f), 0.001f)

    // Persists intermediate scale cleanly
    vm.setAppFontScale(1.25f)
    assertEquals(1.25f, vm.appFontScale.value, 0.001f)

    val restoredVm = NewsViewModel(NewsRepository(database.articleDao()), context)
    assertEquals(1.25f, restoredVm.appFontScale.value, 0.001f)

    restoredVm.resetAppFontScale()
    assertEquals(1.0f, restoredVm.appFontScale.value, 0.001f)
    assertEquals(1.0f, prefs.getFloat("app_font_scale", 1.5f), 0.001f)
  }

  @Test
  fun `nothing light mode palette guarantees light surfaces and dark ink contrast`() {
    val lightBgLum = com.example.ui.theme.NothingSurfaceLight.luminance()
    val lightLowLum = com.example.ui.theme.NothingSurfaceLowLight.luminance()
    val lightHighLum = com.example.ui.theme.NothingSurfaceHighLight.luminance()
    val lightRedTintLum = com.example.ui.theme.NothingRedTintLight.luminance()
    val lightGreenTintLum = com.example.ui.theme.NothingGreenTintLight.luminance()
    val lightTextPrimaryLum = com.example.ui.theme.NothingTextPrimaryLight.luminance()
    val lightTextSecondaryLum = com.example.ui.theme.NothingTextSecondaryLight.luminance()

    // All Nothing Light Mode containers must stay bright so dark ink (#111111 / #555555) is clearly legible
    assertTrue(lightBgLum > 0.80f)
    assertTrue(lightLowLum > 0.75f)
    assertTrue(lightHighLum > 0.65f)
    assertTrue(lightRedTintLum > 0.70f)
    assertTrue(lightGreenTintLum > 0.70f)

    // Primary and secondary ink must stay dark with strong separation
    assertTrue(lightTextPrimaryLum < 0.05f)
    assertTrue(lightTextSecondaryLum < 0.15f)
    assertTrue((lightLowLum - lightTextPrimaryLum) > 0.70f)
  }

  @Test
  fun `all nine material you expressive shapes build bounded paths and morph smoothly`() {
    val shapes = com.example.ui.theme.ExpressiveMorphShape.entries
    assertEquals(9, shapes.size)
    val testSize = androidx.compose.ui.geometry.Size(140f, 140f)

    for (i in shapes.indices) {
      val from = shapes[i]
      val to = shapes[(i + 1) % shapes.size]
      val startPath = com.example.ui.theme.buildMorphingExpressivePath(testSize, from, to, 0f, 15f)
      val midPath = com.example.ui.theme.buildMorphingExpressivePath(testSize, from, to, 0.5f, 45f)
      val endPath = com.example.ui.theme.buildMorphingExpressivePath(testSize, from, to, 1f, 90f)

      assertFalse(startPath.isEmpty)
      assertFalse(midPath.isEmpty)
      assertFalse(endPath.isEmpty)

      val midBounds = midPath.getBounds()
      assertTrue(midBounds.left >= -1f && midBounds.right <= 141f)
      assertTrue(midBounds.top >= -1f && midBounds.bottom <= 141f)
      assertTrue(midBounds.width > 40f && midBounds.height > 40f)
    }
  }

  @Test
  fun `expressive roamer particle curves across viewport bounces off edges and advances morph cursor`() {
    val startParticle = com.example.ui.components.ExpressiveRoamerParticle(
      id = 1,
      xNorm = 0.93f,
      yNorm = 0.50f,
      vx = 0.09f,
      vy = 0.04f,
      sizeFraction = 0.35f,
      rotationDeg = 0f,
      rotationSpeedDeg = 20f,
      morphCursor = 0f,
      morphSpeed = 0.5f,
      curvePhase = 0f,
      curveFreq = 1.0f,
      colorSlot = 0,
      baseAlpha = 0.30f
    )

    // Step across the right boundary to confirm reflection back inward + morph advancement
    var p = startParticle
    repeat(10) {
      p = com.example.ui.components.stepExpressiveParticle(
        particle = p,
        dtSec = 0.04f,
        minNorm = 0.08f,
        maxNorm = 0.92f
      )
    }

    assertTrue(p.xNorm in 0.08f..0.92f)
    assertTrue(p.yNorm in 0.08f..0.92f)
    assertTrue("Particle velocity should reflect inward after crossing maxNorm", p.vx < 0f)
    assertTrue("Morph cursor should advance steadily", p.morphCursor > 0.15f)
    assertTrue("Rotation should advance smoothly", p.rotationDeg > 0f)
  }

  @Test
  fun `hapticFeedbackEnabled defaults to true toggles cleanly and persists across restarts`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()

    val database = com.example.data.local.AppDatabase.getDatabase(context)
    val vm = NewsViewModel(NewsRepository(database.articleDao()), context)

    assertTrue(vm.hapticFeedbackEnabled.value)

    vm.toggleHapticFeedback()
    assertFalse(vm.hapticFeedbackEnabled.value)
    assertFalse(prefs.getBoolean("haptic_feedback_enabled", true))

    val restoredVm = NewsViewModel(NewsRepository(database.articleDao()), context)
    assertFalse(restoredVm.hapticFeedbackEnabled.value)

    restoredVm.setHapticFeedbackEnabled(true)
    assertTrue(restoredVm.hapticFeedbackEnabled.value)
    assertTrue(prefs.getBoolean("haptic_feedback_enabled", false))
  }

  @Test
  fun `appHapticEngine respects enabled flag and triggers all cues across both design themes`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    var style = DesignStyle.NOTHING_OS
    var enabled = true

    val engine = com.example.ui.theme.AppHapticEngine(
      context = context,
      designStyleProvider = { style },
      enabledProvider = { enabled }
    )

    assertTrue(engine.isEnabled())
    com.example.ui.theme.HapticCue.entries.forEach { cue ->
      assertTrue(engine.trigger(cue))
    }
    assertEquals(com.example.ui.theme.HapticCue.entries.size, engine.triggerCount)
    assertEquals(com.example.ui.theme.HapticCue.UI_NAV, engine.lastTriggeredCue)

    style = DesignStyle.MATERIAL_YOU
    assertTrue(engine.trigger(com.example.ui.theme.HapticCue.SHAPE_HIT))
    assertEquals(com.example.ui.theme.HapticCue.SHAPE_HIT, engine.lastTriggeredCue)

    enabled = false
    assertFalse(engine.isEnabled())
    val countBeforeMuted = engine.triggerCount
    assertFalse(engine.trigger(com.example.ui.theme.HapticCue.UI_TAP))
    assertEquals(countBeforeMuted, engine.triggerCount)

    // Forced confirmation click when re-enabling works even before state updates
    assertTrue(engine.trigger(com.example.ui.theme.HapticCue.UI_TOGGLE, force = true))
    assertEquals(countBeforeMuted + 1, engine.triggerCount)
  }

  @Test
  fun `touching expressive roamer particle triggers hit detection, instant morph leap, and scatter bounce`() {
    val particle = com.example.ui.components.ExpressiveRoamerParticle(
      id = 2,
      xNorm = 0.50f,
      yNorm = 0.50f,
      vx = -0.05f,
      vy = -0.03f,
      sizeFraction = 0.36f,
      rotationDeg = 20f,
      rotationSpeedDeg = 12f,
      morphCursor = 1.2f,
      morphSpeed = 0.25f,
      curvePhase = 0.5f,
      curveFreq = 1.0f,
      colorSlot = 0,
      baseAlpha = 0.32f
    )

    assertTrue(
      com.example.ui.components.hitTestExpressiveParticle(
        particle = particle,
        touchXNorm = 0.51f,
        touchYNorm = 0.49f,
        viewportWidth = 1080f,
        viewportHeight = 2200f
      )
    )
    assertFalse(
      com.example.ui.components.hitTestExpressiveParticle(
        particle = particle,
        touchXNorm = 0.05f,
        touchYNorm = 0.05f,
        viewportWidth = 1080f,
        viewportHeight = 2200f
      )
    )

    val reacted = com.example.ui.components.applyShapeTouchReaction(
      particle = particle,
      touchXNorm = 0.46f,
      touchYNorm = 0.50f,
      nowNanos = 123_456L
    )

    assertEquals(1.0f, reacted.hitBoost, 0.001f)
    assertEquals(123_456L, reacted.lastHitNanos)
    assertTrue("Touch should advance morphCursor immediately", reacted.morphCursor > 2.0f)
    assertTrue("Touch from left should scatter particle rightward", reacted.vx > 0.05f)
  }

  @Test
  fun `nothing os red dot pulse stays bounded and intensifies along active touch ripples`() {
    val baseline = com.example.ui.components.computeNothingDotPulse(
      dotXNorm = 0.72f,
      dotYNorm = 0.50f,
      timeSec = 1.0f,
      ripples = emptyList()
    )
    val rippled = com.example.ui.components.computeNothingDotPulse(
      dotXNorm = 0.72f,
      dotYNorm = 0.50f,
      timeSec = 1.0f,
      ripples = listOf(com.example.ui.components.DotPulseRipple(xNorm = 0.50f, yNorm = 0.50f, ageSec = 0.25f))
    )

    assertTrue(baseline in 0f..1f)
    assertTrue(rippled in 0f..1f)
    assertTrue("Expanding Signal Red ripple should boost dot pulse intensity", rippled > baseline)
  }

  @Test
  fun `squiggle progress phase advances with scroll and wave stays bounded`() {
    val phaseAtTop = com.example.ui.components.computeSquigglePhaseRadians(
      scrollFraction = 0.0f,
      driftCycles = 0.0f,
      scrollWaveCycles = 5.0f
    )
    val phaseMidScroll = com.example.ui.components.computeSquigglePhaseRadians(
      scrollFraction = 0.35f,
      driftCycles = 0.0f,
      scrollWaveCycles = 5.0f
    )
    val phaseWithDrift = com.example.ui.components.computeSquigglePhaseRadians(
      scrollFraction = 0.35f,
      driftCycles = 0.25f,
      scrollWaveCycles = 5.0f
    )

    assertTrue("Scrolling down should advance squiggle wave phase", phaseMidScroll > phaseAtTop)
    assertTrue("Ambient drift should advance phase smoothly at rest", phaseWithDrift > phaseMidScroll)

    val centerY = 20f
    val amplitude = 6f
    val wavelength = 40f
    var minObserved = Float.MAX_VALUE
    var maxObserved = -Float.MAX_VALUE
    for (x in 0..240 step 2) {
      val y = com.example.ui.components.computeSquiggleWaveY(
        xPx = x.toFloat(),
        startXPx = 0f,
        centerY = centerY,
        amplitudePx = amplitude,
        wavelengthPx = wavelength,
        phaseRadians = phaseMidScroll
      )
      assertTrue(y in (centerY - amplitude - 0.01f)..(centerY + amplitude + 0.01f))
      if (y < minObserved) minObserved = y
      if (y > maxObserved) maxObserved = y
    }
    assertTrue("Wave should oscillate across crest and trough", (maxObserved - minObserved) > amplitude)
  }

  @Test
  fun `nothing os dot progress maps zero to all black and full progress to all red`() {
    assertEquals(0, com.example.ui.components.computeActiveDotCount(0f, 14))
    assertEquals(1, com.example.ui.components.computeActiveDotCount(0.02f, 14))
    assertEquals(7, com.example.ui.components.computeActiveDotCount(0.50f, 14))
    assertEquals(14, com.example.ui.components.computeActiveDotCount(1.0f, 14))
    assertEquals(14, com.example.ui.components.computeActiveDotCount(1.5f, 14))
    assertEquals(9, com.example.ui.components.computeActiveDotCount(9f / 9f, 9))
  }
}

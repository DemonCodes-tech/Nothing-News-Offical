package com.example.data.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.NewsArticle
import com.example.ui.viewmodel.NotificationPreferences

object NewsNotificationHelper {

    const val CHANNEL_DISASTERS = "newsthing_disasters"
    const val CHANNEL_HUMANITARIAN = "newsthing_humanitarian"
    const val CHANNEL_HEALTH = "newsthing_health"
    const val CHANNEL_WIRE = "newsthing_wire"

    const val ACTION_OPEN_ARTICLE = "com.example.action.OPEN_ARTICLE"
    const val EXTRA_ARTICLE_ID = "extra_article_id"
    const val EXTRA_ARTICLE_TITLE = "extra_article_title"
    const val EXTRA_ARTICLE_SUMMARY = "extra_article_summary"
    const val EXTRA_ARTICLE_CONTENT = "extra_article_content"
    const val EXTRA_ARTICLE_SOURCE = "extra_article_source"
    const val EXTRA_ARTICLE_CATEGORY = "extra_article_category"
    const val EXTRA_ARTICLE_TIMESTAMP = "extra_article_timestamp"
    const val EXTRA_ARTICLE_FACT_SCORE = "extra_article_fact_score"
    const val EXTRA_ARTICLE_SOURCES_COUNT = "extra_article_sources_count"
    const val EXTRA_ARTICLE_IMAGE_URL = "extra_article_image_url"
    const val EXTRA_ARTICLE_OUTLET_CODE = "extra_article_outlet_code"

    private const val PREFS_NAME = "nothing_news_prefs"
    private const val KEY_NOTIFIED_IDS = "notified_article_ids"
    private const val KEY_PROMPTED_ONCE = "has_prompted_notif_permission"
    private const val KEY_DISMISSED_PROMPT = "has_dismissed_notif_prompt"

    fun ensureNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_DISASTERS,
                    "Natural Disasters Abroad",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Earthquakes, tsunamis, volcanic eruptions, and cyclones."
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_HUMANITARIAN,
                    "Humanitarian & Gaza Crises",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "UN OCHA, UNRWA, and ICRC humanitarian crisis dispatches."
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_HEALTH,
                    "WHO Health Emergencies",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "World Health Organization outbreaks and public health alerts."
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_WIRE,
                    "Verified Global Wire Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "3-source audited science, climate, economy, and global dispatches."
                }
            )

            channels.forEach { manager.createNotificationChannel(it) }
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        val runtimeGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        return runtimeGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun requiresRuntimePermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    fun hasPromptedPermissionOnce(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_PROMPTED_ONCE, false)
    }

    fun markPromptedPermissionOnce(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_PROMPTED_ONCE, true)
            .apply()
    }

    fun hasDismissedStandalonePrompt(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DISMISSED_PROMPT, false)
    }

    fun dismissStandalonePrompt(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DISMISSED_PROMPT, true)
            .apply()
    }

    fun sendLiveActivationNotification(context: Context) {
        if (!hasNotificationPermission(context)) return
        ensureNotificationChannels(context)

        val contentIntent = createLaunchPendingIntent(context, 1001)
        val notification = NotificationCompat.Builder(context, CHANNEL_WIRE)
            .setSmallIcon(R.drawable.ic_notification_wire)
            .setContentTitle("NewsThing // Live Alerts Active")
            .setContentText("3-source verified global wire dispatches enabled. Zero data tracking.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Device notifications are active for earthquakes, tsunamis, humanitarian dispatches, and WHO alerts. All preferences stay 100% on your device."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        postSafely(context, 1001, notification)
    }

    fun dispatchAlertsForArticles(
        context: Context,
        articles: List<NewsArticle>,
        preferences: NotificationPreferences,
        forceAlertIfEmpty: Boolean = false
    ): Int {
        if (!hasNotificationPermission(context)) return 0
        ensureNotificationChannels(context)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val alreadyNotified = prefs.getStringSet(KEY_NOTIFIED_IDS, emptySet())?.toMutableList()
            ?: mutableListOf()

        val notifiedSet = alreadyNotified.toSet()
        val eligible = articles.filter { article ->
            !notifiedSet.contains(article.id) && matchesPreferences(article, preferences)
        }

        val toSend = eligible.take(2)
        if (toSend.isEmpty()) {
            if (forceAlertIfEmpty && articles.isNotEmpty()) {
                val topArticle = articles.firstOrNull { matchesPreferences(it, preferences) }
                    ?: articles.first()
                sendArticleNotification(context, topArticle, (topArticle.id.hashCode() and 0x7FFFFFFF) + 2000)
                return 1
            }
            return 0
        }

        toSend.forEach { article ->
            val notificationId = (article.id.hashCode() and 0x7FFFFFFF) + 2000
            sendArticleNotification(context, article, notificationId)
            if (!alreadyNotified.contains(article.id)) {
                alreadyNotified.add(article.id)
            }
        }

        val bounded = if (alreadyNotified.size > 150) {
            alreadyNotified.takeLast(150).toSet()
        } else {
            alreadyNotified.toSet()
        }
        prefs.edit().putStringSet(KEY_NOTIFIED_IDS, bounded).apply()
        return toSend.size
    }

    fun sendArticleNotification(
        context: Context,
        article: NewsArticle,
        notificationId: Int
    ) {
        if (!hasNotificationPermission(context)) return
        ensureNotificationChannels(context)

        val channelId = resolveChannelId(article)
        val outletLabel = article.outletCode ?: article.source
        val varianceTag = if (article.flaggedDiscrepancies.any { it.isContested }) " [CONTESTED CLAIM]" else ""
        val contentIntent = createArticlePendingIntent(context, article, notificationId)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification_wire)
            .setContentTitle("$outletLabel // ${article.category}$varianceTag")
            .setContentText(article.title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${article.title}\n\n${article.summary}\n(${article.factScore}% consensus across ${article.primarySourcesCount} verified sources)")
            )
            .setSubText("${article.factScore}% FACT • ${article.primarySourcesCount} SOURCES")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        postSafely(context, notificationId, notification)
    }

    fun matchesPreferences(article: NewsArticle, preferences: NotificationPreferences): Boolean {
        val text = "${article.title} ${article.summary} ${article.category}".lowercase()
        return when {
            article.category.equals("PALESTINE & GAZA", ignoreCase = true) ||
                text.contains("humanitarian") || text.contains("gaza") || text.contains("unrwa") -> {
                preferences.humanitarianCrisis
            }
            text.contains("earthquake") || text.contains("tsunami") ||
                text.contains("seismic") || text.contains("volcan") || text.contains("cyclone") -> {
                preferences.naturalDisasters
            }
            text.contains("who ") || text.contains("health") || text.contains("medical") ||
                text.contains("outbreak") || text.contains("pediatric") -> {
                preferences.healthPandemic
            }
            article.category.equals("TECH & SCIENCE", ignoreCase = true) -> {
                preferences.scienceClimate
            }
            article.category.equals("ECONOMY", ignoreCase = true) -> {
                preferences.economyEnergy
            }
            else -> preferences.naturalDisasters || preferences.humanitarianCrisis || preferences.healthPandemic
        }
    }

    private fun resolveChannelId(article: NewsArticle): String {
        val text = "${article.title} ${article.summary} ${article.category}".lowercase()
        return when {
            article.category.equals("PALESTINE & GAZA", ignoreCase = true) ||
                text.contains("humanitarian") || text.contains("gaza") -> CHANNEL_HUMANITARIAN
            text.contains("earthquake") || text.contains("tsunami") ||
                text.contains("seismic") || text.contains("cyclone") -> CHANNEL_DISASTERS
            text.contains("who ") || text.contains("health") || text.contains("medical") -> CHANNEL_HEALTH
            else -> CHANNEL_WIRE
        }
    }

    fun buildArticleLaunchIntent(context: Context, article: NewsArticle): Intent {
        return Intent(context, MainActivity::class.java).apply {
            action = "$ACTION_OPEN_ARTICLE.${article.id}"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_ARTICLE_ID, article.id)
            putExtra(EXTRA_ARTICLE_TITLE, article.title)
            putExtra(EXTRA_ARTICLE_SUMMARY, article.summary)
            putExtra(EXTRA_ARTICLE_CONTENT, article.content.take(4000))
            putExtra(EXTRA_ARTICLE_SOURCE, article.source)
            putExtra(EXTRA_ARTICLE_CATEGORY, article.category)
            putExtra(EXTRA_ARTICLE_TIMESTAMP, article.timestampString)
            putExtra(EXTRA_ARTICLE_FACT_SCORE, article.factScore)
            putExtra(EXTRA_ARTICLE_SOURCES_COUNT, article.primarySourcesCount)
            article.imageUrl?.let { putExtra(EXTRA_ARTICLE_IMAGE_URL, it) }
            article.outletCode?.let { putExtra(EXTRA_ARTICLE_OUTLET_CODE, it) }
        }
    }

    fun extractArticleFromIntent(intent: Intent?): NewsArticle? {
        val articleId = intent?.getStringExtra(EXTRA_ARTICLE_ID)?.takeIf { it.isNotBlank() } ?: return null
        val title = intent.getStringExtra(EXTRA_ARTICLE_TITLE) ?: return null
        val summary = intent.getStringExtra(EXTRA_ARTICLE_SUMMARY).orEmpty()
        val content = intent.getStringExtra(EXTRA_ARTICLE_CONTENT).orEmpty().ifBlank { summary }
        val source = intent.getStringExtra(EXTRA_ARTICLE_SOURCE) ?: "Verified Wire"
        val category = intent.getStringExtra(EXTRA_ARTICLE_CATEGORY) ?: "GLOBAL"
        val timestamp = intent.getStringExtra(EXTRA_ARTICLE_TIMESTAMP) ?: "LIVE"
        val factScore = intent.getIntExtra(EXTRA_ARTICLE_FACT_SCORE, 96)
        val sourcesCount = intent.getIntExtra(EXTRA_ARTICLE_SOURCES_COUNT, 3)
        val imageUrl = intent.getStringExtra(EXTRA_ARTICLE_IMAGE_URL)
        val outletCode = intent.getStringExtra(EXTRA_ARTICLE_OUTLET_CODE)

        return NewsArticle(
            id = articleId,
            title = title,
            summary = summary,
            content = content,
            source = source,
            category = category,
            timestampString = timestamp,
            factScore = factScore,
            primarySourcesCount = sourcesCount,
            imageUrl = imageUrl,
            outletCode = outletCode
        )
    }

    private fun createArticlePendingIntent(
        context: Context,
        article: NewsArticle,
        requestCode: Int
    ): PendingIntent {
        val intent = buildArticleLaunchIntent(context, article)
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createLaunchPendingIntent(context: Context, requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun postSafely(
        context: Context,
        notificationId: Int,
        notification: android.app.Notification
    ) {
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (_: SecurityException) {
            // Permission was revoked concurrently; ignore cleanly.
        }
    }
}

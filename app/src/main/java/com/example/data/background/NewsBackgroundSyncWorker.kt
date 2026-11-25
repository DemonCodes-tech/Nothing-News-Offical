package com.example.data.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.notifications.NewsNotificationHelper
import com.example.data.repository.NewsRepository
import com.example.ui.viewmodel.NotificationPreferences

class NewsBackgroundSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val dao = AppDatabase.getDatabase(applicationContext).articleDao()
            val repository = NewsRepository(dao)
            val articles = repository.refresh()

            val prefs = applicationContext.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)
            val notifPrefs = NotificationPreferences(
                naturalDisasters = prefs.getBoolean("notif_natural_disasters", true),
                humanitarianCrisis = prefs.getBoolean("notif_humanitarian_crisis", true),
                healthPandemic = prefs.getBoolean("notif_health_pandemic", true),
                scienceClimate = prefs.getBoolean("notif_science_climate", false),
                economyEnergy = prefs.getBoolean("notif_economy_energy", false)
            )

            NewsNotificationHelper.ensureNotificationChannels(applicationContext)
            if (NewsNotificationHelper.hasNotificationPermission(applicationContext)) {
                NewsNotificationHelper.dispatchAlertsForArticles(
                    context = applicationContext,
                    articles = articles,
                    preferences = notifPrefs
                )
            }

            prefs.edit().putLong("last_bg_sync_timestamp", System.currentTimeMillis()).apply()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}

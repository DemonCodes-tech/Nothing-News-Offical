package com.example.data.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.notifications.NewsNotificationHelper
import com.example.data.repository.NewsRepository
import com.example.ui.viewmodel.NotificationPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NewsLiveSyncService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var syncJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ensureLiveSyncChannel()
        val notification = buildForegroundNotification("Monitoring verified global & personal feeds")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        if (syncJob == null || syncJob?.isActive != true) {
            syncJob = serviceScope.launch {
                val dao = AppDatabase.getDatabase(applicationContext).articleDao()
                val repository = NewsRepository(dao)

                while (isActive) {
                    runCatching {
                        val articles = repository.refresh()
                        val prefs = applicationContext.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)
                        val notifPrefs = NotificationPreferences(
                            naturalDisasters = prefs.getBoolean("notif_natural_disasters", true),
                            humanitarianCrisis = prefs.getBoolean("notif_humanitarian_crisis", true),
                            healthPandemic = prefs.getBoolean("notif_health_pandemic", true),
                            scienceClimate = prefs.getBoolean("notif_science_climate", false),
                            economyEnergy = prefs.getBoolean("notif_economy_energy", false)
                        )
                        if (NewsNotificationHelper.hasNotificationPermission(applicationContext)) {
                            NewsNotificationHelper.dispatchAlertsForArticles(
                                context = applicationContext,
                                articles = articles,
                                preferences = notifPrefs
                            )
                        }
                        prefs.edit().putLong("last_bg_sync_timestamp", System.currentTimeMillis()).apply()
                    }
                    delay(15 * 60 * 1000L) // Refresh every 15 minutes while active
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        syncJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun ensureLiveSyncChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
            val channel = NotificationChannel(
                CHANNEL_LIVE_SYNC,
                "NewsThing Background Sync Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Persistent live wire synchronization status for NewsThing."
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(statusText: String) = NotificationCompat.Builder(this, CHANNEL_LIVE_SYNC)
        .setSmallIcon(R.drawable.ic_notification_wire)
        .setContentTitle("NewsThing // Live Background Sync")
        .setContentText(statusText)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                4040,
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build()

    companion object {
        const val CHANNEL_LIVE_SYNC = "newsthing_live_sync"
        private const val NOTIFICATION_ID = 4041
    }
}

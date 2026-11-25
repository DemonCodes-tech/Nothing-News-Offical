package com.example.data.background

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object BackgroundSyncManager {

    private const val PERIODIC_WORK_NAME = "newsthing_periodic_sync"

    fun schedulePeriodicSync(context: Context) {
        runCatching {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<NewsBackgroundSyncWorker>(
                30, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }

    fun cancelPeriodicSync(context: Context) {
        runCatching {
            WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME)
        }
    }

    fun startLiveSyncService(context: Context) {
        runCatching {
            val intent = Intent(context, NewsLiveSyncService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    fun stopLiveSyncService(context: Context) {
        runCatching {
            val intent = Intent(context, NewsLiveSyncService::class.java)
            context.stopService(intent)
        }
    }
}

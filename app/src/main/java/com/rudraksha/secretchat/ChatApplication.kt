package com.rudraksha.secretchat

import android.app.Application
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rudraksha.secretchat.core.ConnectionService
import com.rudraksha.secretchat.core.WebSocketWorker
import java.util.concurrent.TimeUnit

class ChatApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val serviceIntent = Intent(applicationContext, ConnectionService::class.java)
        applicationContext.startForegroundService(serviceIntent)

        // Ensure Work manager is scheduled only once
        if (!isWorkManagerScheduled()) {
            scheduleWorkManager()
        }
    }

    private fun isWorkManagerScheduled(): Boolean {
        val workManager = WorkManager.getInstance(this)
        val workInfos = workManager.getWorkInfosByTag("WebSocketWorkerConnection").get()
        return workInfos.any {
            it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING
        }
    }

    private fun scheduleWorkManager() {
        val workManager = WorkManager.getInstance(this)
        val workRequest = PeriodicWorkRequestBuilder<WebSocketWorker>(10, TimeUnit.MINUTES).build()

        workManager.enqueueUniquePeriodicWork(
            "WebSocketWorkerConnection",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
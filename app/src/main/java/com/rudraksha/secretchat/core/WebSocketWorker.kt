package com.rudraksha.secretchat.core

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters

class WebSocketWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {
    /**
     * A suspending method to do your work.
     *
     * <p>
     * To specify which [CoroutineDispatcher] your work should run on, use `withContext()` within
     * `doWork()`. If there is no other dispatcher declared, [Dispatchers.Default] will be used.
     *
     * <p>
     * A CoroutineWorker is given a maximum of ten minutes to finish its execution and return a
     * [ListenableWorker.Result]. After this time has expired, the worker will be signalled to stop.
     *
     * @return The [ListenableWorker.Result] of the result of the background work; note that
     *   dependent work will not execute if you return [ListenableWorker.Result.failure]
     */
    override suspend fun doWork(): Result {
        if (!isWorkerWorking()) {
            val serviceIntent = Intent(applicationContext, ConnectionService::class.java)
            applicationContext.startForegroundService(serviceIntent)
        }
        return Result.success()
    }

    private fun isWorkerWorking(): Boolean {
        val workManager = WorkManager.getInstance(applicationContext)
        val workInfos = workManager.getWorkInfosByTag("WebSocketWorkerConnection").get()
        return workInfos.any {
            it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING
        }
    }
}
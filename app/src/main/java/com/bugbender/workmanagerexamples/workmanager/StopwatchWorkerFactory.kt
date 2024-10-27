package com.bugbender.workmanagerexamples.workmanager

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.bugbender.workmanagerexamples.presentation.Stopwatch

class StopwatchWorkerFactory(
    private val stopwatch: Stopwatch,
    private val notificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManager,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return when (workerClassName) {
            StopwatchWorker::class.java.name -> StopwatchWorker(
                appContext,
                workerParameters,
                stopwatch,
                notificationBuilder,
                notificationManager
            )
            else -> throw IllegalStateException("No defined worker: $workerParameters")
        }
    }
}
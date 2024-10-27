package com.bugbender.workmanagerexamples

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Configuration
import com.bugbender.workmanagerexamples.presentation.Stopwatch
import com.bugbender.workmanagerexamples.presentation.StopwatchViewModel
import com.bugbender.workmanagerexamples.workmanager.StopwatchWorker.Companion.NOTIFICATION_CHANNEL_ID
import com.bugbender.workmanagerexamples.workmanager.StopwatchWorkerFactory

class App : Application(), Configuration.Provider {

    private val stopwatch = Stopwatch()
    private val notificationBuilder =
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
    private val notificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(
                StopwatchWorkerFactory(
                    stopwatch,
                    notificationBuilder,
                    notificationManager
                )
            )
            .build()

    lateinit var stopwatchViewModel: StopwatchViewModel

    override fun onCreate() {
        super.onCreate()
        stopwatchViewModel =
            StopwatchViewModel(stopwatch, this, notificationBuilder, notificationManager)
    }
}
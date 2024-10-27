package com.bugbender.workmanagerexamples.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.media.session.PlaybackState.ACTION_STOP
import android.os.Build
import android.util.Log
import android.view.textclassifier.SelectionEvent.ACTION_RESET
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bugbender.workmanagerexamples.R
import com.bugbender.workmanagerexamples.presentation.MainActivity
import com.bugbender.workmanagerexamples.presentation.Stopwatch

class StopwatchWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val stopwatch: Stopwatch,
    private val notificationBuilder: NotificationCompat. Builder,
    private val notificationManager: NotificationManager
) : CoroutineWorker(appContext, workerParams) {

    // Intents
    private val mainIntent = Intent(applicationContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    private val actionIntent =
        Intent(applicationContext, StopwatchActionReceiver::class.java).apply {
            action = ACTION_CLICK
        }
    private val resetIntent =
        Intent(applicationContext, StopwatchActionReceiver::class.java).apply {
            action = ACTION_RESET
        }

    private val mainPendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        mainIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for Android 12+
    )
    private val actionPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        0,
        actionIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    private val resetPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        0,
        resetIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())
        // Collect time updates and update notification
        stopwatch.timeFlow.collect { seconds ->
            notificationManager.notify(
                NOTIFICATION_ID, notificationBuilder.setContentText(seconds.toString()).build()
            )
        }
        return Result.success()
    }

    // Creates an instance of ForegroundInfo which can be used to update the ongoing notification.
    private fun createForegroundInfo(): ForegroundInfo {
        val title = applicationContext.getString(R.string.stopwatch)
        val  stop = applicationContext.getString(R.string.stop)
        val reset = applicationContext.getString(R.string.reset)

        // Create a Notification channel
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, title, importance)
        notificationManager.createNotificationChannel(channel)

        notificationBuilder.mActions?.clear()
        // Create Notification builder
        val notification = notificationBuilder
            .setContentTitle(title)
            .setContentText("0")
            .setSmallIcon(R.drawable.baseline_timer_24)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .addAction(0, stop, actionPendingIntent)
            .addAction(0, reset, resetPendingIntent)
            .setContentIntent(mainPendingIntent)
            .build()

        // Return ForegroundInfo
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ForegroundInfo(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    private fun setActionButtonTitle(@StringRes text: Int) {
        Log.d("k0dm", "not Empty ${notificationBuilder.mActions}")
        //notificationBuilder.mActions.removeAt(0)
        val action:  NotificationCompat. Action = notificationBuilder.mActions[0]
        action.title =  applicationContext.getString(text)
//        notificationBuilder.mActions.add(
//            0,
//            NotificationCompat.Action(
//                0,
//                applicationContext.getString(text),
//                actionPendingIntent
//            )
//        )
//        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }


    companion object {
        const val NOTIFICATION_ID = 1_000_000
        const val NOTIFICATION_CHANNEL_ID = "STOPWATCH_NOTIFICATION"
        const val ACTION_CLICK = "ACTION_CLICK"
        const val ACTION_RESET = "ACTION_RESET"
    }
}
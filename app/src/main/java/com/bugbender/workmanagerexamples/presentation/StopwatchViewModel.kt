package com.bugbender.workmanagerexamples.presentation

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bugbender.workmanagerexamples.R
import com.bugbender.workmanagerexamples.workmanager.StopwatchActionReceiver
import com.bugbender.workmanagerexamples.workmanager.StopwatchWorker
import com.bugbender.workmanagerexamples.workmanager.StopwatchWorker.Companion.ACTION_CLICK
import com.bugbender.workmanagerexamples.workmanager.StopwatchWorker.Companion.ACTION_RESET
import com.bugbender.workmanagerexamples.workmanager.StopwatchWorker.Companion.NOTIFICATION_CHANNEL_ID
import com.bugbender.workmanagerexamples.workmanager.StopwatchWorker.Companion.NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


class StopwatchViewModel(
    private val stopwatch: Stopwatch,
    private val application: Application,
    private val notificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManager
) : ViewModel() {

    private val _stopwatchLiveData: MutableLiveData<StopwatchState> =
        MutableLiveData(StopwatchState.Initial)
    val stopwatchLiveData: LiveData<StopwatchState> = _stopwatchLiveData

    private val workManager = WorkManager.getInstance(application)

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
            stopwatch.timeFlow
                .filter { it != 0 }
                .collect { time ->
                    _stopwatchLiveData.value = StopwatchState.Time(time.toString())
                }
        }
    }

    fun actionClick() {

        if (stopwatch.isRunning()) { //then stop the stopwatch
            _stopwatchLiveData.value = StopwatchState.Stop(stopwatch.timeFlow.value.toString())
            stopwatch.stop()
            setNotificationActionButtonTitle(R.string.resume) //bad
        } else {
            stopwatch.start()
            setNotificationActionButtonTitle(R.string.stop) //bad
            val stopwatchWorkRequest = OneTimeWorkRequestBuilder<StopwatchWorker>().build()
            workManager.enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.KEEP,
                stopwatchWorkRequest
            )
        }
    }

    fun reset() {
        _stopwatchLiveData.value = StopwatchState.Initial
        stopwatch.reset()
        workManager.cancelUniqueWork(WORK_NAME)
    }

    private fun setNotificationActionButtonTitle(@StringRes text: Int) {
        if (notificationBuilder.mActions.isNotEmpty()) {
            val action: NotificationCompat.Action = notificationBuilder.mActions[0]
            notificationBuilder.mActions[0] = NotificationCompat.Action(
                0,
                application.getString(text),
                action.getActionIntent()
            )

            notificationManager.notify(
                NOTIFICATION_ID, notificationBuilder.build()
            )
        }
    }

    companion object {
        const val WORK_NAME = "STOPWATCH_WORKER"
    }
}
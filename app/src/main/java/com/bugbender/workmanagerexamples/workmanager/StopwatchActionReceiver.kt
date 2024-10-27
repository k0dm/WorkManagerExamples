package com.bugbender.workmanagerexamples.workmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bugbender.workmanagerexamples.App

class StopwatchActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val app = context.applicationContext as App
        val viewModel = app.stopwatchViewModel

        when (intent?.action) {
            StopwatchWorker.ACTION_CLICK -> {
                viewModel.actionClick()
            }
            StopwatchWorker.ACTION_RESET -> {
                viewModel.reset()
            }
        }
    }
}
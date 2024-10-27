package com.bugbender.workmanagerexamples.presentation

import android.widget.Button
import android.widget.TextView
import com.bugbender.workmanagerexamples.R

interface StopwatchState {

    fun show(timeTextView: TextView, actionButton: Button, resetButton: Button)

    object Initial : StopwatchState {
        override fun show(timeTextView: TextView, actionButton: Button, resetButton: Button) {
            timeTextView.text = "0"
            actionButton.text = actionButton.context.getString(R.string.start)
        }
    }

    data class Stop(val value: String) : StopwatchState {
        override fun show(timeTextView: TextView, actionButton: Button, resetButton: Button) {
            timeTextView.text = value
            actionButton.text = actionButton.context.getString(R.string.resume)
        }
    }

    data class Time(val value: String) : StopwatchState {
        override fun show(timeTextView: TextView, actionButton: Button, resetButton: Button) {
            timeTextView.text = value
            actionButton.text = actionButton.context.getString(R.string.stop)
        }
    }
}
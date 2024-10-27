package com.bugbender.workmanagerexamples.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

class Stopwatch {

    private var isRunning: Boolean = false
    private var timer: Timer? = null
    private var seconds = 0

    private val _timeFlow = MutableStateFlow(0)
    val timeFlow: StateFlow<Int> = _timeFlow

    fun start() {
        isRunning = true
        timer = fixedRateTimer("stopwatch", false, 0, 1000) {
            seconds++
            _timeFlow.value = seconds
        }
    }

    fun stop() {
        isRunning = false
        timer?.cancel()
    }

    fun reset() {
        isRunning = false
        timer?.cancel()
        seconds = 0
    }

    fun isRunning() = isRunning
}
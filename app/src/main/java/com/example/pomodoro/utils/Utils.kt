package com.example.pomodoro.utils

private const val START_TIME = "00:00:00"
const val ACTION_START = "ACTION_START"
const val ACTION_STOP = "ACTION_STOP"
const val STARTED_TIMER_TIME_MS = "STARTED_TIMER_TIME"
const val STARTED_TIMER_BROADCAST_ACTION = "STARTED_TIMER_BROADCAST_ACTION"
const val COUNT_INTERVAL = 998L
const val NOTIFICATION_ID = 555

fun Long.displayTime(): String {
    if (this <= 0L) {
        return START_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60

    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}
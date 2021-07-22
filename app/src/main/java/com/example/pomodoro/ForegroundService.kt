package com.example.pomodoro

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.pomodoro.utils.*
import org.koin.android.ext.android.inject

class ForegroundService : Service() {
    private var isServiceStarted = false
    private val pomNotifications by inject<PomNotifications>()

    private var timer: CountDownTimer? = null

    var currentProgress = 0L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun processCommand(intent: Intent?) {
        when (intent?.action) {
            ACTION_START -> {
                currentProgress = intent.extras?.getLong(STARTED_TIMER_TIME_MS) ?: return
                commandStart()
            }
            ACTION_STOP -> {
                commandStop()
            }
        }
    }

    private fun commandStart() {
        if (isServiceStarted) {
            return
        }

        try {
            moveToStartedState()
        } finally {
            isServiceStarted = true
        }
        startForegroundAndShowNotification()
        continueTimer()
    }

    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        try {
            timer?.cancel()

            Intent().also { intent ->
                intent.action = STARTED_TIMER_BROADCAST_ACTION
                intent.putExtra(STARTED_TIMER_TIME_MS, currentProgress)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }

            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun continueTimer() {
        timer?.cancel()
        timer = getCountDownTimer()
        timer?.start()
    }

    private fun getCountDownTimer(): CountDownTimer {
        return object : CountDownTimer(currentProgress, COUNT_INTERVAL) {

            override fun onTick(millisUntilFinished: Long) {
                currentProgress = millisUntilFinished
                pomNotifications.refreshNotification(NOTIFICATION_ID, currentProgress.displayTime(), true)
            }

            override fun onFinish() {
                currentProgress = 0L
                pomNotifications.refreshNotification(NOTIFICATION_ID, "Timer is finished! \uD83D\uDE80", false)
            }
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        if (isServiceStarted) {
            val notification = pomNotifications.getNotification("content", true)
            startForeground(NOTIFICATION_ID, notification)
        }
    }
}
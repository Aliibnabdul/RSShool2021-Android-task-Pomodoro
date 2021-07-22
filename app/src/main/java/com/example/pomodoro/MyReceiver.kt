package com.example.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.pomodoro.utils.STARTED_TIMER_BROADCAST_ACTION
import com.example.pomodoro.utils.STARTED_TIMER_TIME_MS

class MyReceiver(private val receiverCallback: ReceiverCallback) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            STARTED_TIMER_BROADCAST_ACTION -> {
                val messageReceived = intent.getLongExtra(STARTED_TIMER_TIME_MS, 0L)
                Log.d("MyReceiver_TAG", "In Recieved : $messageReceived")
                receiverCallback.passValueToUI(messageReceived)
            }
        }
    }
}

interface ReceiverCallback {
    fun passValueToUI(passedValue: Long)
}
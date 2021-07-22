package com.example.pomodoro

interface StopwatchListener {
    fun startPause(id: Int, currentMs: Long)
    fun reset(id: Int)
    fun delete(id: Int)
}
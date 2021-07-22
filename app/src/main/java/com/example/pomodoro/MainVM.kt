package com.example.pomodoro

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pomodoro.data.Stopwatch
import com.example.pomodoro.utils.COUNT_INTERVAL
import com.example.pomodoro.utils.NOTIFICATION_ID
import com.example.pomodoro.utils.PomPreferences

class MainVM(
    private val prefs: PomPreferences,
    private val pomNotifications: PomNotifications,
) : ViewModel() {

    private val mutableItemsList = MutableLiveData(prefs.stopwatchListPref)
    val itemsLiveData: LiveData<List<Stopwatch>> get() = mutableItemsList

    init {
        val item = mutableItemsList.value?.find { it.isStarted }
        item?.let { startCountDownTimer(it) }
    }

    private var startedId: Int? = null

    private var timer: CountDownTimer? = null

    private fun getNextId(): Int {
        val lastId = mutableItemsList.value?.lastOrNull()?.id
        return lastId?.let { it + 1 } ?: 0
    }

    fun addItem(newDurationMinutes: Int) {
        val newList = mutableItemsList.value?.plus(
            Stopwatch(getNextId(), newDurationMinutes * 60 * 1000L, newDurationMinutes * 60 * 1000L, false)
        )
        mutableItemsList.value = newList
    }

    fun startStopTimer(id: Int, currentMs: Long) {
        val item = mutableItemsList.value?.find { it.id == id } ?: return

        if (item.currentMs == 0L) {
            resetTimer(id)
            return
        }

        val newList = mutableItemsList.value?.map {
            if (it.id == id) {
                stopCountDownTimer()
                val copy = it.copy(currentMs = currentMs, isStarted = !it.isStarted)
                if (copy.isStarted) startCountDownTimer(copy)
                copy
            } else {
                it.copy(isStarted = false)
            }
        }
        mutableItemsList.value = newList
    }

    fun resetTimer(id: Int) {
        val newList = mutableItemsList.value?.map {
            if (it.id == id) {
                if (it.isStarted) stopCountDownTimer()
                it.copy(currentMs = it.durationMs, isStarted = false)
            } else {
                it
            }
        }
        mutableItemsList.value = newList
    }

    fun deleteTimer(id: Int) {
        if (id == startedId) {
            stopCountDownTimer()
        }
        val item = mutableItemsList.value?.find { it.id == id } ?: return
        val newList = mutableItemsList.value?.minus(item) ?: return
        mutableItemsList.value = newList
    }

    private fun startCountDownTimer(stopwatch: Stopwatch) {
        timer = getCountDownTimer(stopwatch)
        timer?.start()
        startedId = stopwatch.id
    }

    private fun stopCountDownTimer() {
        timer?.cancel()
        startedId = null
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(stopwatch.currentMs, COUNT_INTERVAL) {

            override fun onTick(millisUntilFinished: Long) {
                postStopwatch(stopwatch.copy(currentMs = millisUntilFinished))
            }

            override fun onFinish() {
                postStopwatch(stopwatch.copy(currentMs = 0L, isStarted = false))
                pomNotifications.refreshNotification(NOTIFICATION_ID, "Timer is finished! 🚀", false)
            }
        }
    }

    private fun postStopwatch(stopwatch: Stopwatch) {
        val newList = mutableItemsList.value?.map {
            if (it.id == stopwatch.id) {
                stopwatch
            } else {
                it
            }
        } ?: return
        mutableItemsList.value = newList
    }

    fun setTimeFromService(startTime: Long) {
        stopCountDownTimer()
        val item = mutableItemsList.value?.find { it.isStarted } ?: return
        val copy = item.copy(currentMs = startTime, isStarted = startTime != 0L)
        if (startTime != 0L) {
            startCountDownTimer(copy)
        } else {
            postStopwatch(copy)
        }
    }

    fun onAppBackgrounded() {
        stopCountDownTimer()
    }

    override fun onCleared() {
        super.onCleared()
        prefs.stopwatchListPref = mutableItemsList.value ?: listOf()
    }
}
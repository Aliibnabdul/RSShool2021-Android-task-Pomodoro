package com.example.pomodoro

import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.ListAdapter
import com.example.pomodoro.data.Stopwatch
import com.example.pomodoro.databinding.StopwatchItemBinding
import com.example.pomodoro.utils.displayTime

class StopwatchAdapter(
    private val listener: StopwatchListener
) : ListAdapter<Stopwatch, StopwatchViewHolder>(Stopwatch.diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(layoutInflater, parent, false)
        return StopwatchViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int, payloads: MutableList<Any>) {
        val stopwatch = getItem(position)
        holder.stopwatch = stopwatch

        var fullRefresh = payloads.isEmpty()

        if (payloads.isNotEmpty()) {
            payloads.forEach { payload ->

                when (payload) {
                    Stopwatch.DURATION_CHANGED -> {
                        Log.d("StopwatchAdapter_TAG", "onBindViewHolder(payloads: $payloads) DURATION_CHANGED")
                        holder.customViewTwo.setPeriod(stopwatch.durationMs)
                    }

                    Stopwatch.CURRENT_CHANGED -> {
                        Log.d("StopwatchAdapter_TAG", "onBindViewHolder(payloads: $payloads) CURRENT_CHANGED")
                        holder.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                        holder.customViewTwo.setCurrent(stopwatch.currentMs)
                        if (!stopwatch.isStarted && stopwatch.currentMs != 0L) holder.container.setBackgroundResource(R.color.transparent)
                    }

                    Stopwatch.IS_STARTED_CHANGED -> {
                        Log.d("StopwatchAdapter_TAG", "onBindViewHolder(payloads: $payloads) IS_STARTED_CHANGED")
                        holder.customViewTwo.setCurrent(stopwatch.currentMs)
                        holder.stopwatchTimer.text = stopwatch.currentMs.displayTime()

                        if (stopwatch.currentMs == 0L) holder.container.setBackgroundResource(R.color.red)

                        if (stopwatch.isStarted) {
                            holder.startPauseButton.text = holder.container.context.getString(R.string.stop)
                            holder.blinkingIndicator.isInvisible = false
                            (holder.blinkingIndicator.background as? AnimationDrawable)?.start()
                        } else {
                            holder.startPauseButton.text = holder.container.context.getString(R.string.start)
                            holder.blinkingIndicator.isInvisible = true
                            (holder.blinkingIndicator.background as? AnimationDrawable)?.stop()
                        }
                    }
                    else -> fullRefresh = true
                }
            }
        }
        Log.d("StopwatchAdapter_TAG", "onBindViewHolder() fullRefresh: $fullRefresh")

        if (fullRefresh) {
            if (stopwatch.currentMs != 0L) holder.container.setBackgroundResource(R.color.transparent)
            else holder.container.setBackgroundResource(R.color.red)

            holder.customViewTwo.setPeriod(stopwatch.durationMs)
            holder.customViewTwo.setCurrent(stopwatch.currentMs)
            holder.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            if (stopwatch.isStarted) {
                holder.startPauseButton.text = holder.container.context.getString(R.string.stop)
                holder.blinkingIndicator.isInvisible = false
                (holder.blinkingIndicator.background as? AnimationDrawable)?.start()
            } else {
                holder.startPauseButton.text = holder.container.context.getString(R.string.start)
                holder.blinkingIndicator.isInvisible = true
                (holder.blinkingIndicator.background as? AnimationDrawable)?.stop()
            }
        }
    }
}
package com.example.pomodoro

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.data.Stopwatch
import com.example.pomodoro.databinding.StopwatchItemBinding

class StopwatchViewHolder(
    binding: StopwatchItemBinding,
    private val listener: StopwatchListener
) : RecyclerView.ViewHolder(binding.root) {

    val blinkingIndicator: ImageView = binding.blinkingIndicator
    val stopwatchTimer: TextView = binding.stopwatchTimer
    val customViewTwo: CustomView = binding.customViewTwo
    val startPauseButton: Button = binding.startPauseButton
    var stopwatch: Stopwatch? = null
    var container = binding.root

    init {
        binding.startPauseButton.setOnClickListener {
            stopwatch?.let { it1 -> listener.startPause(it1.id, it1.currentMs) }
        }
        binding.resetButton.setOnClickListener {
            stopwatch?.let { it1 -> listener.reset(it1.id) }
        }
        binding.deleteButton.setOnClickListener {
            stopwatch?.let { it1 -> listener.delete(it1.id) }
        }
    }
}
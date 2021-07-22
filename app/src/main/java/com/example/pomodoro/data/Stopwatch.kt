package com.example.pomodoro.data

import androidx.recyclerview.widget.DiffUtil
import kotlinx.serialization.Serializable

@Serializable
data class Stopwatch(
    val id: Int,
    val durationMs: Long,
    val currentMs: Long,
    val isStarted: Boolean
) {
    companion object {
        const val DURATION_CHANGED = 1
        const val CURRENT_CHANGED = 2
        const val IS_STARTED_CHANGED = 3

        val diffCallback = object : DiffUtil.ItemCallback<Stopwatch>() {
            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: Stopwatch, newItem: Stopwatch) =
                when {
                    oldItem.isStarted != newItem.isStarted -> {
                        IS_STARTED_CHANGED
                    }
                    oldItem.durationMs != newItem.durationMs -> {
                        DURATION_CHANGED
                    }
                    oldItem.currentMs != newItem.currentMs -> {
                        CURRENT_CHANGED
                    }
                    else -> null
                }
        }
    }
}

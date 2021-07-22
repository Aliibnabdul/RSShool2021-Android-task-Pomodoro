package com.example.pomodoro.utils

import android.content.Context
import com.example.pomodoro.data.Stopwatch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val USER_PREFERENCES_NAME = "user_preferences"
private const val STOPWATCH_LIST_PREF = "STOPWATCH_LIST_PREF"

class PomPreferences(context: Context) {
    private val mPrefs = context.applicationContext.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)

    var stopwatchListPref: List<Stopwatch>
        get() {
            val value = mPrefs.getString(STOPWATCH_LIST_PREF, null) ?: return listOf()
            return Json.decodeFromString(value)
        }
        set(value) {
            val str = Json.encodeToString(value)
            mPrefs.edit().putString(STOPWATCH_LIST_PREF, str).apply()
        }
}
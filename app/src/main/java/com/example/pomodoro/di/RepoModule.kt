package com.example.pomodoro.di

import com.example.pomodoro.PomNotifications
import com.example.pomodoro.utils.PomPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repoModule = module {
    single { PomPreferences(androidContext()) }
    single { PomNotifications(androidContext()) }
}

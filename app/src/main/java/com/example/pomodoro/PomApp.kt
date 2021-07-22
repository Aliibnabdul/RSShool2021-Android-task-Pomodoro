package com.example.pomodoro

import android.app.Application
import com.example.pomodoro.di.repoModule
import com.example.pomodoro.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PomApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PomApp)
            modules(repoModule, viewModelModule)
        }
    }
}
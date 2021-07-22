package com.example.pomodoro.di

import com.example.pomodoro.MainVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainVM(get(), get()) }
}
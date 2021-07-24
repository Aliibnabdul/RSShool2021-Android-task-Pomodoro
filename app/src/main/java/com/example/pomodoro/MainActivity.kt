package com.example.pomodoro

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.databinding.ActivityMainBinding
import com.example.pomodoro.utils.ACTION_START
import com.example.pomodoro.utils.ACTION_STOP
import com.example.pomodoro.utils.STARTED_TIMER_BROADCAST_ACTION
import com.example.pomodoro.utils.STARTED_TIMER_TIME_MS
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver, ReceiverCallback {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainVM by viewModel()
    private val stopwatchAdapter = StopwatchAdapter(this)
    private val intentFilter = IntentFilter()
    private val myReceiver = MyReceiver(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentFilter.addAction(STARTED_TIMER_BROADCAST_ACTION)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
            setHasFixedSize(true)
        }

        viewModel.itemsLiveData.observe(this, {
            stopwatchAdapter.submitList(it)
        })

        binding.addNewStopwatchButton.setOnClickListener {
            val newDurationMinutes = binding.etInput.text.toString().toIntOrNull()

            if (newDurationMinutes != null) {
                binding.etInput.error = null
                viewModel.addItem(newDurationMinutes)
            } else {
                binding.etInput.error = "Empty Field!"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter)
//
        val stopIntent = Intent(this, ForegroundService::class.java).apply {
            action = ACTION_STOP
        }
        startService(stopIntent)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)

        viewModel.onAppBackgrounded()

        val currentMs = viewModel.itemsLiveData.value?.find { it.isStarted }?.currentMs ?: return

        val startIntent = Intent(this, ForegroundService::class.java).apply {
            action = ACTION_START
            putExtra(STARTED_TIMER_TIME_MS, currentMs)
        }
        startService(startIntent)
    }

    override fun startPause(id: Int, currentMs: Long) {
        viewModel.startStopTimer(id, currentMs)
    }

    override fun reset(id: Int) {
        viewModel.resetTimer(id)
    }

    override fun delete(id: Int) {
        viewModel.deleteTimer(id)
    }

    override fun passValueToUI(passedValue: Long) {
        viewModel.setTimeFromService(passedValue)
    }
}
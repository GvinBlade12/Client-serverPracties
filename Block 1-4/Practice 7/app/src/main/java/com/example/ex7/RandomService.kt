package com.example.ex7

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlin.random.Random

class RandomService : Service() {

    private val binder = LocalBinder()
    private var isRunning = false
    private var listener: ((Int) -> Unit)? = null

    inner class LocalBinder : Binder() {
        fun getService(): RandomService = this@RandomService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun setListener(l: (Int) -> Unit) {
        listener = l
    }

    fun startGenerating() {
        if (isRunning) return

        isRunning = true

        Thread {
            while (isRunning) {
                val number = Random.nextInt(0, 101)
                listener?.invoke(number)
                Thread.sleep(1000)
            }
        }.start()
    }

    fun stopGenerating() {
        isRunning = false
    }
}
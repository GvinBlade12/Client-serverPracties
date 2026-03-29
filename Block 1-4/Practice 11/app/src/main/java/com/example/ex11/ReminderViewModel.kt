package com.example.ex11
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReminderViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("prefs", 0)
    private val scheduler = AlarmScheduler(app)

    private val _enabled = MutableStateFlow(prefs.getBoolean("enabled", false))
    val enabled: StateFlow<Boolean> = _enabled

    fun enableReminder() {
        scheduler.scheduleNextAlarm()
        prefs.edit().putBoolean("enabled", true).apply()
        _enabled.value = true
    }

    fun disableReminder() {
        scheduler.cancelAlarm()
        prefs.edit().putBoolean("enabled", false).apply()
        _enabled.value = false
    }
}
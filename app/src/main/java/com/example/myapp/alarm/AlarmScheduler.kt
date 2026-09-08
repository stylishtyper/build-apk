package com.example.myapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {

    const val ACTION_ALARM = "com.example.myapp.ACTION_ALARM_TRIGGER"
    private const val PREFS = "AlarmPrefs"

    data class AlarmConfig(val hour: Int, val minute: Int)

    fun getSavedAlarms(context: Context): List<AlarmConfig> {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return listOf(
            AlarmConfig(sp.getInt("h_0", 5), sp.getInt("m_0", 0)),
            AlarmConfig(sp.getInt("h_1", 9), sp.getInt("m_1", 0)),
            AlarmConfig(sp.getInt("h_2", 13), sp.getInt("m_2", 30)),
            AlarmConfig(sp.getInt("h_3", 17), sp.getInt("m_3", 30)),
            AlarmConfig(sp.getInt("h_4", 21), sp.getInt("m_4", 30))
        )
    }

    fun saveAlarmTime(context: Context, index: Int, hour: Int, minute: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt("h_$index", hour)
            .putInt("m_$index", minute)
            .apply()
    }

    fun scheduleAllAlarms(context: Context) {
        val alarms = getSavedAlarms(context)
        alarms.forEachIndexed { index, config ->
            scheduleSingleAlarm(context, index + 1, config.hour, config.minute)
        }
    }

    private fun scheduleSingleAlarm(context: Context, sessionId: Int, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM
            putExtra("SESSION_ID", sessionId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}

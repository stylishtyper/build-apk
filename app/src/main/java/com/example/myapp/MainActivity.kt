package com.example.myapp

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadApiKey()
        setupAlarmViews()

        binding.btnSave.setOnClickListener {
            saveApiKey()
            AlarmScheduler.scheduleAllAlarms(this)
            Toast.makeText(this, "সকল রিমাইন্ডার শিডিউল করা হয়েছে।", Toast.LENGTH_SHORT).show()
        }

        binding.btnTestReflection.setOnClickListener {
            val intent = Intent(this, ReflectionActivity::class.java).apply {
                putExtra("SESSION_ID", 1)
            }
            startActivity(intent)
        }
    }

    private fun loadApiKey() {
        val key = GeminiApiService.getApiKey(this)
        binding.etApiKey.setText(key)
    }

    private fun saveApiKey() {
        val key = binding.etApiKey.text?.toString().orEmpty()
        GeminiApiService.saveApiKey(this, key)
    }

    private fun setupAlarmViews() {
        binding.alarmsContainer.removeAllViews()
        val alarms = AlarmScheduler.getSavedAlarms(this)

        alarms.forEachIndexed { index, config ->
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_alarm_time, binding.alarmsContainer, false)
            val tvLabel = itemView.findViewById<TextView>(R.id.tvAlarmLabel)
            val tvTime = itemView.findViewById<TextView>(R.id.tvAlarmTime)

            tvLabel.text = "স্মরণ সেশন ${index + 1}"
            tvTime.text = formatTime(config.hour, config.minute)

            tvTime.setOnClickListener {
                TimePickerDialog(
                    this,
                    { _, selectedHour, selectedMinute ->
                        AlarmScheduler.saveAlarmTime(this, index, selectedHour, selectedMinute)
                        tvTime.text = formatTime(selectedHour, selectedMinute)
                    },
                    config.hour,
                    config.minute,
                    false
                ).show()
            }

            binding.alarmsContainer.addView(itemView)
        }
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm)
    }
}

package com.akhirah.reminder.ui

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.akhirah.reminder.R
import com.akhirah.reminder.data.database.StaticReminderDatabase
import com.akhirah.reminder.data.gemini.GeminiApiService
import com.akhirah.reminder.data.model.ReminderSessionData
import com.akhirah.reminder.databinding.ActivityReflectionBinding
import kotlinx.coroutines.launch

class ReflectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReflectionBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isAudioPlaying = false
    private var tasbihCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wakeAndUnlockScreen()

        binding = ActivityReflectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionId = intent.getIntExtra("SESSION_ID", 1)
        val sessionData = StaticReminderDatabase.getSessionById(sessionId)

        renderSession(sessionData)
        setupAudio(sessionData.ayah.audioUrl)
        setupTasbih(sessionData.hasTasbih)
        setupDonation(sessionData.hasDonation, sessionData.donationUrl)
        loadGeminiOrFallbackNews(sessionId, sessionData.fallbackNews)

        binding.btnClose.setOnClickListener {
            stopAudio()
            finish()
        }
    }

    private fun wakeAndUnlockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }

    private fun renderSession(session: ReminderSessionData) {
        // Ayah
        binding.tvAyahArabic.text = session.ayah.arabic
        binding.tvAyahBengali.text = session.ayah.bengali
        binding.tvAyahRef.text = session.ayah.reference

        // Hadith
        binding.tvHadithArabic.text = session.hadith.arabic
        binding.tvHadithBengali.text = session.hadith.bengali
        binding.tvHadithRef.text = session.hadith.reference
    }

    private fun setupAudio(audioUrl: String) {
        binding.btnPlayAudio.setOnClickListener {
            if (isAudioPlaying) {
                stopAudio()
            } else {
                startAudio(audioUrl)
            }
        }
    }

    private fun startAudio(url: String) {
        try {
            binding.btnPlayAudio.text = "লোড হচ্ছে..."
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    isAudioPlaying = true
                    binding.btnPlayAudio.text = getString(R.string.audio_pause)
                }
                setOnCompletionListener {
                    stopAudio()
                }
                setOnErrorListener { _, _, _ ->
                    stopAudio()
                    Toast.makeText(this@ReflectionActivity, "অডিও চালাতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        } catch (e: Exception) {
            stopAudio()
        }
    }

    private fun stopAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        isAudioPlaying = false
        binding.btnPlayAudio.text = getString(R.string.audio_play)
    }

    private fun setupTasbih(enabled: Boolean) {
        if (!enabled) {
            binding.layoutTasbih.visibility = View.GONE
            return
        }
        binding.layoutTasbih.visibility = View.VISIBLE
        binding.layoutTasbih.setOnClickListener {
            tasbihCount++
            binding.tvTasbihCounter.text = tasbihCount.toString()
        }
    }

    private fun setupDonation(enabled: Boolean, donationUrl: String) {
        if (!enabled) {
            binding.btnDonation.visibility = View.GONE
            return
        }
        binding.btnDonation.visibility = View.VISIBLE
        binding.btnDonation.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(donationUrl))
            startActivity(browserIntent)
        }
    }

    private fun loadGeminiOrFallbackNews(sessionId: Int, fallbackNews: String) {
        binding.tvNewsReflection.text = "সংবাদ ও আত্মচিন্তা প্রস্তুত হচ্ছে..."
        val geminiService = GeminiApiService(this)

        lifecycleScope.launch {
            val response = geminiService.fetchDailyReflection(sessionId)
            if (!response.isNullOrBlank()) {
                binding.tvNewsReflection.text = response
            } else {
                binding.tvNewsReflection.text = fallbackNews
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudio()
    }
}

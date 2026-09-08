package com.example.myapp.data.model

data class QuranAyah(
    val arabic: String,
    val bengali: String,
    val reference: String,
    val audioUrl: String
)

data class HadithContent(
    val arabic: String,
    val bengali: String,
    val reference: String
)

data class ReminderSessionData(
    val id: Int,
    val ayah: QuranAyah,
    val hadith: HadithContent,
    val fallbackNews: String,
    val hasTasbih: Boolean = true,
    val hasDonation: Boolean = false,
    val donationUrl: String = "https://assunnahfoundation.org"
)

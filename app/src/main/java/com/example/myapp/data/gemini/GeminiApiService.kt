package com.example.myapp

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiApiService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val PREFS_NAME = "AkhirahPrefs"
        private const val KEY_GEMINI_API = "gemini_api_key"

        fun saveApiKey(context: Context, key: String) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_GEMINI_API, key.trim())
                .apply()
        }

        fun getApiKey(context: Context): String {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_GEMINI_API, "") ?: ""
        }
    }

    suspend fun fetchDailyReflection(sessionIndex: Int): String? = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(context)
        if (apiKey.isBlank()) return@withContext null

        val promptText = """
আজকের তারিখ কত? এটা মাথায় রাখো। আমাকে বলা লাগবে না। আজকে বাংলাদেশের ছোট বড় জাতীয় দৈনিকগুলো অনুযায়ী কতজন মানুষ গত এক দিনের মধ্যে মৃত্যুবরণ করেছে? এটাও তুমি মাথায় রাখো। আমাকে বলা লাগবে না। এই মৃত্যুগুলোর মধ্যে কিছু আছে যা অন্যায় মৃত্যু বা খুন, যেগুলো এড়ানো সম্ভব ছিল। তুমি এগুলো আগেই বাদ দাও হিসাব থেকে। এখন বাকি থাকল এমন মৃত্যু যেগুলো স্বাভাবিক অথবা প্রাকৃতিক কারণে ঘটা অথবা দুর্ঘটনা অথবা এমন কিছু যার জন্য কাউকে দায়ী করা বা আদালতে মামলা করা যায় না। সড়ক দুর্ঘটনা, বিদ্যুৎস্পৃষ্ট হওয়া, পড়ে যাওয়া, ডুবে যাওয়া, আগুন লাগা ইত্যাদি আরো যত প্রকার আছে আমরা শুধু এগুলো নিয়ে আলোচনা করব। কারণ এগুলো সবাই রিলেট করতে পারবে এবং এগুলো যে কারো জীবনেই ঘটতে পারে। 

এখন তুমি সবচেয়ে রিলেটেবল ৫ টা ঘটনা আনবে। এমন ঘটনা যেগুলো সবাই খুব দ্রুত ইমোশনালি কানেক্ট করতে পারবে। যেমন অটোরিকশা ও বাসের সংঘর্ষে মৃত্যু বা বাসাবাড়ির বিদ্যুতের স্পর্শে মৃত্যু। শুধু সেইগুলো যেগুলো গতকাল ঘটেছে। যেন তুমি এভাবে বলতে পারো যে গতকাল দুপুর বেলায় এই এলাকায় একজন লোক অথবা অন্য যেকোনো ভাবে বলতে পারো। এভাবে ঘটনা বর্ণনা করবে। 

ঘটনা বর্ণনার মূল উদ্দেশ্য আমি যেন নিজের মৃত্যুর কথা স্মরণ করে আখিরাতমুখী জীবনযাপন করতে পারি। যেভাবে সে চলে গেল আমার সমাপ্তিও ঠিক এভাবেই ঠিক সেই সময়েই সেই জায়গাতেই হতে পারত। কিন্তু আমাকে আল্লাহ তায়ালা এখনও সুযোগের মধ্যে রেখেছেন। আমি যেন শুধু খবর পড়ে পৃষ্ঠা উল্টিয়ে চলে না যাই। আমি যেন চিন্তা করি। সময় নিয়ে গভীরভাবে বেশ কিছুক্ষণ চিন্তা করি। তার জায়গায় আমার না থাকার কোনো সম্ভাবনাই তো নেই। মৃত্যু শুধু অন্যদের জন্য না। এটা আমারও জন্য। তুমি তোমার লেখায় শুধু মৃত্যুর স্মরণ ও জীবনের স্বাদ বিনষ্টকারী মৃত্যুর স্মরণকে হাইলাইট কর। জান্নাত, আখিরাতের সুখ, এগুলো নিয়ে পরে কথা হবে। আগে আমি নিজের মস্তিষ্ককে বোঝাতে চাই যে মৃত্যু এবং আখিরাত কল্পনা নয় বাস্তব। 

ঘটনা বর্ণনার ক্ষেত্রে কিছু কথা শুনে রাখ। কখনো মূল বর্ণনার মধ্যে নাটকীয় সাহিত্যিক ভাষা ব্যবহার করবে না। অর্থাৎ মৃত্যুটা কীভাবে হলো বা মূল বৈষয়িক ঘটনা বর্ণনা যেন স্বাভাবিক এবং দৈনন্দিন কথ্য চলিত ভাষার মত হয়। যেমন- জীবন প্রদীপ নিভে গেল, ঘটনাস্থলেই স্তব্ধ হয়ে গেল তার জীবন। এমন কথা আমরা কখনো বাস্তব জীবনের কথ্য ভাষায় বলি না। যদি নিউজপেপারে থাকে তারপরও এমন ভাষা পরিহার করবে। আমি নিজের কাছে মৃত্যুকে বাস্তব হিসেবে বর্ণনা করতে চাই। সাহিত্য বা কল্পনা হিসেবে না। কিন্তু মৃত্যুর মূল বৈষয়িক ঘটনাটা যেটা নিউজপেপারে উল্লেখ আছে সেটা উল্লেখ করার পর রিফ্লেকশন, আত্মচিন্তা ইত্যাদি নিয়ে কথা বলবা। দুইটা অংশের বর্ণনার ভাষা আলাদা রাখবা। 

আরো একটা ব্যাপার মাথায় রাখো যে আমি কিন্ত তোমার পুরো রিপ্লাই একবারে পড়ব না। দিনের পাঁচটা ভিন্ন ভিন্ন সময়ে আমি তোমার বর্ণনা করা পাঁচটা ঘটনা পড়ব। তাই এমনভাবে লেখ যেন পাঁচটা আলাদা প্যারাগ্রাফ স্ট্যান্ডালোন হয়। অর্থাৎ এগুলো একইবারে একসাথে বর্ণনা করা কোনো ঘটনাগুচ্ছ নয়। একজন যদি যেকোনো একটা পড়ে তাহলেই তার আখিরাতমুখী রিমাইন্ডার হিসেবে এটা যথেষ্ট হবে। 

এবার লেখার আউটপুট ফরম্যাটিং নিয়ে কিছু কথা বলি শোন। একটা বাক্য বা রিলেটেড দুইটা বাক্যের পর একটা লাইন ফাঁকা রাখবা। যেন আমি এই লেখাগুলো সোশ্যাল মিডিয়ার মত করে পড়তে পারি, যেন বোরিং কোনো ফিকশন নভেল মনে না হয়। বাস্তব, দৈনন্দিন, স্বাভাবিক ঘটনা মনে হয় যার নেক্সট কোনো একটা চরিত্র আমি নিজেই। প্রতিটা ঘটনার সাথে নিউজপেপারের লিঙ্ক অবশ্যই দিয়ে দিবা।
        """.trimIndent()

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
            
            val jsonPayload = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", promptText) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val body = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val responseBody = response.body?.string() ?: return@withContext null
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null

            val text = candidates.getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            return@withContext text
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}

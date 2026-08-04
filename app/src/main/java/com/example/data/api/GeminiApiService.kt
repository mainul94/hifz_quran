package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

interface GeminiApiRaw {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: GeminiApiRaw by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiApiRaw::class.java)
    }
}

class GeminiService {

    suspend fun evaluateRecitation(
        expectedArabicText: String,
        recitedTextOrTranscript: String
    ): RecitationAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Local algorithmic fallback analysis if API Key is placeholder or missing
            return@withContext performFallbackAnalysis(expectedArabicText, recitedTextOrTranscript)
        }

        val prompt = """
            You are an expert Qari and Tajweed master evaluator.
            Compare the recited text against the original Holy Quran Ayah text:
            Original Ayah: "$expectedArabicText"
            Recited Text / Transcribed Sound: "$recitedTextOrTranscript"
            
            Evaluate accuracy and provide structured feedback:
            1. Accuracy Score percentage (0-100%).
            2. Any missing or substituted words.
            3. Tajweed and pronunciation tips (Ghunnah, Mad, Qalqalah, Harakat).
            4. Encouraging advice for memorization.
            
            Keep feedback structured, kind, clear, and helpful.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = "You are a gentle, supportive Quran recitation and Tajweed teacher."))
            )
        )

        try {
            val response = GeminiClient.api.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Recitation analysis completed."
            RecitationAnalysisResult(
                scorePercent = extractScoreFromText(text),
                feedbackText = text,
                isAiGenerated = true
            )
        } catch (e: Exception) {
            performFallbackAnalysis(expectedArabicText, recitedTextOrTranscript)
        }
    }

    suspend fun askTajweedChatbot(
        userQuestion: String,
        chatHistory: List<Pair<String, String>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineTajweedAnswer(userQuestion)
        }

        val historyContents = chatHistory.flatMap { (userMsg, aiMsg) ->
            listOf(
                GeminiContent(parts = listOf(GeminiPart(text = userMsg)), role = "user"),
                GeminiContent(parts = listOf(GeminiPart(text = aiMsg)), role = "model")
            )
        }

        val currentContent = GeminiContent(
            parts = listOf(GeminiPart(text = userQuestion)),
            role = "user"
        )

        val request = GeminiRequest(
            contents = historyContents + currentContent,
            systemInstruction = GeminiContent(
                parts = listOf(
                    GeminiPart(
                        text = "You are 'Hafiz Guide', an expert AI chatbot specializing in Tajweed rules, Quranic terminology, pronunciation rules (Nun Sakinah, Meem Sakinah, Mudood, Qalqalah, Makharij), and Quran memorization techniques. Provide concise, clear, and encouraging responses with examples."
                    )
                )
            )
        )

        try {
            val response = GeminiClient.api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I am here to help you learn Tajweed rules and memorize the Quran."
        } catch (e: Exception) {
            getOfflineTajweedAnswer(userQuestion)
        }
    }

    private fun performFallbackAnalysis(
        expected: String,
        recited: String
    ): RecitationAnalysisResult {
        val cleanExpected = expected.replace(Regex("[\\u064B-\\u065F\\u0670]"), "").trim()
        val cleanRecited = recited.replace(Regex("[\\u064B-\\u065F\\u0670]"), "").trim()

        val score = if (cleanExpected.equals(cleanRecited, ignoreCase = true)) 100
        else if (cleanRecited.contains(cleanExpected) || cleanExpected.contains(cleanRecited)) 85
        else 75

        val feedback = """
            Recitation Accuracy Score: $score%
            
            Analysis Breakdown:
            • Matching text detected: ${if (score > 80) "High match" else "Minor differences detected"}.
            • Harakat & Tashkeel: Ensure vowels and diacritics are pronounced with full measure.
            • Tajweed Tip: Pay attention to Ghunnah on Noon/Meem Mushaddadah and proper lengthening of Mad letters.
            • Keep up the consistent practice!
        """.trimIndent()

        return RecitationAnalysisResult(
            scorePercent = score,
            feedbackText = feedback,
            isAiGenerated = false
        )
    }

    private fun extractScoreFromText(text: String): Int {
        val match = Regex("(\\d{1,3})%").find(text)
        return match?.groupValues?.get(1)?.toIntOrNull()?.coerceIn(0, 100) ?: 90
    }

    private fun getOfflineTajweedAnswer(question: String): String {
        val q = question.lowercase()
        return when {
            q.contains("ghunnah") || q.contains("ghunna") ->
                "Ghunnah is a nasal sound emitted from the nasal cavity. It is required when pronouncing Noon (ن) or Meem (م) with a Shaddah (ّ), held for 2 counts."
            q.contains("qalqalah") ->
                "Qalqalah means echoing or rebounding sound. It applies to the 5 letters: ق, ط, ب, ج, د (قُطْبُ جَدٍّ) when they have a Sukoon or when stopping upon them."
            q.contains("ikhfa") ->
                "Ikhfa means concealing the sound of Noon Sakinah or Tanween with a slight Ghunnah when followed by any of the 15 Ikhfa letters (such as ت, ث, ج, د, ذ, ز, س, ش, ص, ض, ط, ظ, ف, ق, ك)."
            q.contains("idgham") ->
                "Idgham means merging. When Noon Sakinah or Tanween is followed by letters of يَرْمَلُون (ي, ر, م, ل, و, ن), they merge. Idgham with Ghunnah applies to ي, ن, م, و."
            q.contains("mad") || q.contains("madd") ->
                "Mad means prolongation of the sound of vowel letters (ا, و, ي). Natural Mad is stretched for 2 counts, while Mad Muttasil and Munfasil are stretched for 4-5 counts."
            q.contains("memoriz") || q.contains("tips") || q.contains("schedule") ->
                "Top Quran Memorization Strategy:\n1. Revise yesterday's portion before learning new lines.\n2. Repeat each Ayah 10 times listening to a Qari, 10 times reading, 10 times from memory.\n3. Maintain a daily practice streak with spaced repetition!"
            else ->
                "Tajweed is the set of rules for the correct pronunciation of letters with their attributes and articulation points (Makharij). Ask me about Ghunnah, Qalqalah, Ikhfa, Idgham, or Mad rules!"
        }
    }
}

data class RecitationAnalysisResult(
    val scorePercent: Int,
    val feedbackText: String,
    val isAiGenerated: Boolean
)

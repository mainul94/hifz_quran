package com.example.data.model

data class Reciter(
    val id: String,
    val name: String,
    val englishName: String,
    val style: String = "Murattal",
    val audioBaseUrl: String
)

val DEFAULT_RECITERS = listOf(
    Reciter(
        id = "mishary",
        name = "مشاري راشد العفاسي",
        englishName = "Mishary Rashid Alafasy",
        audioBaseUrl = "https://server8.mp3quran.net/afs/"
    ),
    Reciter(
        id = "abdulbasit",
        name = "عبد الباسط عبد الصمد",
        englishName = "Abdul Basit Abdul Samad",
        audioBaseUrl = "https://server7.mp3quran.net/basit/"
    ),
    Reciter(
        id = "husary",
        name = "محمود خليل الحصري",
        englishName = "Mahmoud Khalil Al-Husary",
        audioBaseUrl = "https://server13.mp3quran.net/hssrs/"
    ),
    Reciter(
        id = "ghamdi",
        name = "سعد الغامدي",
        englishName = "Saad Al-Ghamdi",
        audioBaseUrl = "https://server7.mp3quran.net/s_gmd/"
    ),
    Reciter(
        id = "shatri",
        name = "أبو بكر الشاطري",
        englishName = "Abu Bakr Al-Shatri",
        audioBaseUrl = "https://server11.mp3quran.net/shatri/"
    )
)

data class Surah(
    val id: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val translatedName: String,
    val verseCount: Int,
    val revelationType: String,
    val juzNumber: Int
)

data class Word(
    val position: Int,
    val arabicText: String,
    val transliteration: String,
    val translation: String
)

data class Ayah(
    val id: Int,
    val surahId: Int,
    val ayahNumber: Int,
    val arabicText: String,
    val englishText: String,
    val transliteration: String,
    val words: List<Word> = emptyList(),
    val audioUrl: String = ""
)

enum class MemorizationStatus(val label: String, val weight: Int) {
    NEW("New", 0),
    REVIEWING("Reviewing", 1),
    MEMORIZED("Memorized", 2),
    MASTERED("Mastered", 3)
}

data class LeaderboardUser(
    val id: String,
    val rank: Int,
    val name: String,
    val points: Int,
    val streakDays: Int,
    val versesMemorized: Int,
    val badgeName: String,
    val isCurrentUser: Boolean = false
)

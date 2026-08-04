package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.DailyStreakEntity
import com.example.data.db.MemorizationProgressEntity
import com.example.data.db.UserProfileEntity
import com.example.data.db.VoiceNoteEntity
import com.example.data.model.Ayah
import com.example.data.model.LeaderboardUser
import com.example.data.model.MemorizationStatus
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.quran.QuranDataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuranRepository {
    fun getAllSurahs(): List<Surah> = QuranDataProvider.ALL_SURAHS

    fun getAyahsForSurah(surahId: Int): List<Ayah> = QuranDataProvider.getAyahsForSurah(surahId)

    fun getAudioUrl(surahId: Int, ayahNumber: Int, reciterId: String): String {
        return QuranDataProvider.getAudioUrlForAyah(surahId, ayahNumber, reciterId)
    }
}

class MemorizationRepository(private val database: AppDatabase) {

    private val progressDao = database.memorizationDao()
    private val voiceNoteDao = database.voiceNoteDao()
    private val streakDao = database.dailyStreakDao()

    val allProgress: Flow<List<MemorizationProgressEntity>> = progressDao.getAllProgress()
    val allVoiceNotes: Flow<List<VoiceNoteEntity>> = voiceNoteDao.getAllVoiceNotes()
    val userProfile: Flow<UserProfileEntity?> = streakDao.getUserProfile()
    val allStreaks: Flow<List<DailyStreakEntity>> = streakDao.getAllStreaks()

    fun getSurahProgress(surahId: Int): Flow<List<MemorizationProgressEntity>> =
        progressDao.getSurahProgress(surahId)

    fun getDueReviews(currentTime: Long = System.currentTimeMillis()): Flow<List<MemorizationProgressEntity>> =
        progressDao.getDueReviews(currentTime)

    suspend fun updateAyahStatus(surahId: Int, ayahNumber: Int, status: MemorizationStatus) {
        val key = "${surahId}_${ayahNumber}"
        val now = System.currentTimeMillis()

        // Spaced repetition interval calculator
        val nextReviewDays = when (status) {
            MemorizationStatus.NEW -> 1
            MemorizationStatus.REVIEWING -> 3
            MemorizationStatus.MEMORIZED -> 7
            MemorizationStatus.MASTERED -> 30
        }
        val nextReviewTimestamp = now + (nextReviewDays * 24 * 60 * 60 * 1000L)

        val progress = MemorizationProgressEntity(
            ayahKey = key,
            surahId = surahId,
            ayahNumber = ayahNumber,
            status = status.name,
            lastReviewed = now,
            nextReviewDate = nextReviewTimestamp,
            repetitionCount = 1
        )
        progressDao.insertOrUpdate(progress)
        recordDailyPractice(1)
    }

    suspend fun saveVoiceNote(
        surahId: Int,
        ayahNumber: Int,
        title: String,
        filePath: String,
        durationMs: Long,
        notes: String
    ) {
        val entity = VoiceNoteEntity(
            surahId = surahId,
            ayahNumber = ayahNumber,
            title = title,
            filePath = filePath,
            durationMs = durationMs,
            timestamp = System.currentTimeMillis(),
            notes = notes
        )
        voiceNoteDao.insert(entity)
    }

    suspend fun deleteVoiceNote(id: Long) {
        voiceNoteDao.deleteById(id)
    }

    suspend fun recordDailyPractice(versesCount: Int) {
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val streak = DailyStreakEntity(
            dateString = dateString,
            versesRevised = versesCount,
            minutesPracticed = 15,
            streakActive = true
        )
        streakDao.insertOrUpdate(streak)

        // Update user profile points
        val currentProfile = userProfile.firstOrNull() ?: UserProfileEntity()
        val newPoints = currentProfile.totalPoints + (versesCount * 10)
        streakDao.insertUserProfile(currentProfile.copy(totalPoints = newPoints))
    }

    fun getCommunityLeaderboard(): List<LeaderboardUser> {
        return listOf(
            LeaderboardUser("u1", 1, "Tariq Al-Hafiz", 3450, 42, 280, "Gold Hafiz", false),
            LeaderboardUser("u2", 2, "Fatima Az-Zahra", 3120, 35, 250, "Silver Hafiz", false),
            LeaderboardUser("u3", 3, "Youssef Qari", 2890, 29, 210, "Bronze Hafiz", false),
            LeaderboardUser("u_me", 4, "You (Qari Student)", 1250, 7, 95, "Hafiz Apprentice", true),
            LeaderboardUser("u5", 5, "Aisha Rahman", 1180, 12, 88, "Hafiz Apprentice", false),
            LeaderboardUser("u6", 6, "Zayd Ibn Ali", 950, 9, 72, "Beginner", false),
            LeaderboardUser("u7", 7, "Maryam Siddiqua", 820, 5, 60, "Beginner", false)
        )
    }
}

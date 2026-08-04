package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memorization_progress")
data class MemorizationProgressEntity(
    @PrimaryKey val ayahKey: String, // e.g. "1_1"
    val surahId: Int,
    val ayahNumber: Int,
    val status: String, // NEW, REVIEWING, MEMORIZED, MASTERED
    val lastReviewed: Long = System.currentTimeMillis(),
    val nextReviewDate: Long = System.currentTimeMillis(),
    val repetitionCount: Int = 0,
    val accuracyScore: Int = 100
)

@Entity(tableName = "voice_notes")
data class VoiceNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahId: Int,
    val ayahNumber: Int,
    val title: String,
    val filePath: String,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "daily_streaks")
data class DailyStreakEntity(
    @PrimaryKey val dateString: String, // "YYYY-MM-DD"
    val versesRevised: Int,
    val minutesPracticed: Int,
    val streakActive: Boolean
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Qari Student",
    val streakDays: Int = 5,
    val totalPoints: Int = 1250,
    val level: String = "Hafiz Apprentice"
)

package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemorizationDao {
    @Query("SELECT * FROM memorization_progress")
    fun getAllProgress(): Flow<List<MemorizationProgressEntity>>

    @Query("SELECT * FROM memorization_progress WHERE surahId = :surahId")
    fun getSurahProgress(surahId: Int): Flow<List<MemorizationProgressEntity>>

    @Query("SELECT * FROM memorization_progress WHERE nextReviewDate <= :currentTime")
    fun getDueReviews(currentTime: Long): Flow<List<MemorizationProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: MemorizationProgressEntity)
}

@Dao
interface VoiceNoteDao {
    @Query("SELECT * FROM voice_notes ORDER BY timestamp DESC")
    fun getAllVoiceNotes(): Flow<List<VoiceNoteEntity>>

    @Query("SELECT * FROM voice_notes WHERE surahId = :surahId ORDER BY timestamp DESC")
    fun getVoiceNotesForSurah(surahId: Int): Flow<List<VoiceNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(voiceNote: VoiceNoteEntity)

    @Query("DELETE FROM voice_notes WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface DailyStreakDao {
    @Query("SELECT * FROM daily_streaks ORDER BY dateString DESC")
    fun getAllStreaks(): Flow<List<DailyStreakEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(streak: DailyStreakEntity)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfileEntity)
}

package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MemorizationProgressEntity::class,
        VoiceNoteEntity::class,
        DailyStreakEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memorizationDao(): MemorizationDao
    abstract fun voiceNoteDao(): VoiceNoteDao
    abstract fun dailyStreakDao(): DailyStreakDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hifz_quran_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

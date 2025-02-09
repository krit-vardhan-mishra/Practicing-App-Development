package com.just_for_fun.taskview

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Task::class, NoteFormattingEntity::class], version = 1, exportSchema = false)
@TypeConverters(TextStyleConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun noteFormattingDao(): NoteFormattingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tasks"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
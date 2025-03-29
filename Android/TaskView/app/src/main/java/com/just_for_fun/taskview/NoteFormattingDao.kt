package com.just_for_fun.taskview

import androidx.room.*

@Dao
interface NoteFormattingDao {
    @Query("SELECT * FROM note_formatting WHERE taskId = :taskId")
    suspend fun getFormattingForTask(taskId: Long): List<NoteFormattingEntity>

    @Insert
    suspend fun insertFormatting(formatting: NoteFormattingEntity): Long

    @Delete
    suspend fun deleteFormatting(formatting: NoteFormattingEntity)

    @Query("DELETE FROM note_formatting WHERE taskId = :taskId AND start >= :selStart AND `end` <= :selEnd")
    suspend fun deleteFormattingInRange(taskId: Long, selStart: Int, selEnd: Int)

}
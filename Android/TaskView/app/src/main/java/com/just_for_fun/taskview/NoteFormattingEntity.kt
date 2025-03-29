package com.just_for_fun.taskview

import androidx.room.*

@Entity(
    tableName = "note_formatting",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId"])]
)
data class NoteFormattingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val start: Int,
    val end: Int,
    val style: TextStyle,
    val color: Int = 0
)

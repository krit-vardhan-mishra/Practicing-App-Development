package com.just_for_fun.taskview

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "task_database"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "task_database"
        const val TASK_ID = "id"
        const val TASK_TITLE = "task_title"
        const val TASK_IS_CHECKED = "is_checked"
        const val TASK_NOTES = "task_notes"
        const val TASK_ATTACHMENT_PATH = "task_attachment_path"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TASK_TABLE = ("create table $TABLE_NAME ("
                + "$TASK_ID integer primary key autoincrement,"
                + "$TASK_TITLE TEXT PRIMARY KEY,"
                + "$TASK_IS_CHECKED INTEGER,"
                + "$TASK_NOTES TEXT,"
                + "$TASK_ATTACHMENT_PATH TEXT"
                + ")"
                )
        db.execSQL(CREATE_TASK_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists $TABLE_NAME")
        if (db != null) {
            onCreate(db)
        }
    }

    fun insertTask(task: Task) : Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TASK_TITLE, task.getTitle())
        values.put(TASK_IS_CHECKED, if (task.isChecked()) 1 else 0)
        values.put(TASK_NOTES, task.getNotes())
        values.put(TASK_ATTACHMENT_PATH, task.getAttachment())

        val insertedRowId = db.insert(TABLE_NAME, null, values)

        if (insertedRowId != -1L) {
            val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
            cursor.use {
                if (it.moveToFirst()) {
                    val lastId = it.getLong(0)
                    db.close()
                    return lastId
                }
            }
        }
        db.close()
        return -1L
    }

    fun updateTask(task: Task) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TASK_TITLE, task.getTitle())
        values.put(TASK_IS_CHECKED, if (task.isChecked()) 1 else 0)
        values.put(TASK_NOTES, task.getNotes())
        values.put(TASK_ATTACHMENT_PATH, task.getAttachment())
        val whereClause = "$TASK_ID = ?"
        val whereArgs = arrayOf(task.getId().toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getAllTaskTitles(): List<String> {
        val titleList = ArrayList<String>()
        val db = this.readableDatabase
        val query = "SELECT $TASK_TITLE FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                val title = it.getString(it.getColumnIndexOrThrow(TASK_TITLE))
                titleList.add(title)
            }
        }

        db.close()
        return titleList
    }

    fun getAllTask() : List<Task> {
        val taskList = ArrayList<Task>()
        val db = this.readableDatabase
        val query = "select * from $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(TASK_ID)) // Get ID by name
                val title = it.getString(it.getColumnIndexOrThrow(TASK_TITLE))
                val isChecked = it.getInt(it.getColumnIndexOrThrow(TASK_IS_CHECKED)) == 1
                val notes = it.getString(it.getColumnIndexOrThrow(TASK_NOTES))
                val attachment = it.getString(it.getColumnIndexOrThrow(TASK_ATTACHMENT_PATH))

                val task = Task(id, title, notes, isChecked, attachment)
                taskList.add(task)
            }
        }

        db.close()
        return taskList
    }

    fun deleteTask(task: Task) {
        val db = this.writableDatabase
        val whereClause = "$TASK_ID = ?"
        val whereArgs = arrayOf(task.getId().toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}
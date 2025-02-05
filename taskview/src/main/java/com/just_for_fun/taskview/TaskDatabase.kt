package com.just_for_fun.taskview

import android.content.Context
import android.net.Uri
import androidx.room.Room


class TaskDatabase(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "task_database"
    ).build()
    private val taskDao = db.taskDao()

    fun getAllTask(): List<Task> = taskDao.getAllTasks().map { it.toTask() }

    fun getTaskById(id: Int): Task? = taskDao.getTaskById(id)?.toTask()

    fun insertTask(task: Task): Long = taskDao.insertTask(task.toTaskEntity())

    fun updateTask(task: Task) = taskDao.updateTask(task.toTaskEntity())

    fun deleteTask(task: Task) = taskDao.deleteTask(task.toTaskEntity())

    private fun TaskEntity.toTask(): Task = Task(id, title, notes, isChecked, attachment?.let { Uri.parse(it) })

    private fun Task.toTaskEntity(): TaskEntity = TaskEntity(id, title, notes, isChecked, attachment?.toString())

}
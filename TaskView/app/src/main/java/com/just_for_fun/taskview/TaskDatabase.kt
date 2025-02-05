package com.just_for_fun.taskview

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class TaskDatabase(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val taskDao = db.taskDao()

    fun getAllTask(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    fun getTaskById(id: Long): Flow<Task?> {
        return taskDao.getTaskById(id)
    }

    fun getTask(): Flow<Task?> {
        return taskDao.getTask()
    }

//    suspend fun insertTask(task: Task): Long {
//        return withContext(Dispatchers.IO) {
//            taskDao.insertTask(task)
//        }
//    }
//
//    suspend fun updateTask(task: Task) {
//        withContext(Dispatchers.IO) {
//            taskDao.updateTask(task)
//        }
//    }
//
//    suspend fun deleteTask(task: Task) {
//        withContext(Dispatchers.IO) {
//            taskDao.deleteTask(task)
//        }
//    }

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TaskDatabase(context).also { INSTANCE = it }
            }
        }
    }
}
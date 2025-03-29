package com.just_for_fun.taskview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TaskViewModelFactory(private val taskDatabase: TaskDatabase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom((TaskViewModel::class.java))) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
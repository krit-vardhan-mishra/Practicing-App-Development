package com.just_for_fun.dot_list_v110

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var content: String = "",
    var isChecked: Boolean = false
)
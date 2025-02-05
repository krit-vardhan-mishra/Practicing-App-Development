package com.just_for_fun.taskview

import android.net.Uri

class Task {
    var id : Int = 0
    var title: String = ""
    var notes: String = ""
    var isChecked: Boolean = false
    var attachment: Uri? = null

    constructor()

    constructor(title: String, notes: String, isChecked: Boolean, attachment: Uri?) {
        this.title = title
        this.notes = notes
        this.isChecked = isChecked
        this.attachment = attachment
    }

    constructor(id: Int, title: String, notes: String, isChecked: Boolean, attachment: Uri?) {
        this.id = id
        this.title = title
        this.notes = notes
        this.isChecked = isChecked
        this.attachment = attachment
    }
}

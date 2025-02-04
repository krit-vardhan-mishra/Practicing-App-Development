package com.just_for_fun.taskview

class Task {
    private var id : Int = 0
    private var title: String = ""
    private var notes: String = ""
    private var isChecked: Boolean = false
    private var attachment: String? = null

    constructor()

    constructor(title: String, notes: String, isChecked: Boolean, attachment: String?) {
        this.title = title
        this.notes = notes
        this.isChecked = isChecked
        this.attachment = attachment
    }

    constructor(id: Int, title: String, notes: String, isChecked: Boolean, attachment: String?) {
        this.id = id
        this.title = title
        this.notes = notes
        this.isChecked = isChecked
        this.attachment = attachment
    }

    fun getId(): Int = id
    fun getTitle(): String = title
    fun getNotes(): String = notes
    fun isChecked(): Boolean = isChecked
    fun getAttachment(): String? = attachment
    fun setTitle(value: String) { title = value }
    fun setNotes(value: String) { notes = value }
    fun setChecked(value: Boolean) { isChecked = value }
    fun setAttachment(value: String?) { attachment = value }
    fun setId(value: Int) { id = value }
}

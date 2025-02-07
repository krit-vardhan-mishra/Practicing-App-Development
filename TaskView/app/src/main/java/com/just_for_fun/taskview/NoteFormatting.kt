package com.just_for_fun.taskview

class NoteFormatting @JvmOverloads constructor(
    private val start: Int,
    private val end: Int,
    private val style: TextStyle,
    private val color: Int = 0
) {
    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null || javaClass != obj.javaClass) return false
        val that = obj as NoteFormatting
        return start == that.start && end == that.end && style == that.style && color == that.color
    }

    override fun hashCode(): Int {
        var result = start
        result = 31 * result + end
        result = 31 * result + style.hashCode()
        result = 31 * result + color
        return result
    }
}
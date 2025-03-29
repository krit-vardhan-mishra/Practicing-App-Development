package com.just_for_fun.taskview

import androidx.room.TypeConverter

class TextStyleConverter {
    @TypeConverter
    fun fromTextStyle(style: TextStyle): String {
        return style.name
    }

    @TypeConverter
    fun toTextStyle(styleName: String): TextStyle {
        return TextStyle.valueOf(styleName)
    }
}
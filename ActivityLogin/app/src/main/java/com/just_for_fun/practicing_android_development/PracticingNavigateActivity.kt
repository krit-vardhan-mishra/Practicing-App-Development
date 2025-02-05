package com.just_for_fun.practicing_android_development

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold

class PracticingNavigateActivity : AppCompatActivity() {

    private lateinit var name : TextView
    private lateinit var gender : TextView
    private lateinit var email : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practicing_navigate)

        name = findViewById(R.id.name_display)
        gender = findViewById(R.id.gender_display)
        email = findViewById(R.id.email_display)

        val inputName = intent.getStringExtra("name")
        val inputGender = intent.getStringExtra("gender")
        val inputEmail = intent.getStringExtra("email")

        name.text = SpannableStringBuilder().bold { append("Name: ") }.append(inputName)
        gender.text = SpannableStringBuilder().bold { append("Gender: ") }.append(inputGender)
        email.text = SpannableStringBuilder().bold { append("Email: ") }.append(inputEmail)

    }
}
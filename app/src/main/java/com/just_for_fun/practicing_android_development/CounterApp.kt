package com.just_for_fun.practicing_android_development

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CounterApp : AppCompatActivity() {

    private var count = 0
    private lateinit var textView: TextView
    private lateinit var resetButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter_app)

        textView = findViewById(R.id.count_view)
        resetButton = findViewById(R.id.reset_button)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        count = sharedPreferences.getInt("count", 0)
        updateCounterDisplay()

        resetButton.setOnClickListener {
            count = 0
            saveCounterValue()
            updateCounterDisplay()
        }

    }

    override fun onResume() {
        super.onResume()
        count++
        saveCounterValue()
        updateCounterDisplay()
    }

    private fun saveCounterValue() {
        val editor = sharedPreferences.edit()
        editor.putInt("count", count)
        editor.apply()
    }

    private fun updateCounterDisplay() {
        textView.text = Html.fromHtml("The Counter Application has been opened <span style=\"font-size:100px\"><b>$count times</b></span>.", Html.FROM_HTML_MODE_LEGACY)
    }

}
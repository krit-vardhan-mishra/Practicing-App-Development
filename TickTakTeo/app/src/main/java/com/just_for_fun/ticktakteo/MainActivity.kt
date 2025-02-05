package com.just_for_fun.ticktakteo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var user_button : Button
    private lateinit var ai_button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user_button = findViewById(R.id.user_button)
        ai_button = findViewById(R.id.ai_button)

        user_button.setOnClickListener {
            val intent = Intent(this, TickTacToeTwoUser::class.java)
            startActivity(intent)
            finish()
        }

        ai_button.setOnClickListener {
            val intent = Intent(this, TickTacToeAI::class.java)
            startActivity(intent)
            finish()
        }
    }
}

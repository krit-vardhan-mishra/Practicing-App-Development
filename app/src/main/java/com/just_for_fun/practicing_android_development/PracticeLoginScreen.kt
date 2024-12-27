package com.just_for_fun.practicing_android_development

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class PracticeLoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_practice_login_screen)

        val name : EditText = findViewById(R.id.name)
        val email : EditText = findViewById(R.id.email)
        val password : EditText = findViewById(R.id.password)
        val submit : Button = findViewById(R.id.submit)

        submit.setOnClickListener {
            val nameText = name.text.toString()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            Toast.makeText(this, "Name: $nameText\nEmail: $emailText\nPassword: $passwordText", Toast.LENGTH_LONG).show()
        }

        val fab: View = findViewById(R.id.action_button)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Here's a FloatingActionButton", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }
    }
}
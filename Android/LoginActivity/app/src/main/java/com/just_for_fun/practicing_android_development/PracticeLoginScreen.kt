package com.just_for_fun.practicing_android_development

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.getbase.floatingactionbutton.FloatingActionButton
import kotlin.math.hypot

class PracticeLoginScreen : AppCompatActivity() {

    private var isRotated = false
    private var isFabMenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practice_login_screen)

        setupLoginForm()
        setupFabMenu()
    }

    private fun setupLoginForm() {
        val name : EditText = findViewById(R.id.name)
        val email : EditText = findViewById(R.id.email)
        val submit : Button = findViewById(R.id.submit)
        val gender : Spinner = findViewById(R.id.gender_spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_items,
            android.R.layout.simple_spinner_item
        ).also{adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            gender.adapter =adapter
        }

        submit.setOnClickListener {
            val nameText = name.text.toString()
            val emailText = email.text.toString()
            val genderText = gender.selectedItem.toString()

            val intent = Intent(this, PracticingNavigateActivity::class.java)
            intent.putExtra("name", nameText)
            intent.putExtra("email", emailText)
            intent.putExtra("gender", genderText)
            startActivity(intent)
        }
    }

    private fun setupFabMenu() {
        val actionButton : FloatingActionButton = findViewById(R.id.action_button)
        val accountButton : FloatingActionButton = findViewById(R.id.account_button)
        val rideButton : FloatingActionButton = findViewById(R.id.ride_button)
        val shareButton : FloatingActionButton = findViewById(R.id.share_button)
        val accountView : TextView = findViewById(R.id.textView5)
        val rideView : TextView = findViewById(R.id.textView6)
        val shareView : TextView = findViewById(R.id.textView7)

        // Hiding the menu initially
        accountButton.visibility = View.GONE
        rideButton.visibility = View.GONE
        shareButton.visibility = View.GONE
        accountView.visibility = View.GONE
        rideView.visibility = View.GONE
        shareView.visibility = View.GONE

        actionButton.setOnClickListener {

            val rotateAnimation = if (!isRotated) {
                AnimationUtils.loadAnimation(this, R.anim.rotate_fab)
            } else {
                AnimationUtils.loadAnimation(this, R.anim.rotate_fab_reverse)
            }

            rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    isRotated = !isRotated
                }

                override fun onAnimationRepeat(p0: Animation?) { }
            })

            actionButton.startAnimation(rotateAnimation)
            toggleFabMenu(accountButton, rideButton, shareButton, accountView, rideView, shareView)
        }

        accountButton.setOnClickListener( {
            accountView.visibility = View.VISIBLE
        })

        shareButton.setOnClickListener( {
            shareView.visibility = View.VISIBLE
        })

        rideButton.setOnClickListener( {
            rideView.visibility = View.VISIBLE
        })
    }

    private fun toggleFabMenu(
        accountButton: FloatingActionButton,
        rideButton: FloatingActionButton,
        shareButton: FloatingActionButton,
        accountView: TextView,
        rideView: TextView,
        shareView: TextView
    ) {

        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val submitButton = findViewById<Button>(R.id.submit)

        if (!isFabMenuOpen) {

            val cx = mainLayout.width
            val cy = mainLayout.height
            val finalRadius = hypot(mainLayout.width.toDouble(), mainLayout.height.toDouble()).toFloat()

            val revealAnim = ViewAnimationUtils.createCircularReveal(mainLayout, cx, cy, 0f, finalRadius)
            mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.dimmed_background))
            mainLayout.visibility = View.VISIBLE
            revealAnim.duration = 300
            revealAnim.start()

            // Show buttons and views
            accountButton.visibility = View.VISIBLE
            rideButton.visibility = View.VISIBLE
            shareButton.visibility = View.VISIBLE
            accountView.visibility = View.VISIBLE
            rideView.visibility = View.VISIBLE
            shareView.visibility = View.VISIBLE

            submitButton.setTextColor(ContextCompat.getColor(this, R.color.another_random))
        } else {
            val cx = mainLayout.width
            val cy = mainLayout.height
            val initialRadius = hypot(mainLayout.width.toDouble(), mainLayout.height.toDouble()).toFloat()

            val hideAnim = ViewAnimationUtils.createCircularReveal(mainLayout, cx, cy, initialRadius, 0f)
            hideAnim.duration = 300

            hideAnim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    mainLayout.visibility = View.VISIBLE
                    mainLayout.setBackgroundColor(Color.TRANSPARENT)
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            })
            submitButton.setTextColor(ContextCompat.getColor(this, R.color.white))

            hideAnim.start()

            accountButton.visibility = View.GONE
            rideButton.visibility = View.GONE
            shareButton.visibility = View.GONE
            accountView.visibility = View.GONE
            rideView.visibility = View.GONE
            shareView.visibility = View.GONE

        }
        isFabMenuOpen = !isFabMenuOpen
    }
}
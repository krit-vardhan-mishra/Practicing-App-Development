package com.just_for_fun.justforfun.ui.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.just_for_fun.justforfun.MainActivity
import com.just_for_fun.justforfun.R
import com.just_for_fun.justforfun.ui.custom.FloatingBackgroundView
import com.just_for_fun.justforfun.databinding.ActivityLoginBinding

// Firebase imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

//class LoginActivity : AppCompatActivity() {
//
//    private val TAG = "LoginActivity"
//
//    private var floatingBackgroundView: FloatingBackgroundView? = null
//    private lateinit var binding: ActivityLoginBinding // Use View Binding
//
//    // Firebase Auth and Firestore instances
//    private lateinit var auth: FirebaseAuth
//    private lateinit var db: FirebaseFirestore
//
//    private var isPasswordVisible = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root) // Set the root view from the binding
//
//        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        // Initialize Firebase
//        auth = FirebaseAuth.getInstance()
//        db = FirebaseFirestore.getInstance() // Initializing Firestore, though not directly used in login for now
//
//        initializeViews()
//        setupClickListeners()
//        startEntryAnimations()
//
//        // Hide progress bar initially
//        binding.progressBar.visibility = View.GONE
//    }
//
//    private fun initializeViews() {
//        floatingBackgroundView = binding.floatingBackground
//        // All other views are accessed via 'binding.'
//    }
//
//    private fun setupClickListeners() {
//        binding.btnLogin.setOnClickListener {
//            // Disable button and show progress while logging in
//            binding.btnLogin.isEnabled = false
//            binding.progressBar.visibility = View.VISIBLE
//            animateLoginButton {
//                performLogin()
//            }
//        }
//
//        binding.passwordToggle.setOnClickListener {
//            togglePasswordVisibility()
//        }
//
//        binding.forgotPasswordText.setOnClickListener {
//            animateClickFeedback(it) {
//                // TODO: Handle forgot password - e.g., navigate to a password reset activity
//                Toast.makeText(this, "Forgot Password clicked!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        binding.signUpText.setOnClickListener {
//            animateClickFeedback(it) {
//                // Navigate to SignUpActivity
//                val intent = Intent(this, SignUpActivity::class.java)
//                startActivity(intent)
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//            }
//        }
//
//        binding.btnGoogleLogin.setOnClickListener {
//            animateClickFeedback(it) {
//                // TODO: Implement Google Sign-In
//                Toast.makeText(this, "Google Login clicked!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        binding.btnFacebookLogin.setOnClickListener {
//            animateClickFeedback(it) {
//                // TODO: Implement Facebook Login
//                Toast.makeText(this, "Facebook Login clicked!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Add focus change listeners for input fields
//        binding.emailEditText.setOnFocusChangeListener { _, hasFocus ->
//            animateInputFocus(binding.emailEditText.parent as LinearLayout, hasFocus)
//        }
//
//        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus ->
//            animateInputFocus(binding.passwordContainer, hasFocus)
//        }
//    }
//
//    private fun togglePasswordVisibility() {
//        isPasswordVisible = !isPasswordVisible
//
//        if (isPasswordVisible) {
//            binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//            binding.passwordToggle.setImageResource(R.drawable.ic_visibility)
//        } else {
//            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//            binding.passwordToggle.setImageResource(R.drawable.ic_visibility_off)
//        }
//
//        // Move cursor to end
//        binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
//
//        // Animate toggle
//        animatePasswordToggle()
//    }
//
//    private fun animatePasswordToggle() {
//        val rotateAnimator = ObjectAnimator.ofFloat(binding.passwordToggle, "rotation", 0f, 360f)
//        rotateAnimator.duration = 300
//        rotateAnimator.interpolator = AccelerateDecelerateInterpolator()
//        rotateAnimator.start()
//    }
//
//    private fun startEntryAnimations() {
//        // Initially hide all views
//        binding.loginCard.alpha = 0f
//        binding.loginCard.translationY = 100f
//        binding.loginCard.scaleX = 0.9f
//        binding.loginCard.scaleY = 0.9f
//
//        // Animate card entrance
//        val cardAnimator = AnimatorSet().apply {
//            playTogether(
//                ObjectAnimator.ofFloat(binding.loginCard, "alpha", 0f, 1f),
//                ObjectAnimator.ofFloat(binding.loginCard, "translationY", 100f, 0f),
//                ObjectAnimator.ofFloat(binding.loginCard, "scaleX", 0.9f, 1f),
//                ObjectAnimator.ofFloat(binding.loginCard, "scaleY", 0.9f, 1f)
//            )
//            duration = 800
//            interpolator = OvershootInterpolator(0.8f)
//            startDelay = 300
//        }
//
//        cardAnimator.start()
//    }
//
//    private fun animateLoginButton(onAnimationComplete: () -> Unit) {
//        val scaleDown = AnimatorSet().apply {
//            playTogether(
//                ObjectAnimator.ofFloat(binding.btnLogin, "scaleX", 1f, 0.95f),
//                ObjectAnimator.ofFloat(binding.btnLogin, "scaleY", 1f, 0.95f)
//            )
//            duration = 100
//            interpolator = AccelerateDecelerateInterpolator()
//        }
//
//        val scaleUp = AnimatorSet().apply {
//            playTogether(
//                ObjectAnimator.ofFloat(binding.btnLogin, "scaleX", 0.95f, 1f),
//                ObjectAnimator.ofFloat(binding.btnLogin, "scaleY", 0.95f, 1f)
//            )
//            duration = 100
//            interpolator = OvershootInterpolator(2f)
//        }
//
//        scaleDown.start()
//        scaleDown.addListener(object : android.animation.AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: android.animation.Animator) {
//                scaleUp.start()
//                onAnimationComplete()
//            }
//        })
//    }
//
//    private fun animateClickFeedback(view: View, onAnimationComplete: () -> Unit) {
//        val scaleAnimator = AnimatorSet().apply {
//            playTogether(
//                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1f),
//                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1f)
//            )
//            duration = 200
//            interpolator = AccelerateDecelerateInterpolator()
//        }
//
//        scaleAnimator.start()
//        scaleAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: android.animation.Animator) {
//                onAnimationComplete()
//            }
//        })
//    }
//
//    private fun animateInputFocus(container: LinearLayout, hasFocus: Boolean) {
//        val targetElevation = if (hasFocus) 8f else 0f
//        val targetScale = if (hasFocus) 1.02f else 1f
//
//        ObjectAnimator.ofFloat(container, "elevation", container.elevation, targetElevation).apply {
//            duration = 200
//            interpolator = AccelerateDecelerateInterpolator()
//        }.start()
//
//        AnimatorSet().apply {
//            playTogether(
//                ObjectAnimator.ofFloat(container, "scaleX", container.scaleX, targetScale),
//                ObjectAnimator.ofFloat(container, "scaleY", container.scaleY, targetScale)
//            )
//            duration = 200
//            interpolator = AccelerateDecelerateInterpolator()
//        }.start()
//
//        // Update background drawable to show focus state
//        if (hasFocus) {
//            container.setBackgroundResource(R.drawable.dark_input_background_focused)
//        } else {
//            container.setBackgroundResource(R.drawable.dark_input_background)
//        }
//    }
//
//    private fun performLogin() {
//        val email = binding.emailEditText.text.toString().trim()
//        val password = binding.passwordEditText.text.toString().trim()
//
//        // Validate inputs before attempting Firebase login
//        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            showError(binding.emailEditText.parent as LinearLayout, "Enter a valid email")
//            binding.emailEditText.requestFocus()
//            // Re-enable button and hide progress
//            binding.btnLogin.isEnabled = true
//            binding.progressBar.visibility = View.GONE
//            return
//        }
//
//        if (password.isEmpty()) {
//            showError(binding.passwordContainer, "Password is required")
//            binding.passwordEditText.requestFocus()
//            // Re-enable button and hide progress
//            binding.btnLogin.isEnabled = true
//            binding.progressBar.visibility = View.GONE
//            return
//        }
//
//        // Clear any previous errors
//        clearError(binding.emailEditText.parent as LinearLayout)
//        clearError(binding.passwordContainer)
//
//        // Use CoroutineScope for asynchronous Firebase operation
//        CoroutineScope(Dispatchers.Main).launch {
//            try {
//                // Attempt to sign in with email and password
//                val authResult = auth.signInWithEmailAndPassword(email, password).await()
//                val firebaseUser = authResult.user
//
//                if (firebaseUser != null) {
//                    // Login successful
//                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
//                    animateSuccessfulLogin()
//                } else {
//                    Toast.makeText(this@LoginActivity, "Login failed: User is null.", Toast.LENGTH_LONG).show()
//                    Log.e(TAG, "Firebase user is null after successful sign-in attempt.")
//                }
//            } catch (e: Exception) {
//                // Handle various Firebase Authentication exceptions
//                val errorMessage = when (e) {
//                    is FirebaseAuthInvalidUserException -> "User not found or has been disabled."
//                    is FirebaseAuthInvalidCredentialsException -> "Invalid password or email address."
//                    else -> "Login failed: ${e.message}"
//                }
//                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
//                Log.e(TAG, "Login error: ${e.message}", e)
//
//                // Re-enable button and hide progress on failure
//                binding.btnLogin.isEnabled = true
//                binding.progressBar.visibility = View.GONE
//                animateShake(binding.loginCard) // Shake the card on login failure
//            }
//        }
//    }
//
//    private fun showError(container: LinearLayout, message: String) {
//        // Change background to error state
//        container.setBackgroundResource(R.drawable.dark_input_background_error)
//
//        // Animate shake
//        animateShake(container)
//
//        // You can add a TextView to show error message if needed
//        // For example, if you add a TextView with id `errorText` inside the LinearLayout:
//        // val errorTextView = container.findViewById<TextView>(R.id.errorText)
//        // errorTextView?.text = message
//        // errorTextView?.visibility = View.VISIBLE
//    }
//
//    private fun clearError(container: LinearLayout) {
//        container.setBackgroundResource(R.drawable.dark_input_background)
//        // Optionally hide error text view if you added one
//        // val errorTextView = container.findViewById<TextView>(R.id.errorText)
//        // errorTextView?.visibility = View.GONE
//    }
//
//    private fun animateShake(view: View) {
//        val shakeAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
//        shakeAnimator.duration = 600
//        shakeAnimator.start()
//    }
//
//    private fun animateSuccessfulLogin() {
//        // Change button text and animate
//        binding.btnLogin.text = "Logging In..."
//        binding.btnLogin.isEnabled = false
//
//        // Animate card exit
//        val exitAnimator = AnimatorSet().apply {
//            playTogether(
//                ObjectAnimator.ofFloat(binding.loginCard, "alpha", 1f, 0f),
//                ObjectAnimator.ofFloat(binding.loginCard, "translationY", 0f, -100f),
//                ObjectAnimator.ofFloat(binding.loginCard, "scaleX", 1f, 0.9f),
//                ObjectAnimator.ofFloat(binding.loginCard, "scaleY", 1f, 0.9f)
//            )
//            duration = 500
//            interpolator = AccelerateDecelerateInterpolator()
//            startDelay = 500 // Shortened delay for faster transition
//        }
//
//        exitAnimator.start()
//        exitAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: android.animation.Animator) {
//                val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//            }
//        })
//    }
//
//    override fun onResume() {
//        super.onResume()
//        floatingBackgroundView?.startFloatingAnimation()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        floatingBackgroundView?.stopFloatingAnimation()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        floatingBackgroundView?.cleanup()
//        floatingBackgroundView = null
//    }
//}

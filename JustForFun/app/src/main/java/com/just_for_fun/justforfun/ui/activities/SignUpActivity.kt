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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.just_for_fun.justforfun.R
import com.just_for_fun.justforfun.data.models.user.User
import com.just_for_fun.justforfun.ui.custom.FloatingBackgroundView
import com.just_for_fun.justforfun.databinding.ActivitySignupBinding

// Firebase imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class SignUpActivity : AppCompatActivity() {

    private val TAG = "SignUpActivity"

    private var floatingBackgroundView: FloatingBackgroundView? = null
    private lateinit var binding: ActivitySignupBinding // Use View Binding

    // Firebase Auth and Firestore instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initializeViews()
        setupClickListeners()
        startEntryAnimations()

        // Hide progress bar initially
        binding.progressBar.visibility = View.GONE
    }

    private fun initializeViews() {
        floatingBackgroundView = binding.floatingBackground
        // All other views are accessed via 'binding.'
    }

    private fun setupClickListeners() {
        binding.signUpButton.setOnClickListener {
            // Disable button and show progress while signing up
            binding.signUpButton.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            animateSignUpButton {
                performSignUp()
            }
        }

        binding.passwordToggle.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.confirmPasswordToggle.setOnClickListener {
            toggleConfirmPasswordVisibility()
        }

        binding.signInText.setOnClickListener {
            animateClickFeedback(it) {
                navigateToLogin()
            }
        }
    }

    private fun startEntryAnimations() {
        // Set initial state
        binding.signUpCard.alpha = 0f
        binding.signUpCard.scaleX = 0.8f
        binding.signUpCard.scaleY = 0.8f

        // Animate card entrance
        val cardAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.signUpCard, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.signUpCard, "scaleX", 0.8f, 1f),
                ObjectAnimator.ofFloat(binding.signUpCard, "scaleY", 0.8f, 1f)
            )
            duration = 800
            interpolator = OvershootInterpolator(1.2f)
            startDelay = 200
        }
        cardAnimator.start()
    }

    private fun animateSignUpButton(onComplete: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(binding.signUpButton, "scaleX", 1f, 0.95f)
        val scaleDownY = ObjectAnimator.ofFloat(binding.signUpButton, "scaleY", 1f, 0.95f)
        val scaleUpX = ObjectAnimator.ofFloat(binding.signUpButton, "scaleX", 0.95f, 1f)
        val scaleUpY = ObjectAnimator.ofFloat(binding.signUpButton, "scaleY", 0.95f, 1f)

        val scaleDown = AnimatorSet().apply {
            playTogether(scaleDownX, scaleDownY)
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(scaleUpX, scaleUpY)
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }

        scaleDown.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                scaleUp.start()
                // Do not call onComplete here, call it after Firebase operation finishes
            }
        })

        scaleDown.start()
        onComplete() // Call onComplete immediately to start the signup process
    }

    private fun animateClickFeedback(view: View, onComplete: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f)

        val scaleDown = AnimatorSet().apply {
            playTogether(scaleDownX, scaleDownY)
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(scaleUpX, scaleUpY)
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }

        scaleDown.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                scaleUp.start()
                onComplete()
            }
        })

        scaleDown.start()
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordToggle.setImageResource(R.drawable.ic_visibility_off)
        } else {
            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordToggle.setImageResource(R.drawable.ic_visibility)
        }
        binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    private fun toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            binding.confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.confirmPasswordToggle.setImageResource(R.drawable.ic_visibility_off)
        } else {
            binding.confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.confirmPasswordToggle.setImageResource(R.drawable.ic_visibility)
        }
        binding.confirmPasswordEditText.setSelection(binding.confirmPasswordEditText.text.length)
        isConfirmPasswordVisible = !isConfirmPasswordVisible
    }

    private fun performSignUp() {
        val name = binding.nameEditText.text.toString().trim()
        val username = binding.usernameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        // Validate inputs
        if (!validateInputs(name, username, email, password, confirmPassword)) {
            // Re-enable button and hide progress if validation fails
            binding.signUpButton.isEnabled = true
            binding.progressBar.visibility = View.GONE
            return
        }

        // Use CoroutineScope for asynchronous Firebase operations
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 1. Create user with Email and Password in Firebase Authentication
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // 2. Create a User object with the Firebase User ID
                    val newUser = User(
                        id = firebaseUser.uid, // Use Firebase Auth UID as the user ID
                        name = name,
                        email = email,
                        username = username,
                        joinDate = System.currentTimeMillis()
                        // Other fields will have default values or can be set here
                    )

                    // 3. Save user data to Firestore
                    saveUserDataToFirestore(newUser)

                    Toast.makeText(this@SignUpActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                } else {
                    Toast.makeText(this@SignUpActivity, "Sign up failed: User creation failed.", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Firebase user is null after successful creation attempt.")
                }
            } catch (e: Exception) {
                // Handle various Firebase Authentication exceptions
                val errorMessage = when (e) {
                    is FirebaseAuthUserCollisionException -> "The email address is already in use by another account."
                    else -> "Sign up failed: ${e.message}"
                }
                Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Sign up error: ${e.message}", e)
            } finally {
                // Always re-enable button and hide progress
                binding.signUpButton.isEnabled = true
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun saveUserDataToFirestore(user: User) {
        CoroutineScope(Dispatchers.IO).launch { // Use IO dispatcher for database operations
            try {
                // Save the User object to a "users" collection, using UID as document ID
                db.collection("users")
                    .document(user.id) // Use the Firebase Auth UID as the document ID
                    .set(user) // Use .set() to create or overwrite the document
                    .await()
                Log.d(TAG, "User data saved to Firestore successfully for user ID: ${user.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving user data to Firestore: ${e.message}", e)
                // Optionally show a more persistent error or log for analytics
                // Toast.makeText(this@SignUpActivity, "Failed to save user data.", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun validateInputs(
        name: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Validate name
        if (name.isEmpty()) {
            binding.nameEditText.error = "Name is required"
            binding.nameEditText.requestFocus()
            return false
        }

        if (name.length < 2) {
            binding.nameEditText.error = "Name must be at least 2 characters"
            binding.nameEditText.requestFocus()
            return false
        }

        // Validate username
        if (username.isEmpty()) {
            binding.usernameEditText.error = "Username is required"
            binding.usernameEditText.requestFocus()
            return false
        }

        if (username.length < 3) {
            binding.usernameEditText.error = "Username must be at least 3 characters"
            binding.usernameEditText.requestFocus()
            return false
        }

        if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            binding.usernameEditText.error = "Username can only contain letters, numbers, and underscores"
            binding.usernameEditText.requestFocus()
            return false
        }

        // Validate email
        if (email.isEmpty()) {
            binding.emailEditText.error = "Email is required"
            binding.emailEditText.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Please enter a valid email address"
            binding.emailEditText.requestFocus()
            return false
        }

        // Validate password
        if (password.isEmpty()) {
            binding.passwordEditText.error = "Password is required"
            binding.passwordEditText.requestFocus()
            return false
        }

        if (password.length < 6) {
            binding.passwordEditText.error = "Password must be at least 6 characters"
            binding.passwordEditText.requestFocus()
            return false
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordEditText.error = "Please confirm your password"
            binding.confirmPasswordEditText.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            binding.confirmPasswordEditText.error = "Passwords do not match"
            binding.confirmPasswordEditText.requestFocus()
            return false
        }

        return true
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Add transition for smoother UX
    }

    override fun onResume() {
        super.onResume()
        floatingBackgroundView?.startFloatingAnimation()
    }

    override fun onPause() {
        super.onPause()
        floatingBackgroundView?.stopFloatingAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingBackgroundView?.cleanup()
        floatingBackgroundView = null
    }
}

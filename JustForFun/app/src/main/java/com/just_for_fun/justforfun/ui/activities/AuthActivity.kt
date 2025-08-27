package com.just_for_fun.justforfun.ui.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewTreeObserver
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
import com.just_for_fun.justforfun.data.models.user.User
import com.just_for_fun.justforfun.data.models.user.UserPreferences
import com.just_for_fun.justforfun.data.managers.UserManager
import com.just_for_fun.justforfun.ui.custom.FloatingBackgroundView
import com.just_for_fun.justforfun.databinding.ActivityAuthBinding

// Firebase imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

//// Facebook imports
//import com.facebook.AccessToken
//import com.facebook.CallbackManager
//import com.facebook.FacebookCallback
//import com.facebook.FacebookException
//import com.facebook.FacebookSdk
//import com.facebook.login.LoginManager
//import com.facebook.login.LoginResult

class AuthActivity : AppCompatActivity() {

    private val TAG = "AuthActivity"

    private var floatingBackgroundView: FloatingBackgroundView? = null
    private lateinit var binding: ActivityAuthBinding

    // Firebase Auth and Firestore instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var credentialManager: CredentialManager

    // UI State
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var isSignUpMode = false
    private var indicatorInitialized = false

    // Facebook login
//    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        credentialManager = CredentialManager.create(this)

        // Initialize Facebook SDK
//        FacebookSdk.sdkInitialize(applicationContext)
//        callbackManager = CallbackManager.Factory.create()

        // Check if user is already signed in
        if (auth.currentUser != null) {
            // User is signed in, redirect to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        initializeViews()
        setupClickListeners()
        startEntryAnimations()

        // Hide progress bar initially
        binding.progressBar.visibility = View.GONE

        // Initialize in login mode after layout is complete
        binding.modeToggleContainer.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.modeToggleContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                initializeModeToggle()
                // Ensure we're in login mode initially
                isSignUpMode = false
                setLoginMode()
            }
        })
    }

    private fun initializeViews() {
        floatingBackgroundView = binding.floatingBackground
    }

    private fun initializeModeToggle() {
        // Set initial indicator size and position
        val buttonWidth = binding.btnLoginMode.width
        val layoutParams = binding.modeIndicator.layoutParams
        layoutParams.width = buttonWidth
        binding.modeIndicator.layoutParams = layoutParams

        // Position indicator at login button (left position) - make sure it's at 0
        binding.modeIndicator.translationX = 0f
        indicatorInitialized = true

        // Force login mode state
        isSignUpMode = false
        
        // Set initial button states
        updateButtonStates()
    }

    private fun setupClickListeners() {
        // Mode toggle buttons with enhanced animations
        binding.btnLoginMode.setOnClickListener {
            if (isSignUpMode) {
                Log.d(TAG, "Switching to Login Mode")
                animateButtonPress(it) {
                    setLoginMode()
                }
            }
        }

        binding.btnSignUpMode.setOnClickListener {
            if (!isSignUpMode) {
                Log.d(TAG, "Switching to SignUp Mode")
                animateButtonPress(it) {
                    setSignUpMode()
                }
            }
        }

        // Auth button
        binding.btnAuth.setOnClickListener {
            binding.btnAuth.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            animateAuthButton {
                if (isSignUpMode) {
                    performSignUp()
                } else {
                    performLogin()
                }
            }
        }

        // Password toggles
        binding.passwordToggle.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.confirmPasswordToggle.setOnClickListener {
            toggleConfirmPasswordVisibility()
        }

        // Forgot password (only visible in login mode)
        binding.forgotPasswordText.setOnClickListener {
            animateClickFeedback(it) {
                showForgotPasswordDialog()
            }
        }

        // Social login buttons (only visible in login mode)
        binding.btnGoogleLogin.setOnClickListener {
            animateClickFeedback(it) {
                performGoogleSignIn()
            }
        }

        // Add focus change listeners for input fields
        binding.emailEditText.setOnFocusChangeListener { _, hasFocus ->
            animateInputFocus(binding.emailInputContainer, hasFocus)
        }

        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            animateInputFocus(binding.passwordContainer, hasFocus)
        }

        binding.nameEditText.setOnFocusChangeListener { _, hasFocus ->
            animateInputFocus(binding.nameInputContainer, hasFocus)
        }

        binding.usernameEditText.setOnFocusChangeListener { _, hasFocus ->
            animateInputFocus(binding.usernameInputContainer, hasFocus)
        }

        binding.confirmPasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            animateInputFocus(binding.confirmPasswordContainer, hasFocus)
        }
    }

    private fun setLoginMode() {
        Log.d(TAG, "setLoginMode() called - switching to Login")
        isSignUpMode = false

        // Update UI elements immediately (before animations)
        // Hide sign-up specific fields
        binding.nameInputContainer.visibility = View.GONE
        binding.usernameInputContainer.visibility = View.GONE
        binding.confirmPasswordContainer.visibility = View.GONE

        // Show login specific elements
        binding.loginOptionsContainer.visibility = View.VISIBLE
        binding.dividerContainer.visibility = View.VISIBLE
        binding.socialLoginContainer.visibility = View.VISIBLE

        // Update texts immediately
        binding.welcomeText.text = "Welcome Back"
        binding.subtitleText.text = "Sign in to continue your movie journey"
        binding.btnAuth.text = "Sign In"

        // Clear fields
        clearAllFields()

        // Update mode toggle buttons with animation
        if (indicatorInitialized) {
            animateModeToggleButtons()
        }

        // Add subtle animation after content is updated
        animateModeSwitch {
            // Content already updated, this is just for visual effect
        }
    }

    private fun setSignUpMode() {
        Log.d(TAG, "setSignUpMode() called - switching to SignUp")
        isSignUpMode = true

        // Update UI elements immediately (before animations)
        // Show sign-up specific fields
        binding.nameInputContainer.visibility = View.VISIBLE
        binding.usernameInputContainer.visibility = View.VISIBLE
        binding.confirmPasswordContainer.visibility = View.VISIBLE

        // Hide login specific elements
        binding.loginOptionsContainer.visibility = View.GONE
        binding.dividerContainer.visibility = View.GONE
        binding.socialLoginContainer.visibility = View.GONE

        // Update texts immediately
        binding.welcomeText.text = "Create Account"
        binding.subtitleText.text = "Join our movie community"
        binding.btnAuth.text = "Sign Up"

        // Clear fields
        clearAllFields()

        // Update mode toggle buttons with animation
        if (indicatorInitialized) {
            animateModeToggleButtons()
        }

        // Add subtle animation after content is updated
        animateModeSwitch {
            // Content already updated, this is just for visual effect
        }
    }

    private fun animateModeToggleButtons() {
        if (!indicatorInitialized) return

        val loginButton = binding.btnLoginMode
        val signUpButton = binding.btnSignUpMode
        val indicator = binding.modeIndicator

        // Calculate target position for indicator
        val targetTranslationX = if (isSignUpMode) {
            signUpButton.x - loginButton.x
        } else {
            0f
        }

        // Create slide animation for the indicator with bounce effect
        val indicatorAnimator = ObjectAnimator.ofFloat(indicator, "translationX", indicator.translationX, targetTranslationX)
        indicatorAnimator.duration = 400
        indicatorAnimator.interpolator = OvershootInterpolator(0.8f)

        // Add scale effect to indicator
        val scaleAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(indicator, "scaleX", 1f, 1.05f, 1f),
                ObjectAnimator.ofFloat(indicator, "scaleY", 1f, 1.05f, 1f)
            )
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Animate button states
        val buttonAnimatorSet = AnimatorSet()

        if (isSignUpMode) {
            // Sign up mode animations
            val signUpScale = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(signUpButton, "scaleX", 1f, 1.1f, 1f),
                    ObjectAnimator.ofFloat(signUpButton, "scaleY", 1f, 1.1f, 1f)
                )
                duration = 300
                interpolator = OvershootInterpolator(1.2f)
            }

            val loginScale = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(loginButton, "scaleX", 1f, 0.95f, 1f),
                    ObjectAnimator.ofFloat(loginButton, "scaleY", 1f, 0.95f, 1f)
                )
                duration = 200
                interpolator = AccelerateDecelerateInterpolator()
            }

            buttonAnimatorSet.playTogether(signUpScale, loginScale)
        } else {
            // Login mode animations
            val loginScale = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(loginButton, "scaleX", 1f, 1.1f, 1f),
                    ObjectAnimator.ofFloat(loginButton, "scaleY", 1f, 1.1f, 1f)
                )
                duration = 300
                interpolator = OvershootInterpolator(1.2f)
            }

            val signUpScale = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(signUpButton, "scaleX", 1f, 0.95f, 1f),
                    ObjectAnimator.ofFloat(signUpButton, "scaleY", 1f, 0.95f, 1f)
                )
                duration = 200
                interpolator = AccelerateDecelerateInterpolator()
            }

            buttonAnimatorSet.playTogether(loginScale, signUpScale)
        }

        // Start animations
        val masterAnimator = AnimatorSet()
        masterAnimator.playTogether(indicatorAnimator, scaleAnimator, buttonAnimatorSet)
        masterAnimator.start()

        // Update button text colors
        animateButtonColors()
    }

    private fun animateButtonColors() {
        val loginButton = binding.btnLoginMode
        val signUpButton = binding.btnSignUpMode

        // Use white text for active tab on the dark gradient background for better contrast
        val activeTextColor = resources.getColor(R.color.white, null) // White text on dark gradient background
        val inactiveTextColor = resources.getColor(R.color.text_secondary, null) // Light gray text on transparent background

        if (isSignUpMode) {
            // Animate sign up button to active (white text)
            val signUpColorAnimator = ValueAnimator.ofArgb(inactiveTextColor, activeTextColor)
            signUpColorAnimator.duration = 300
            signUpColorAnimator.addUpdateListener { animator ->
                signUpButton.setTextColor(animator.animatedValue as Int)
            }

            // Animate login button to inactive (light text)
            val loginColorAnimator = ValueAnimator.ofArgb(activeTextColor, inactiveTextColor)
            loginColorAnimator.duration = 300
            loginColorAnimator.addUpdateListener { animator ->
                loginButton.setTextColor(animator.animatedValue as Int)
            }

            signUpColorAnimator.start()
            loginColorAnimator.start()
        } else {
            // Animate login button to active (white text)
            val loginColorAnimator = ValueAnimator.ofArgb(inactiveTextColor, activeTextColor)
            loginColorAnimator.duration = 300
            loginColorAnimator.addUpdateListener { animator ->
                loginButton.setTextColor(animator.animatedValue as Int)
            }

            // Animate sign up button to inactive (light text)
            val signUpColorAnimator = ValueAnimator.ofArgb(activeTextColor, inactiveTextColor)
            signUpColorAnimator.duration = 300
            signUpColorAnimator.addUpdateListener { animator ->
                signUpButton.setTextColor(animator.animatedValue as Int)
            }

            loginColorAnimator.start()
            signUpColorAnimator.start()
        }
    }

    private fun updateButtonStates() {
        val loginButton = binding.btnLoginMode
        val signUpButton = binding.btnSignUpMode

        // Use white text for active tab on the dark gradient background for better contrast
        val activeTextColor = resources.getColor(R.color.white, null) // White text on dark gradient background
        val inactiveTextColor = resources.getColor(R.color.text_secondary, null) // Light gray text on transparent background

        if (isSignUpMode) {
            signUpButton.setTextColor(activeTextColor)
            loginButton.setTextColor(inactiveTextColor)
        } else {
            loginButton.setTextColor(activeTextColor)
            signUpButton.setTextColor(inactiveTextColor)
        }
    }

    private fun animateButtonPress(view: View, onComplete: () -> Unit) {
        val scaleDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.96f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.96f)
            )
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.96f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.96f, 1f)
            )
            duration = 150
            interpolator = OvershootInterpolator(1.5f)
        }

        scaleDown.start()
        scaleDown.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                scaleUp.start()
                onComplete()
            }
        })
    }

    private fun clearAllFields() {
        binding.nameEditText.text?.clear()
        binding.usernameEditText.text?.clear()
        binding.emailEditText.text?.clear()
        binding.passwordEditText.text?.clear()
        binding.confirmPasswordEditText.text?.clear()
        binding.rememberMeCheckbox.isChecked = false
    }

    private fun animateModeSwitch(onComplete: () -> Unit) {
        val fadeOut = ObjectAnimator.ofFloat(binding.authCard, "alpha", 1f, 0.85f)
        val scaleDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.authCard, "scaleX", 1f, 0.98f),
                ObjectAnimator.ofFloat(binding.authCard, "scaleY", 1f, 0.98f)
            )
        }

        val fadeIn = ObjectAnimator.ofFloat(binding.authCard, "alpha", 0.85f, 1f)
        val scaleUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.authCard, "scaleX", 0.98f, 1f),
                ObjectAnimator.ofFloat(binding.authCard, "scaleY", 0.98f, 1f)
            )
        }

        val exitAnimator = AnimatorSet().apply {
            playTogether(fadeOut, scaleDown)
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
        }

        val enterAnimator = AnimatorSet().apply {
            playTogether(fadeIn, scaleUp)
            duration = 300
            interpolator = OvershootInterpolator(0.5f)
        }

        exitAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onComplete()
                enterAnimator.start()
            }
        })

        exitAnimator.start()
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordToggle.setImageResource(R.drawable.ic_visibility)
        } else {
            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordToggle.setImageResource(R.drawable.ic_visibility_off)
        }

        binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
        animatePasswordToggle(binding.passwordToggle)
    }

    private fun toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible

        if (isConfirmPasswordVisible) {
            binding.confirmPasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.confirmPasswordToggle.setImageResource(R.drawable.ic_visibility)
        } else {
            binding.confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.confirmPasswordToggle.setImageResource(R.drawable.ic_visibility_off)
        }

        binding.confirmPasswordEditText.setSelection(binding.confirmPasswordEditText.text.length)
        animatePasswordToggle(binding.confirmPasswordToggle)
    }

    private fun animatePasswordToggle(toggleView: View) {
        val rotateAnimator = ObjectAnimator.ofFloat(toggleView, "rotation", 0f, 360f)
        rotateAnimator.duration = 300
        rotateAnimator.interpolator = AccelerateDecelerateInterpolator()
        rotateAnimator.start()
    }

    private fun startEntryAnimations() {
        // Initially hide all views
        binding.modeToggleContainer.alpha = 0f
        binding.modeToggleContainer.translationY = -50f
        binding.authCard.alpha = 0f
        binding.authCard.translationY = 100f
        binding.authCard.scaleX = 0.9f
        binding.authCard.scaleY = 0.9f

        // Animate mode toggle entrance with enhanced effects
        val toggleAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.modeToggleContainer, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.modeToggleContainer, "translationY", -50f, 0f),
                ObjectAnimator.ofFloat(binding.modeToggleContainer, "scaleX", 0.8f, 1f),
                ObjectAnimator.ofFloat(binding.modeToggleContainer, "scaleY", 0.8f, 1f)
            )
            duration = 700
            interpolator = OvershootInterpolator(0.8f)
            startDelay = 200
        }

        // Animate card entrance with enhanced effects
        val cardAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.authCard, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.authCard, "translationY", 100f, 0f),
                ObjectAnimator.ofFloat(binding.authCard, "scaleX", 0.9f, 1f),
                ObjectAnimator.ofFloat(binding.authCard, "scaleY", 0.9f, 1f)
            )
            duration = 800
            interpolator = OvershootInterpolator(0.8f)
            startDelay = 400
        }

        toggleAnimator.start()
        cardAnimator.start()
    }

    private fun animateAuthButton(onAnimationComplete: () -> Unit) {
        val scaleDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.btnAuth, "scaleX", 1f, 0.95f),
                ObjectAnimator.ofFloat(binding.btnAuth, "scaleY", 1f, 0.95f)
            )
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.btnAuth, "scaleX", 0.95f, 1f),
                ObjectAnimator.ofFloat(binding.btnAuth, "scaleY", 0.95f, 1f)
            )
            duration = 100
            interpolator = OvershootInterpolator(2f)
        }

        scaleDown.start()
        scaleDown.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                scaleUp.start()
                onAnimationComplete()
            }
        })
    }

    private fun animateClickFeedback(view: View, onAnimationComplete: () -> Unit) {
        val scaleAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1f)
            )
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
        }

        scaleAnimator.start()
        scaleAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onAnimationComplete()
            }
        })
    }

    private fun animateInputFocus(container: LinearLayout, hasFocus: Boolean) {
        val targetElevation = if (hasFocus) 8f else 0f
        val targetScale = if (hasFocus) 1.02f else 1f

        ObjectAnimator.ofFloat(container, "elevation", container.elevation, targetElevation).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
        }.start()

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(container, "scaleX", container.scaleX, targetScale),
                ObjectAnimator.ofFloat(container, "scaleY", container.scaleY, targetScale)
            )
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
        }.start()

        // Update background drawable to show focus state
        if (hasFocus) {
            container.setBackgroundResource(R.drawable.dark_input_background_focused)
        } else {
            container.setBackgroundResource(R.drawable.dark_input_background)
        }
    }

    // ... (rest of the methods remain the same as in your original code)
    private fun performLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        // Validate inputs
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(binding.emailInputContainer, "Enter a valid email")
            binding.emailEditText.requestFocus()
            resetButton()
            return
        }

        if (password.isEmpty()) {
            showError(binding.passwordContainer, "Password is required")
            binding.passwordEditText.requestFocus()
            resetButton()
            return
        }

        // Clear any previous errors
        clearError(binding.emailInputContainer)
        clearError(binding.passwordContainer)

        // Perform Firebase login
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // Load user data from Firestore and update UserManager
                    try {
                        val userDocument = db.collection("users")
                            .document(firebaseUser.uid)
                            .get()
                            .await()
                        
                        if (userDocument.exists()) {
                            val user = userDocument.toObject(User::class.java)
                            if (user != null) {
                                UserManager.getInstance().loginUser(user)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading user data during login: ${e.message}", e)
                    }
                    
                    Toast.makeText(this@AuthActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    animateSuccessfulAuth()
                } else {
                    Toast.makeText(this@AuthActivity, "Login failed: User is null.", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Firebase user is null after successful sign-in attempt.")
                    resetButton()
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidUserException -> "User not found or has been disabled."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid password or email address."
                    else -> "Login failed: ${e.message}"
                }
                Toast.makeText(this@AuthActivity, errorMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Login error: ${e.message}", e)
                resetButton()
                animateShake(binding.authCard)
            }
        }
    }

    private fun performSignUp() {
        val name = binding.nameEditText.text.toString().trim()
        val username = binding.usernameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        // Validate inputs
        if (!validateSignUpInputs(name, username, email, password, confirmPassword)) {
            resetButton()
            return
        }

        // Perform Firebase signup
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val newUser = User(
                        id = firebaseUser.uid,
                        name = name,
                        email = email,
                        username = username,
                        joinDate = System.currentTimeMillis(),
                        preferences = UserPreferences()
                    )

                    saveUserDataToFirestore(newUser)
                    
                    // Send email verification
                    firebaseUser.sendEmailVerification()
                        .addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Log.d(TAG, "Email verification sent.")
                                Toast.makeText(this@AuthActivity, "Account created! Please check your email for verification.", Toast.LENGTH_LONG).show()
                            } else {
                                Log.w(TAG, "Failed to send email verification.", verificationTask.exception)
                                Toast.makeText(this@AuthActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    
                    animateSuccessfulAuth()
                } else {
                    Toast.makeText(this@AuthActivity, "Sign up failed: User creation failed.", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Firebase user is null after successful creation attempt.")
                    resetButton()
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthUserCollisionException -> "The email address is already in use by another account."
                    else -> "Sign up failed: ${e.message}"
                }
                Toast.makeText(this@AuthActivity, errorMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Sign up error: ${e.message}", e)
                resetButton()
                animateShake(binding.authCard)
            }
        }
    }

    private fun validateSignUpInputs(
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

    private fun saveUserDataToFirestore(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users")
                    .document(user.id)
                    .set(user)
                    .await()
                Log.d(TAG, "User data saved to Firestore successfully for user ID: ${user.id}")
                
                // Update UserManager with the logged-in user
                withContext(Dispatchers.Main) {
                    UserManager.getInstance().loginUser(user)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving user data to Firestore: ${e.message}", e)
            }
        }
    }

    private fun showError(container: LinearLayout, message: String) {
        container.setBackgroundResource(R.drawable.dark_input_background_error)
        animateShake(container)
    }

    private fun clearError(container: LinearLayout) {
        container.setBackgroundResource(R.drawable.dark_input_background)
    }

    private fun animateShake(view: View) {
        val shakeAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        shakeAnimator.duration = 600
        shakeAnimator.start()
    }

    private fun resetButton() {
        binding.btnAuth.isEnabled = true
        binding.progressBar.visibility = View.GONE
    }

    private fun animateSuccessfulAuth() {
        binding.btnAuth.text = if (isSignUpMode) "Creating Account..." else "Logging In..."
        binding.btnAuth.isEnabled = false

        val exitAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.authCard, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(binding.authCard, "translationY", 0f, -100f),
                ObjectAnimator.ofFloat(binding.authCard, "scaleX", 1f, 0.9f),
                ObjectAnimator.ofFloat(binding.authCard, "scaleY", 1f, 0.9f),
                ObjectAnimator.ofFloat(binding.modeToggleContainer, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(binding.modeToggleContainer, "translationY", 0f, -50f)
            )
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 500
        }

        exitAnimator.start()
        exitAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                val intent = Intent(this@AuthActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        })
    }

    private fun performGoogleSignIn() {
        try {
            // Check if Google Sign-In is properly configured
            val webClientId = try {
                // Try to get the web client ID from resources
                val resourceId = resources.getIdentifier("default_web_client_id", "string", packageName)
                if (resourceId != 0) {
                    getString(resourceId)
                } else {
                    throw Exception("default_web_client_id not found")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Google Sign-In not configured: default_web_client_id not found")
                Toast.makeText(this, "Google Sign-In is not configured yet. Please use email authentication.", Toast.LENGTH_LONG).show()
                return
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@AuthActivity,
                    )
                    val credential = result.credential
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val googleIdToken = googleIdTokenCredential.idToken

                    // Authenticate with Firebase using the Google ID token
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = auth.signInWithCredential(firebaseCredential).await()
                    val firebaseUser = authResult.user

                    if (firebaseUser != null) {
                        // Check if this is a new user and save to Firestore if needed
                        if (authResult.additionalUserInfo?.isNewUser == true) {
                            val newUser = User(
                                id = firebaseUser.uid,
                                name = firebaseUser.displayName ?: "Google User",
                                email = firebaseUser.email ?: "",
                                username = firebaseUser.email?.substringBefore("@") ?: "user",
                                joinDate = System.currentTimeMillis(),
                                preferences = UserPreferences()
                            )
                            saveUserDataToFirestore(newUser)
                        }
                        
                        Toast.makeText(this@AuthActivity, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                        animateSuccessfulAuth()
                    } else {
                        Toast.makeText(this@AuthActivity, "Google Sign-In failed.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: GetCredentialException) {
                    Log.e(TAG, "Google Sign-In failed: ${e.message}", e)
                    Toast.makeText(this@AuthActivity, "Google Sign-In cancelled or failed.", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Log.e(TAG, "Google Sign-In error: ${e.message}", e)
                    Toast.makeText(this@AuthActivity, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In setup error: ${e.message}", e)
            Toast.makeText(this, "Google Sign-In is not properly configured. Please use email authentication.", Toast.LENGTH_LONG).show()
        }
    }

//    private fun performFacebookSignIn() {
//        // Implement Facebook Sign-In
//        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
//        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//            override fun onSuccess(result: LoginResult) {
//                handleFacebookAccessToken(result.accessToken)
//            }
//
//            override fun onCancel() {
//                Toast.makeText(this@AuthActivity, "Facebook login canceled.", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onError(error: FacebookException) {
//                Toast.makeText(this@AuthActivity, "Facebook login failed: ${error.message}", Toast.LENGTH_LONG).show()
//                Log.e(TAG, "Facebook login error: ${error.message}", error)
//            }
//        })
//    }

//    private fun handleFacebookAccessToken(token: AccessToken?) {
//        if (token != null) {
//            val credential = FacebookAuthProvider.getCredential(token.token)
//            CoroutineScope(Dispatchers.Main).launch {
//                try {
//                    val authResult = auth.signInWithCredential(credential).await()
//                    val firebaseUser = authResult.user
//
//                    if (firebaseUser != null) {
//                        // Check if this is a new user and save to Firestore if needed
//                        if (authResult.additionalUserInfo?.isNewUser == true) {
//                            val newUser = User(
//                                id = firebaseUser.uid,
//                                name = firebaseUser.displayName ?: "Facebook User",
//                                email = firebaseUser.email ?: "",
//                                username = firebaseUser.email?.substringBefore("@") ?: "user",
//                                joinDate = System.currentTimeMillis(),
//                                preferences = UserPreferences()
//                            )
//                            saveUserDataToFirestore(newUser)
//                        }
//
//                        Toast.makeText(this@AuthActivity, "Facebook Sign-In successful!", Toast.LENGTH_SHORT).show()
//                        animateSuccessfulAuth()
//                    } else {
//                        Toast.makeText(this@AuthActivity, "Facebook Sign-In failed.", Toast.LENGTH_LONG).show()
//                    }
//                } catch (e: Exception) {
//                    Toast.makeText(this@AuthActivity, "Facebook Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
//                    Log.e(TAG, "Facebook Sign-In error: ${e.message}", e)
//                }
//            }
//        } else {
//            Toast.makeText(this@AuthActivity, "Invalid Facebook access token.", Toast.LENGTH_LONG).show()
//        }
//    }

    private fun showForgotPasswordDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_JustForFun_Dialog)
        builder.setTitle("Reset Password")
        builder.setMessage("Enter your email address to receive password reset instructions.")

        val input = android.widget.EditText(this)
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        input.hint = "Email address"
        input.setPadding(50, 30, 50, 30)
        builder.setView(input)

        builder.setPositiveButton("Send Reset Email") { _, _ ->
            val email = input.text.toString().trim()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            
            sendPasswordResetEmail(email)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
                } else {
                    val error = task.exception?.message ?: "Failed to send reset email"
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
package com.just_for_fun.justforfun.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.LinearLayout
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.just_for_fun.justforfun.R
import com.just_for_fun.justforfun.data.models.user.User
import com.just_for_fun.justforfun.data.managers.UserManager
import com.just_for_fun.justforfun.ui.activities.AuthActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"

    // UserManager instance
    private lateinit var userManager: UserManager

    // Views
    private lateinit var nameTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var joinDateTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var userInfoContainer: LinearLayout
    private lateinit var errorTextView: TextView
    private lateinit var refreshFab: FloatingActionButton
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        // Initialize UserManager
        userManager = UserManager.getInstance()
        
        // Initialize views
        initializeViews(view)
        
        // Setup listeners
        setupListeners()
        
        // Observe user state
        observeUserState()
        
        return view
    }

    private fun initializeViews(view: View) {
        nameTextView = view.findViewById(R.id.nameTextView)
        usernameTextView = view.findViewById(R.id.usernameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        joinDateTextView = view.findViewById(R.id.joinDateTextView)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        userInfoContainer = view.findViewById(R.id.userInfoContainer)
        errorTextView = view.findViewById(R.id.errorTextView)
        refreshFab = view.findViewById(R.id.refreshFab)
        logoutButton = view.findViewById(R.id.logoutButton)
    }

    private fun setupListeners() {
        refreshFab.setOnClickListener {
            userManager.refreshUserData()
        }
        
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun observeUserState() {
        // Observe current user
        userManager.currentUser.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                displayUserData(user)
            } else {
                showError("No user data available")
            }
        })
        
        // Observe login state
        userManager.isLoggedIn.observe(viewLifecycleOwner, Observer { isLoggedIn ->
            if (!isLoggedIn) {
                // User has been logged out, redirect to auth
                redirectToAuth()
            }
        })
        
        // Observe loading state
        userManager.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                showLoading()
            }
        })
    }

    private fun displayUserData(user: User) {
        // Hide loading and error states
        loadingProgressBar.visibility = View.GONE
        errorTextView.visibility = View.GONE
        
        // Show user info container
        userInfoContainer.visibility = View.VISIBLE
        
        // Populate user data
        nameTextView.text = user.name.ifEmpty { "Not provided" }
        usernameTextView.text = if (user.username.isNotEmpty()) "@${user.username}" else "Not provided"
        emailTextView.text = user.email.ifEmpty { "Not provided" }
        
        // Format join date
        val joinDate = if (user.joinDate > 0) {
            val date = Date(user.joinDate)
            val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            formatter.format(date)
        } else {
            "Unknown"
        }
        joinDateTextView.text = joinDate
        
        Log.d(TAG, "User data displayed successfully for: ${user.name} (${user.email})")
    }

    private fun showLoading() {
        loadingProgressBar.visibility = View.VISIBLE
        userInfoContainer.visibility = View.GONE
        errorTextView.visibility = View.GONE
    }

    private fun showError(message: String) {
        loadingProgressBar.visibility = View.GONE
        userInfoContainer.visibility = View.GONE
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = message
        
        Log.e(TAG, "Error: $message")
    }
    
    private fun showLogoutConfirmationDialog() {
        context?.let { ctx ->
            MaterialAlertDialogBuilder(ctx)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    performLogout()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
    
    private fun performLogout() {
        Log.d(TAG, "User initiated logout")
        userManager.logoutUser()
    }
    
    private fun redirectToAuth() {
        Log.d(TAG, "Redirecting to authentication")
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
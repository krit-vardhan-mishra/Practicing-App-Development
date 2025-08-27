package com.just_for_fun.justforfun.data.managers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.just_for_fun.justforfun.data.models.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * UserManager - Singleton class for managing user authentication state and data
 * Similar to Context API/Redux for managing global user state
 */
class UserManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: UserManager? = null
        private const val TAG = "UserManager"
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        
        fun getInstance(): UserManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserManager().also { INSTANCE = it }
            }
        }
    }

    // Firebase instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    // SharedPreferences for local storage
    private lateinit var sharedPrefs: SharedPreferences
    
    // LiveData for reactive state management
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    /**
     * Initialize UserManager with application context
     */
    fun initialize(context: Context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Check if user was previously logged in
        val wasLoggedIn = sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false)
        val savedUserId = sharedPrefs.getString(KEY_USER_ID, null)
        
        if (wasLoggedIn && savedUserId != null && auth.currentUser != null) {
            // Auto-login: Load user data
            loadUserData(savedUserId)
        } else {
            // Clear any stale data
            clearUserSession()
        }
        
        Log.d(TAG, "UserManager initialized. Auto-login: $wasLoggedIn")
    }
    
    /**
     * Login user and save session
     */
    fun loginUser(user: User) {
        CoroutineScope(Dispatchers.Main).launch {
            _isLoading.value = true
            
            try {
                // Save to local storage
                sharedPrefs.edit()
                    .putBoolean(KEY_IS_LOGGED_IN, true)
                    .putString(KEY_USER_ID, user.id)
                    .apply()
                
                // Update LiveData
                _currentUser.value = user
                _isLoggedIn.value = true
                
                Log.d(TAG, "User logged in: ${user.name} (${user.email})")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Logout user and clear session
     */
    fun logoutUser() {
        CoroutineScope(Dispatchers.Main).launch {
            _isLoading.value = true
            
            try {
                // Sign out from Firebase
                auth.signOut()
                
                // Clear local storage
                clearUserSession()
                
                Log.d(TAG, "User logged out successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load user data from Firestore
     */
    fun loadUserData(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                _isLoading.value = true
            }
            
            try {
                val userDocument = db.collection("users")
                    .document(userId)
                    .get()
                    .await()
                
                if (userDocument.exists()) {
                    val user = userDocument.toObject(User::class.java)
                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            _currentUser.value = user
                            _isLoggedIn.value = true
                            Log.d(TAG, "User data loaded: ${user.name}")
                        } else {
                            clearUserSession()
                            Log.w(TAG, "Failed to parse user data")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        clearUserSession()
                        Log.w(TAG, "User document not found")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user data: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    clearUserSession()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }
    
    /**
     * Refresh current user data
     */
    fun refreshUserData() {
        _currentUser.value?.let { user ->
            loadUserData(user.id)
        }
    }
    
    /**
     * Update user data in Firestore and local state
     */
    fun updateUserData(updatedUser: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users")
                    .document(updatedUser.id)
                    .set(updatedUser)
                    .await()
                
                withContext(Dispatchers.Main) {
                    _currentUser.value = updatedUser
                    Log.d(TAG, "User data updated: ${updatedUser.name}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating user data: ${e.message}", e)
            }
        }
    }
    
    /**
     * Clear user session and reset state
     */
    private fun clearUserSession() {
        sharedPrefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_ID)
            .apply()
        
        _currentUser.value = null
        _isLoggedIn.value = false
        
        Log.d(TAG, "User session cleared")
    }
    
    /**
     * Check if user is currently authenticated
     */
    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null && _isLoggedIn.value == true
    }
    
    /**
     * Get current Firebase user ID
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
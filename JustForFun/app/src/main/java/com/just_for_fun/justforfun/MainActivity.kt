package com.just_for_fun.justforfun

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.just_for_fun.justforfun.data.managers.UserManager
import com.just_for_fun.justforfun.ui.activities.AuthActivity
import com.just_for_fun.justforfun.ui.fragments.FavoritesFragment
import com.just_for_fun.justforfun.ui.fragments.HomeFragment
import com.just_for_fun.justforfun.ui.fragments.MoviesFragment
import com.just_for_fun.justforfun.ui.fragments.ProfileFragment
import com.just_for_fun.justforfun.ui.fragments.WatchlistFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UserManager
        userManager = UserManager.getInstance()
        
        // Check authentication state
        observeAuthState()

        initializeViews()
        setupBottomNavigation()

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun initializeViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_movies -> {
                    loadFragment(MoviesFragment())
                    true
                }
                R.id.nav_watchlist -> {
                    loadFragment(WatchlistFragment())
                    true
                }
                R.id.nav_favorites -> {
                    loadFragment(FavoritesFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    private fun observeAuthState() {
        // If user is not authenticated, redirect to auth
        if (!userManager.isUserAuthenticated()) {
            redirectToAuth()
            return
        }
        
        // Observe login state changes
        userManager.isLoggedIn.observe(this, Observer { isLoggedIn ->
            if (!isLoggedIn) {
                redirectToAuth()
            }
        })
    }
    
    private fun redirectToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}


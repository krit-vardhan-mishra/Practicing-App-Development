package com.just_for_fun.justforfun.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.just_for_fun.justforfun.R
import com.just_for_fun.justforfun.ui.adapters.ImageSliderAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorContainer: LinearLayout
    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        viewPager.currentItem = (viewPager.currentItem + 1) % imageList.size
    }

    // List of images to display - replace with your actual resource IDs
    private val imageList = listOf(
        R.drawable.a2,
        R.drawable.a4,
        R.drawable.b3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable menu in fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        // Initialize ViewPager
        setupImageSlider(view)
    }

    private fun setupImageSlider(view: View) {
        viewPager = view.findViewById(R.id.viewPager)
        indicatorContainer = view.findViewById(R.id.indicatorContainer)

        val adapter = ImageSliderAdapter(imageList)
        viewPager.adapter = adapter

        // Setup indicators
        setupIndicators()

        // Setup auto-scrolling
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000) // Change slide every 3 seconds
            }
        })
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(imageList.size)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.indicator_inactive
                )
            )
            indicators[i]?.layoutParams = layoutParams
            indicatorContainer.addView(indicators[i])
        }

        // Set the first indicator as active
        if (indicators.isNotEmpty()) {
            indicators[0]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.indicator_active
                )
            )
        }
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until indicatorContainer.childCount) {
            val imageView = indicatorContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showToast("Search clicked")
                true
            }
            R.id.action_filter -> {
                showToast("Filter clicked")
                true
            }
            R.id.action_sort -> {
                showToast("Sort clicked")
                true
            }
            R.id.action_settings -> {
                showToast("Settings clicked")
                true
            }
            R.id.action_about -> {
                showToast("About clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
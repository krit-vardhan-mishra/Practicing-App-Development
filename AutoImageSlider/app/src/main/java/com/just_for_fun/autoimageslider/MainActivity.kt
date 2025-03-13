package com.just_for_fun.autoimageslider

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var indicators: ArrayList<ImageView>

    private val scrollHandler = Handler(Looper.getMainLooper())
    private var scrollRunnable: Runnable? = null
    private val scrollInterval = 3000L

    // Sample images - replace with your actual image resources
    private val images = listOf(
        R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four,
        R.drawable.five, R.drawable.six, R.drawable.eight, R.drawable.nine,
        R.drawable.ten, R.drawable.eleven, R.drawable.twelve, R.drawable.thirteen,
        R.drawable.fourteen, R.drawable.fifteen
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        indicatorContainer = findViewById(R.id.indicatorContainer)

        setupViewPager()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun setupViewPager() {
        val adapter = ImageSliderAdapter(images)
        viewPager.adapter = adapter

        // Start from the middle to create infinite scroll effect
        val middlePosition = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % images.size
        viewPager.setCurrentItem(middlePosition, false)

        // Reduce slide sensitivity
        viewPager.offscreenPageLimit = 3

        // Set page transformer for the effect
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
            page.scaleX = 0.85f + r * 0.15f

            // Adjust alpha for a better looking effect
            page.alpha = 0.5f + r * 0.5f
        }

        viewPager.setPageTransformer(compositePageTransformer)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Update indicator
                setCurrentIndicator(position % images.size)

                // Auto scroll functionality can be added here with Handler
            }
        })
    }

    private fun setupIndicators() {
        indicators = ArrayList()

        // Clear any existing indicators
        indicatorContainer.removeAllViews()

        for (i in images.indices) {
            val indicator = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(8, 0, 8, 0)
            indicator.layoutParams = layoutParams

            indicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive))

            indicatorContainer.addView(indicator)
            indicators.add(indicator)
        }
    }

    private fun setCurrentIndicator(position: Int) {
        for (i in indicators.indices) {
            val drawable = if (i == position) {
                R.drawable.indicator_active
            } else {
                R.drawable.indicator_inactive
            }
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, drawable))
        }
    }

    // Auto-scroll functionality
    private fun startAutoScroll() {
        stopAutoScroll() // Clear any existing auto-scroll

        scrollRunnable = Runnable {
            val currentItem = viewPager.currentItem
            viewPager.setCurrentItem(currentItem + 1, true)
            scrollHandler.postDelayed(scrollRunnable!!, scrollInterval)
        }

        scrollHandler.postDelayed(scrollRunnable!!, scrollInterval)
    }

    private fun stopAutoScroll() {
        scrollRunnable?.let {
            scrollHandler.removeCallbacks(it)
        }
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }
}
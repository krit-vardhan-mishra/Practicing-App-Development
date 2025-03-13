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

        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.page_margin)
        val offsetPx = resources.getDimensionPixelOffset(R.dimen.page_offset)
        viewPager.setPadding(offsetPx, 0, offsetPx, 0)
        viewPager.clipToPadding = false
        viewPager.clipChildren = false

        val middlePosition = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % images.size
        viewPager.setCurrentItem(middlePosition, false)

        viewPager.offscreenPageLimit = 3

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(pageMarginPx))

        compositePageTransformer.addTransformer { page, position ->
            val scaleFactor = 0.85f + (1 - abs(position)) * 0.15f
            page.scaleY = scaleFactor
            page.scaleX = scaleFactor
        }

        viewPager.setPageTransformer(compositePageTransformer)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentIndicator(position % images.size)
            }
        })
    }

    private fun setupIndicators() {
        indicators = ArrayList()

        indicatorContainer.removeAllViews()

        for (i in images.indices) {
            val indicator = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(8, 0, 8, 0)
            indicator.layoutParams = layoutParams

            indicator.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.indicator_inactive
                )
            )

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

    private fun startAutoScroll() {
        stopAutoScroll()

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
package com.just_for_fun.autoimageslider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.os.Handler
import android.os.Looper
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: PosterPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1) Find Views
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tab_layout)

        // 2) Prepare data
        val posters = listOf(
            PosterItem(R.drawable.one, "Jawaan 1"),
            PosterItem(R.drawable.two, "Brahmastra"),
            PosterItem(R.drawable.three, "Kalki 2894AD"),
            PosterItem(R.drawable.four, "Jawaan 2"),
            PosterItem(R.drawable.five, "Bhavesh Joshi SuperHero 1"),
            PosterItem(R.drawable.six, "Bhavesh Joshi SuperHero 2"),
            PosterItem(R.drawable.eight, "Jigra"),
            PosterItem(R.drawable.nine, "Mard Ko Dard Nahi Hota"),
            PosterItem(R.drawable.ten, "Jawaan 3"),
            PosterItem(R.drawable.eleven, "Baahubali"),
            PosterItem(R.drawable.twelve, "Vikram"),
            PosterItem(R.drawable.thirteen, "War"),
            PosterItem(R.drawable.fourteen, "Shivaay 1"),
            PosterItem(R.drawable.fifteen, "Shivaay 2")
        )

        // 3) Set up Adapter
        adapter = PosterPagerAdapter(posters)
        viewPager.adapter = adapter

        // 4) Optional: Adjust offscreen page limit so the adjacent items remain loaded
        viewPager.offscreenPageLimit = 3

        // 5) Make adjacent items partially visible
        //    The XML padding + the transformation below will show partial next/previous
        val pageMarginPx = resources.getDimensionPixelSize(R.dimen.viewpager_page_margin)
        val pageTransformer = ViewPager2.PageTransformer { page, position ->
            // This reduces the page’s translation so adjacent pages are partially visible
            page.translationX = -pageMarginPx * position
        }
        viewPager.setPageTransformer(pageTransformer)

        // 6) Attach TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // If you want custom text or icons in the tab indicators, set them here.
            // By default, it will just create the correct number of dots.
        }.attach()

        // 7) (Optional) Start auto-scroll
        autoScrollViewPager()
    }

    // (Optional) Auto-scroll with a Timer/Handler
    private fun autoScrollViewPager() {
        val handler = Handler(Looper.getMainLooper())
        var currentPage = 0
        val update = Runnable {
            if (currentPage == adapter.itemCount) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        val timer = Timer()
        // Schedule the task to run after DELAY_MS, and repeat every PERIOD_MS
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 2000, 3000) // (delay = 2 seconds, period = 3 seconds)
    }
}

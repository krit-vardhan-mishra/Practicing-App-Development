package com.just_for_fun.autoimageslider

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class SliderPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val scaleFactor = 0.85f + (1 - abs(position)) * 0.15f
        page.scaleX = scaleFactor
        page.scaleY = scaleFactor
        page.alpha = 0.5f + (1 - abs(position)) * 0.5f // Fades edges slightly for smooth effect
    }
}


//class SliderPageTransformer : ViewPager2.PageTransformer {
//    override fun transformPage(page: View, position: Float) {
//        val offset = abs(position)
//        val scale = 1 - (offset * 0.15f)
//        val translation = page.width * position
//
//        page.scaleX = scale
//        page.scaleY = scale
//        page.translationX = translation
//    }
//}
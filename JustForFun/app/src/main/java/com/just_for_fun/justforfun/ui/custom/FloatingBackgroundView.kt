package com.just_for_fun.justforfun.ui.custom

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import com.just_for_fun.justforfun.R
import kotlin.random.Random

class FloatingBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val moviePosters = arrayOf(
        R.drawable.a1,
        R.drawable.a2,
        R.drawable.a3,
        R.drawable.a4,
        R.drawable.a5,
        R.drawable.b1,
        R.drawable.b2,
        R.drawable.b4,
        R.drawable.b5,
        R.drawable.b6
    )

    private val floatingViews = mutableListOf<ImageView>()
    private val animatorSets = mutableListOf<AnimatorSet>()

    init {
        post {
            createFloatingImages()
        }
    }

    private fun createFloatingImages() {
        removeAllViews()
        floatingViews.clear()

        val numberOfImages = 12
        val screenWidth = width
        val screenHeight = height

        if (screenWidth <= 0 || screenHeight <= 0) return

        for (i in 0 until numberOfImages) {
            val imageWidth = dpToPx(Random.nextInt(60, 90))
            val imageHeight = dpToPx(Random.nextInt(90, 130))

            val imageView = ImageView(context).apply {
                layoutParams = LayoutParams(
                    imageWidth,
                    imageHeight
                ).apply {
                    leftMargin = Random.nextInt(-imageWidth / 2, screenWidth - imageWidth / 2)
                    topMargin = Random.nextInt(-imageHeight / 2, screenHeight - imageHeight / 2)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(moviePosters[i % moviePosters.size])
                alpha = Random.nextFloat() * 0.2f + 0.1f // Very subtle for dark theme

                rotation = Random.nextFloat() * 360f
                scaleX = Random.nextFloat() * 0.3f + 0.5f
                scaleY = scaleX
            }

            addView(imageView)
            floatingViews.add(imageView)
        }
    }

    fun startFloatingAnimation() {
        stopFloatingAnimation()

        if (floatingViews.isEmpty()) {
            createFloatingImages()
        }

        floatingViews.forEachIndexed { index, imageView ->
            val animatorSet = AnimatorSet()

            val translateX = ObjectAnimator.ofFloat(
                imageView, "translationX",
                imageView.translationX,
                imageView.translationX + Random.nextFloat() * 150f - 75f
            ).apply {
                duration = Random.nextLong(10000, 25000)
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
            }

            val translateY = ObjectAnimator.ofFloat(
                imageView, "translationY",
                imageView.translationY,
                imageView.translationY + Random.nextFloat() * 200f - 100f
            ).apply {
                duration = Random.nextLong(12000, 30000)
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
            }

            val rotation = ObjectAnimator.ofFloat(
                imageView, "rotation",
                imageView.rotation,
                imageView.rotation + (Random.nextFloat() * 40 - 20f)
            ).apply {
                duration = Random.nextLong(20000, 40000)
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = LinearInterpolator()
            }

            val currentScale = imageView.scaleX
            val scaleX = ObjectAnimator.ofFloat(
                imageView, "scaleX",
                currentScale,
                currentScale + Random.nextFloat() * 0.2f - 0.1f
            ).apply {
                duration = Random.nextLong(10000, 20000)
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
            }

            val scaleY = ObjectAnimator.ofFloat(
                imageView, "scaleY",
                currentScale,
                currentScale + Random.nextFloat() * 0.2f - 0.1f
            ).apply {
                duration = Random.nextLong(10000, 20000)
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
            }

            val alpha = ObjectAnimator.ofFloat(
                imageView, "alpha",
                imageView.alpha,
                Random.nextFloat() * 0.15f + 0.05f
            ).apply {
                duration = Random.nextLong(8000, 15000)
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
            }

            animatorSet.playTogether(translateX, translateY, rotation, scaleX, scaleY, alpha)
            animatorSet.startDelay = (index * 800).toLong()
            animatorSets.add(animatorSet)

            animatorSet.start()
        }
    }

    fun stopFloatingAnimation() {
        animatorSets.forEach {
            it.cancel()
        }
        animatorSets.clear()
    }

    fun cleanup() {
        stopFloatingAnimation()
        removeAllViews()
        floatingViews.clear()
        animatorSets.clear()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopFloatingAnimation()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            post {
                createFloatingImages()
                if (animatorSets.isNotEmpty()) {
                    startFloatingAnimation()
                }
            }
        }
    }
}
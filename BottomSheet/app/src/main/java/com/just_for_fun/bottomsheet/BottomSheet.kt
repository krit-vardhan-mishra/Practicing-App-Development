package com.just_for_fun.bottomsheet

import android.os.Bundle
import android.view.ViewStub
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.just_for_fun.bottomsheet.databinding.ActivityBottomSheetBinding

class BottomSheet : AppCompatActivity() {
    private lateinit var binding: ActivityBottomSheetBinding
    private lateinit var arrowImageView: ImageView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arrowImageView = binding.arrow
        bottomSheetBehavior = BottomSheetBehavior.from(binding.sheet)

        bottomSheetBehavior.apply {
            peekHeight = 200
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        updateArrowDrawable(bottomSheetBehavior.state)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: android.view.View, newState: Int) {
                updateArrowDrawable(newState)
            }

            override fun onSlide(bottomSheet: android.view.View, slideOffset: Float) {}
        })
    }

    private fun updateArrowDrawable(state: Int) {
        when (state) {
            BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> {
                arrowImageView.setImageResource(R.drawable.baseline_arrow_drop_up_24)
            }
            BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                arrowImageView.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                includeExtraLayout()
            }
            else -> {}
        }
    }

    private fun includeExtraLayout() {
        val viewStub = binding.sheet.findViewById<ViewStub>(R.id.extra_layout)
        if (viewStub != null && viewStub.parent != null) {
            viewStub.inflate()
            bottomSheetBehavior.peekHeight = 100
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

}
package com.just_for_fun.justforfun.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.just_for_fun.justforfun.R

class WatchlistFragment : Fragment() {
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        return inflater.inflate(R.layout.fragment_watchlist, container, false)
    }
}
package com.just_for_fun.justforfun.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.just_for_fun.justforfun.R

class MoviesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup your other UI components here
        setupRecyclerView(view)
    }


    private fun setupRecyclerView(view: View) {
        // Setup your RecyclerView here for movies list
        // val recyclerView = view.findViewById<RecyclerView>(R.id.moviesRecyclerView)
        // recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        // recyclerView.adapter = yourMoviesAdapter
    }
}
package com.just_for_fun.justforfun.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.just_for_fun.justforfun.data.models.tmdb.award.media.Movie
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveMovie(movie: Movie) {
        firestore.collection("movies")
            .document(movie.id)
            .set(movie)
            .await()
    }

    suspend fun getMovie(movieId: String): Movie? {
        return firestore.collection("movies")
            .document(movieId)
            .get()
            .await()
            .toObject<Movie>()
    }

}
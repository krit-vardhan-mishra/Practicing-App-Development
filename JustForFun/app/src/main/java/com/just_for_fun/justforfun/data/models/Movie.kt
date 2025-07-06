package com.just_for_fun.justforfun.data.models

data class Movie(
    val id: String,                 // unique ID of movie
    val name: String,               // name of the movie
    val releaseYear: String,        // release year DD-MM-YYYY
    val moviePoster: String,        // movie poster url
    val backdropImage: String? = null,    // backdrop image url
    val description: String,        // description of the movie (synopsis)
    val rating: Float = 0.0f,              // average rating of the movie
    val ratingCount: Int = 0,               // number of ratings
    val genre: List<Genre> = emptyList(),         // genre of the movie from enum class Genre
    val director: List<String> = emptyList(),     // list of directors IDs of the movie
    val totalAwards: Int = 0,           // total number of the awards that the movie have
    val awards: List<String> = emptyList(),       // the IDs of Awards
    val reviews: List<String> = emptyList(),      // IDs of the review that the movie have
    val moreLikeThis: List<String> = emptyList(), // IDs of the movies which have same type of Genre
    val budget: String? = null,             // budget of the movie
    val boxOffice: String? = null,          // box office collection of the movie both worldwide and domestic
    val writers: List<String> = emptyList(),      // list of IDs of the writer
    val cast: Map<String, String> = emptyMap(),  // map of of cast where key of map will have ID of cast and value will have the role they plays
    val runtime: Int,            // total runtime of the movie in both mins and hours
    val language: String,           // original language
    val country: String,            // country of origin
    val imdbId: String? = null,     // IMDB ID for external reference
    val tmdbId: String? = null,     // TMDB ID for external reference
    val userRating: Float? = null,          // rating given by the user
    val isWatched: Boolean = false,         // has the user watch the movie or not
    val isLiked: Boolean = false,           // the user like the movie or not
    val isFavourite: Boolean = false,       // is user added the movie to his favourite list or not
    val isMarkedToWatch: Boolean = false    // marked to store it to watch later
)
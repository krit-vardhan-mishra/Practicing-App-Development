package com.just_for_fun.justforfun.data.models.tmdb.award.media

import com.just_for_fun.justforfun.data.models.tmdb.award.person.PersonInMovie

// Represents a movie in the application
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
    val totalAwards: Int = 0,           // total number of the awards that the movie have
    val awards: List<String> = emptyList(),       // the IDs of Awards
    val reviews: List<String> = emptyList(),      // IDs of the review that the movie have
    val moreLikeThis: List<String> = emptyList(), // IDs of the movies which have same type of Genre
    val budget: String? = null,             // budget of the movie
    val boxOffice: String? = null,          // box office collection of the movie both worldwide and domestic
    val runtime: Int,            // total runtime of the movie in both mins and hours
    val language: String,           // original language
    val country: String,            // country of origin
    val imdbId: String? = null,     // IMDB ID for external reference
    val tmdbId: String? = null,     // TMDB ID for external reference
    val userRating: Float? = null,          // rating given by the user
    val isWatched: Boolean = false,         // has the user watch the movie or not
    val isLiked: Boolean = false,           // the user like the movie or not
    val isFavourite: Boolean = false,       // is user added the movie to his favourite list or not
    val isMarkedToWatch: Boolean = false,   // marked to store it to watch later

    // Enhanced cast and crew using PersonInMovie
    val cast: List<PersonInMovie> = emptyList(),        // Complete cast information
    val directors: List<PersonInMovie> = emptyList(),   // Directors with full info
    val writers: List<PersonInMovie> = emptyList(),     // Writers with full info
    val producers: List<PersonInMovie> = emptyList(),   // Producers with full info
    val crew: List<PersonInMovie> = emptyList(),        // Other crew members

    // Additional movie metadata
    val certification: String? = null,      // Movie rating (PG, PG-13, R, etc.)
    val keywords: List<String> = emptyList(), // Keywords/tags for the movie
    val tagline: String? = null,            // Movie tagline
    val homepage: String? = null,           // Official website
    val trailer: String? = null,            // Trailer URL
    val productionCompanies: List<String> = emptyList(), // Production company IDs
    val distributors: List<String> = emptyList(),        // Distributor company IDs
    val sequels: List<String> = emptyList(),             // Sequel movie IDs
    val prequels: List<String> = emptyList(),            // Prequel movie IDs
    val trivia: List<String> = emptyList(),              // Movie trivia/facts
    val goofs: List<String> = emptyList(),               // Movie mistakes/goofs
    val quotes: List<String> = emptyList(),              // Famous quotes from movie
    val soundtracks: List<String> = emptyList(),         // Soundtrack IDs
    val alternativeTitles: Map<String, String> = emptyMap(), // Alternative titles by country
    val isAdultContent: Boolean = false,     // Adult content flag
    val originalTitle: String? = null,       // Original title if different
    val popularity: Float = 0.0f,           // Popularity score
    val voteAverage: Float = 0.0f,          // Vote average from external sources
    val voteCount: Int = 0                  // Vote count from external sources
)

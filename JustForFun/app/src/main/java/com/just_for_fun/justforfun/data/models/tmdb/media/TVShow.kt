package com.just_for_fun.justforfun.data.models.tmdb.award.media

// Represents a TV show with various details
data class TVShow(
    val id: String,                 // unique ID of tv show
    val name: String,               // name of the tv show
    val startingYear: String,       // starting year DD-MM-YYYY
    val endingYear: String? = null,         // ending year of the series if not then '-' will use
    val tvShowPoster: String,       // tv show poster url
    val backdropImage: String? = null,       // backdrop image url
    val description: String,        // description of the tv show (synopsis)
    val rating: Float = 0.0f,              // average rating of the tv show
    val ratingCount: Int = 0,               // number of ratings
    val genre: List<Genre> = emptyList(),         // genre of the tv show from enum class Genre
    val showRunners: List<String> = emptyList(),   // have the IDs of the show runners of the show
    val director: List<String> = emptyList(),     // list of directors IDs of the tv show
    val totalAwards: Int = 0,           // total number of the awards that the tv show have
    val awards: List<String> = emptyList(),       // the IDs of Awards
    val reviews: List<String> = emptyList(),      // IDs of the review that the tv show have
    val moreLikeThis: List<String> = emptyList(), // IDs of the tv shows which have same type of Genre
    val seasons: Int,               // number of the seasons show have
    val episodes: String,           // number of episodes show have
    val writers: List<String> = emptyList(),      // list of IDs of the writer
    val cast: Map<String, String> = emptyMap(),  // map of of cast where key of map will have ID of cast and value will have the role they plays
    val averageRuntime: String,            // average runtime of the episode
    val language: String,            // original language
    val country: String,             // country of origin
    val network: String? = null,     // network that aired the show
    val status: String,             // "Ongoing", "Ended", "Cancelled"
    val imdbId: String? = null,     // IMDB ID for external reference
    val tmdbId: String? = null,     // TMDB ID for external reference
    val userRating: Float? = null,          // rating given by the user
    val isWatched: Boolean = false,         // has the user watch the tv show or not
    val isLiked: Boolean = false,           // the user like the tv show or not
    val isFavourite: Boolean = false,       // is user added the tv show to his favourite list or not
    val isMarkedToWatch: Boolean = false    // marked to store it to watch later
)

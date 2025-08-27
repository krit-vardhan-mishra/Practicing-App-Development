package com.just_for_fun.justforfun.data.models.tmdb.award.media

// Represents a TV show with various details
data class TVShow(
    val id: String = "",                    // unique ID of tv show (TMDB ID)
    val name: String = "",                  // name of the tv show
    val startDate: Long = 0L,               // starting date as timestamp
    val endDate: Long? = null,              // ending date as timestamp (null if ongoing)
    val startingYear: String = "",          // starting year (for display)
    val endingYear: String = "",            // ending year (for display, empty if ongoing)
    val tvShowPoster: String = "",          // tv show poster url
    val backdropImage: String = "",         // backdrop image url
    val description: String = "",           // description of the tv show (synopsis)
    val rating: Float = 0.0f,               // average rating of the tv show
    val ratingCount: Int = 0,               // number of ratings
    val genre: List<Genre> = emptyList(),   // genre of the tv show from enum class Genre
    val showRunners: List<String> = emptyList(), // have the IDs of the show runners of the show
    val director: List<String> = emptyList(), // list of directors IDs of the tv show
    val totalAwards: Int = 0,               // total number of the awards that the tv show have
    val awards: List<String> = emptyList(), // the IDs of Awards
    val reviews: List<String> = emptyList(), // IDs of the review that the tv show have
    val moreLikeThis: List<String> = emptyList(), // IDs of the tv shows which have same type of Genre
    val seasons: Int = 0,                   // number of the seasons show have
    val totalEpisodes: Int = 0,             // total number of episodes
    val writers: List<String> = emptyList(), // list of IDs of the writer
    val cast: Map<String, String> = emptyMap(), // map of cast where key=cast ID, value=role
    val averageRuntime: Int = 0,            // average runtime of the episode in minutes
    val language: String = "",              // original language
    val country: String = "",               // country of origin
    val network: String = "",               // network that aired the show
    val status: String = "",                // "Ongoing", "Ended", "Cancelled"
    val imdbId: String = "",                // IMDB ID for external reference
    val tmdbId: String = "",                // TMDB ID for external reference
    val userRating: Float = 0.0f,           // rating given by the user (0 if not rated)
    val isWatched: Boolean = false,         // has the user watch the tv show or not
    val isLiked: Boolean = false,           // the user like the tv show or not
    val isFavourite: Boolean = false,       // is user added the tv show to his favourite list or not
    val isMarkedToWatch: Boolean = false,   // marked to store it to watch later
    
    // Additional TV show metadata
    val certification: String = "",         // Content rating
    val keywords: List<String> = emptyList(), // Keywords/tags
    val tagline: String = "",               // Show tagline
    val homepage: String = "",              // Official website
    val trailer: String = "",               // Trailer URL
    val productionCompanies: List<String> = emptyList(), // Production company IDs
    val isAdultContent: Boolean = false,    // Adult content flag
    val originalTitle: String = "",         // Original title if different
    val popularity: Float = 0.0f,           // Popularity score
    val voteAverage: Float = 0.0f,          // Vote average from external sources
    val voteCount: Int = 0                  // Vote count from external sources
)

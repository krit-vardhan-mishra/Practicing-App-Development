package com.just_for_fun.justforfun.data.api

data class TMDBResponse<T>(
    val page: Int,
    val results: List<T>,
    val total_pages: Int,
    val total_results: Int
)

data class TMDBMovie(
    val id: Int,
    val title: String,
    val overview: String,
    val release_date: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val vote_average: Float,
    val vote_count: Int,
    val genre_ids: List<Int>,
    val runtime: Int?,
    val original_language: String
)
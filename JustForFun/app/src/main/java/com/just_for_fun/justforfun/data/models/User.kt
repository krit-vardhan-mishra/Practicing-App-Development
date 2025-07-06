package com.just_for_fun.justforfun.data.models

data class User(
    val id: String,                          // unique ID for the user
    val name: String,                        // name of the user
    val email: String,                       // email ID of user, required for authentication
    val username: String,                    // unique username for public display and distinguishing the user from other
    val bio: String = "",                         // optional bio
    val profilePicture: String = "",              // profile photo url that will uploaded on firebase, so next time user logged in then it shows
    val joinDate: Long,                      // timestamp of user joining
    val location: String? = null,            // optional
    val website: String? = null,             // optional
    val following: List<String> = emptyList(),             // this list will have id of user IDs followed
    val followers: List<String> = emptyList(),             // this list will have id of user IDs following
    val favourites: List<String> = emptyList(),            // list of movies/tv shows IDs that user have added to favourite
    val watchList: List<String> = emptyList(),             // list of movies/tv shows IDs that user have added to watch later
    val reviews: List<String> = emptyList(),               // list of IDs of reviews written by the user to movies/tv shows
    val ratings: List<String> = emptyList(),               // list of IDs of rating given by the user to movies/tv shows
    val likedReviews: List<String> = emptyList(),          // list of IDs of reviews that user have liked
    val likedReplies: List<String> = emptyList(),       // track which replies user liked (like likedReviews)
    val replies: List<Reply> = emptyList(),                // list of replies that user have made
    val watchHistory: List<String> = emptyList(),          // list of IDs of movies/tv shows that user have watched and marked completed
    val socialLinks: Map<String, String>  = emptyMap(),   // list of user's other social media IDs
    val topGenres: List<String> = emptyList(),             // list of most watched genres by user
    val customListMovies: List<String> = emptyList(),      // list of IDs of movies that user has created custom to showcase on his ID
    val customShowMovies: List<String> = emptyList(),      // list of IDs of tv shows that user has created custom to showcase on his ID
    val blockedUsers: List<String> = emptyList(),          // list of IDs of users that user have blocked
    val preferences: UserPreferences = UserPreferences(),        // setting that user have set for his account
    val isPrivate: Boolean = false,                  // if user had marked private then only his follower and following can see his account details
    val isVerified: Boolean = false,                 // verified user badge
    val lastActivateDate: Long? = null,             // timestamp of last activity
    val totalReviews: Int = 0,                  // cached count of total reviews
    val totalRatings: Int = 0,              // cache count of total ratings
    val averageRating: Float = 0.0f         // user's average rating across all movies/shows
)
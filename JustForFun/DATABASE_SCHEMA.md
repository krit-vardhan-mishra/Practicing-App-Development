# Firestore Database Schema

## Overview
This document outlines the complete Firestore database structure for the JustForFun movie/TV app.

## Collections

### 1. `users` Collection
**Purpose**: Store user profiles and account information
**Document ID**: Firebase Auth UID

```kotlin
// User.kt
data class User(
    val id: String,                          // Firebase Auth UID
    val name: String,                        // Display name
    val email: String,                       // Email address
    val username: String,                    // Unique username
    val bio: String = "",                    // User bio
    val profilePicture: String = "",         // Profile image URL
    val joinDate: Long,                      // Account creation timestamp
    val location: String? = null,            // User location
    val website: String? = null,             // Personal website
    val following: List<String> = emptyList(), // Following user IDs
    val followers: List<String> = emptyList(), // Follower user IDs
    val favourites: List<String> = emptyList(), // Favorite media IDs
    val watchList: List<String> = emptyList(), // Watchlist item IDs
    val reviews: List<String> = emptyList(),   // Review IDs
    val ratings: List<String> = emptyList(),   // Rating IDs
    val likedReviews: List<String> = emptyList(), // Liked review IDs
    val likedReplies: List<String> = emptyList(), // Liked reply IDs
    val replies: List<String> = emptyList(),      // Reply IDs
    val watchHistory: List<String> = emptyList(), // Watched media IDs
    val socialLinks: Map<String, String> = emptyMap(), // Social media links
    val topGenres: List<String> = emptyList(),    // Preferred genres
    val customListMovies: List<String> = emptyList(), // Custom movie list IDs
    val customShowMovies: List<String> = emptyList(), // Custom TV list IDs
    val blockedUsers: List<String> = emptyList(),     // Blocked user IDs
    val preferences: UserPreferences = UserPreferences(), // User settings
    val isPrivate: Boolean = false,              // Private profile flag
    val isVerified: Boolean = false,             // Verified user badge
    val lastActivateDate: Long? = null,          // Last activity timestamp
    val totalReviews: Int = 0,                   // Cached review count
    val totalRatings: Int = 0,                   // Cached rating count
    val averageRating: Float = 0.0f              // Average user rating
)
```

### 2. `reviews` Collection
**Purpose**: Store user reviews for movies and TV shows
**Document ID**: Auto-generated

```kotlin
// Review.kt (in user.review package)
data class Review(
    val id: String = "",                     // Auto-generated ID
    val mediaId: String = "",                // TMDB movie/TV ID
    val mediaType: String = "",              // "movie" or "tv"
    val userId: String = "",                 // Author user ID
    val userName: String = "",               // Author name (cached)
    val userAvatar: String = "",             // Author avatar URL (cached)
    val rating: Float = 0.0f,                // Rating (1-10)
    val title: String = "",                  // Review title
    val content: String = "",                // Review content
    val timestamp: Long = 0L,                // Creation timestamp
    val lastModified: Long = 0L,             // Last edit timestamp
    val isVerifiedWatch: Boolean = false,    // Verified viewing
    val helpfulVotes: Int = 0,               // Helpful vote count
    val totalVotes: Int = 0,                 // Total vote count
    val isSpoiler: Boolean = false,          // Spoiler flag
    val isVerified: Boolean = false,         // Verified review
    val reviewType: ReviewType = ReviewType.USER, // Review type
    val pros: List<String> = emptyList(),    // Positive aspects
    val cons: List<String> = emptyList(),    // Negative aspects
    val wouldRecommend: Boolean? = null,     // Recommendation flag
    val watchedDate: Long? = null,           // Watch timestamp
    val watchedWith: String = "",            // Viewing companions
    val watchedWhere: String = "",           // Viewing location
    val tags: List<String> = emptyList(),    // Review tags
    val replies: List<String> = emptyList(), // Reply IDs
    val likes: List<String> = emptyList(),   // Liked by user IDs
    val likesCount: Int = 0,                 // Cached like count
    val isEdited: Boolean = false,           // Edit flag
    val season: Int? = null,                 // TV season (for TV shows)
    val episode: Int? = null                 // TV episode (for TV shows)
)
```

### 3. `replies` Collection
**Purpose**: Store replies to reviews and other replies
**Document ID**: Auto-generated

```kotlin
// Reply.kt (in user.review package)
data class Reply(
    val id: String = "",                     // Auto-generated ID
    val userId: String = "",                 // Author user ID
    val parentId: String = "",               // Parent review/reply ID
    val parentType: ParentType = ParentType.REVIEW, // Parent type
    val userProfilePhoto: String = "",       // Author avatar (cached)
    val userName: String = "",               // Author name (cached)
    val replyText: String = "",              // Reply content
    val timestamp: Long = 0L,                // Creation timestamp
    val lastModified: Long = 0L,             // Last edit timestamp
    val likes: List<String> = emptyList(),   // Liked by user IDs
    val likesCount: Int = 0,                 // Cached like count
    val dislikes: List<String> = emptyList(), // Disliked by user IDs
    val dislikesCount: Int = 0,              // Cached dislike count
    val replies: List<String> = emptyList(), // Nested reply IDs
    val isEdited: Boolean = false,           // Edit flag
    val isSpoiler: Boolean = false,          // Spoiler flag
    val isVerified: Boolean = false          // Verified user flag
)
```

### 4. `ratings` Collection
**Purpose**: Store user ratings for movies and TV shows
**Document ID**: Auto-generated

```kotlin
// Rating.kt (in user.review package)
data class Rating(
    val id: String = "",                    // Auto-generated ID
    val mediaId: String = "",               // TMDB movie/TV ID
    val mediaType: MediaType = MediaType.MOVIE, // Media type
    val userId: String = "",                // User ID
    val rating: Float = 0.0f,               // Rating (1-10)
    val timestamp: Long = 0L,               // Creation timestamp
    val lastModified: Long = 0L,            // Last update timestamp
    val isPublic: Boolean = true,           // Public visibility
    val isRewatch: Boolean = false,         // Rewatch flag
    val season: Int? = null,                // TV season
    val episode: Int? = null,               // TV episode
    val notes: String = ""                  // Optional notes
)
```

### 5. `watchlist` Collection
**Purpose**: Store user watchlist items
**Document ID**: Auto-generated

```kotlin
// WatchlistItem.kt
data class WatchlistItem(
    val id: String = "",                     // Auto-generated ID
    val userId: String = "",                 // User ID
    val mediaId: String = "",                // TMDB movie/TV ID
    val mediaType: String = "",              // "movie" or "tv"
    val addedDate: Long = 0L,                // Addition timestamp
    val priority: Int = 0,                   // Priority (1-5)
    val notes: String = "",                  // User notes
    val isWatched: Boolean = false,          // Watch status
    val watchedDate: Long? = null            // Watch timestamp
)
```

### 6. `customLists` Collection
**Purpose**: Store user-created custom lists
**Document ID**: Auto-generated

```kotlin
// CustomList.kt
data class CustomList(
    val id: String = "",                     // Auto-generated ID
    val userId: String = "",                 // Creator user ID
    val name: String = "",                   // List name
    val description: String = "",            // List description
    val createdDate: Long = 0L,              // Creation timestamp
    val lastModified: Long = 0L,             // Last update timestamp
    val items: List<CustomListItem> = emptyList(), // List items
    val isPublic: Boolean = true,            // Public visibility
    val coverImage: String = "",             // Cover image URL
    val tags: List<String> = emptyList(),    // List tags
    val likes: List<String> = emptyList(),   // Liked by user IDs
    val likesCount: Int = 0                  // Cached like count
)

data class CustomListItem(
    val mediaId: String = "",                // TMDB movie/TV ID
    val mediaType: String = "",              // "movie" or "tv"
    val addedDate: Long = 0L,                // Addition timestamp
    val position: Int = 0,                   // List position
    val notes: String = ""                   // Item notes
)
```

### 7. `follows` Collection
**Purpose**: Store follow relationships between users
**Document ID**: Auto-generated

```kotlin
// Follow.kt
data class Follow(
    val id: String = "",                     // Auto-generated ID
    val followerId: String = "",             // Follower user ID
    val followingId: String = "",            // Following user ID
    val followDate: Long = 0L,               // Follow timestamp
    val isAccepted: Boolean = true,          // Acceptance status
    val requestDate: Long = 0L               // Request timestamp
)
```

### 8. `userStats` Collection
**Purpose**: Store annual user statistics
**Document ID**: `{userId}_{year}`

```kotlin
// UserStats.kt
data class UserStats(
    val userId: String,                      // User ID
    val year: Int,                          // Statistics year
    val moviesWatched: Int = 0,             // Movies watched count
    val tvShowsWatched: Int = 0,            // TV shows watched count
    val reviewsWritten: Int = 0,            // Reviews written count
    val ratingsGiven: Int = 0,              // Ratings given count
    val topGenre: Genre? = null,            // Most watched genre
    val averageRating: Float = 0.0f,        // Average rating given
    val totalWatchTime: Int = 0             // Total watch time (minutes)
)
```

## Enums

### ReviewType
```kotlin
enum class ReviewType {
    USER,           // Regular user review
    CRITIC,         // Professional critic review
    VERIFIED_BUYER, // Verified purchaser review
    EARLY_ACCESS,   // Early access/premiere review
    FESTIVAL        // Film festival review
}
```

### ParentType
```kotlin
enum class ParentType {
    REVIEW,         // Reply to a review
    REPLY           // Reply to another reply
}
```

### MediaType
```kotlin
enum class MediaType {
    MOVIE,          // Movie content
    TV_SHOW         // TV show content
}
```

## Database Queries

### Common Query Patterns

1. **Get user reviews**: `reviews.where("userId", "==", userId)`
2. **Get media reviews**: `reviews.where("mediaId", "==", mediaId)`
3. **Get user watchlist**: `watchlist.where("userId", "==", userId)`
4. **Get follow relationships**: `follows.where("followerId", "==", userId)`
5. **Get public custom lists**: `customLists.where("isPublic", "==", true)`

### Composite Indexes Needed

1. `reviews`: [`userId`, `timestamp`]
2. `reviews`: [`mediaId`, `timestamp`]
3. `ratings`: [`userId`, `mediaType`]
4. `watchlist`: [`userId`, `addedDate`]
5. `follows`: [`followerId`, `followDate`]
6. `follows`: [`followingId`, `followDate`]

## Security Rules

The database uses comprehensive security rules to ensure:
- Users can only modify their own data
- Public content is readable by all
- Private profiles respect privacy settings
- Proper authentication is enforced

## Notes

- All timestamps use Unix timestamp format (milliseconds)
- Media IDs reference TMDB (The Movie Database) IDs
- User IDs are Firebase Auth UIDs
- Cached counts are updated via Cloud Functions
- Images are stored in Firebase Storage with URLs in Firestore

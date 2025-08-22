package com.just_for_fun.justforfun.data.models.customlist

data class CustomList(
    val id: String = "",                     // unique custom list ID
    val userId: String = "",                 // ID of user who created this list
    val name: String = "",                   // name of the custom list
    val description: String = "",            // description of the list
    val createdDate: Long = 0L,              // when list was created
    val lastModified: Long = 0L,             // when list was last updated
    val items: List<CustomListItem> = emptyList(), // items in the list
    val isPublic: Boolean = true,            // whether list is public or private
    val coverImage: String = "",             // optional cover image URL
    val tags: List<String> = emptyList(),    // tags for categorizing lists
    val likes: List<String> = emptyList(),   // user IDs who liked this list
    val likesCount: Int = 0                  // cached count of likes
)

data class CustomListItem(
    val mediaId: String = "",                // TMDB movie/TV show ID
    val mediaType: String = "",              // "movie" or "tv"
    val addedDate: Long = 0L,                // when item was added to list
    val position: Int = 0,                   // position in the list (for ordering)
    val notes: String = ""                   // optional notes about why it's in the list
)

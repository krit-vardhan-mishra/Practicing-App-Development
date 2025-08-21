package com.just_for_fun.justforfun.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.just_for_fun.justforfun.data.models.user.review.ParentType
import com.just_for_fun.justforfun.data.models.user.review.Reply
import com.just_for_fun.justforfun.data.models.user.review.Review
import com.just_for_fun.justforfun.data.models.user.review.ReviewType
import kotlinx.coroutines.tasks.await

class ReviewRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val reviewsCol get() = db.collection("reviews")
    private val repliesCol get() = db.collection("replies")

    suspend fun addReview(review: Review): String {
        val newId = if (review.id.isNotBlank()) review.id else reviewsCol.document().id
        val createdAt = System.currentTimeMillis()
        val data = reviewToMap(review.copy(id = newId)) + mapOf(
            "createdAt" to createdAt,
            "updatedAt" to createdAt
        )

        // Write review and update user counters atomically
        db.runTransaction { tr ->
            val reviewRef = reviewsCol.document(newId)
            tr.set(reviewRef, data)
            val userRef = db.collection("users").document(review.userId)
            tr.update(userRef, mapOf(
                "reviews" to FieldValue.arrayUnion(newId),
                "totalReviews" to FieldValue.increment(1)
            ))
        }.await()
        return newId
    }

    suspend fun updateReview(review: Review) {
        val updatedAt = System.currentTimeMillis()
        reviewsCol.document(review.id)
            .update((reviewToMap(review) + mapOf("updatedAt" to updatedAt)))
            .await()
    }

    suspend fun deleteReview(reviewId: String, userId: String) {
        db.runTransaction { tr ->
            tr.delete(reviewsCol.document(reviewId))
            tr.update(db.collection("users").document(userId), mapOf(
                "reviews" to FieldValue.arrayRemove(reviewId),
                "totalReviews" to FieldValue.increment(-1)
            ))
        }.await()
    }

    suspend fun getReviewsForMovie(movieId: String, limit: Int = 50): List<Review> {
        val snap = reviewsCol
            .whereEqualTo("movieId", movieId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
        return snap.documents.mapNotNull { mapToReview(it.data ?: emptyMap(), it.id) }
    }

    suspend fun likeReview(reviewId: String, userId: String, like: Boolean) {
        db.runTransaction { tr ->
            val ref = reviewsCol.document(reviewId)
            val snapshot = tr.get(ref)
            val data = snapshot.data ?: emptyMap()
            val helpful = (data["helpfulVotes"] as? Number)?.toInt() ?: 0
            val total = (data["totalVotes"] as? Number)?.toInt() ?: 0
            val newHelpful = if (like) helpful + 1 else helpful
            val newTotal = total + 1
            tr.update(ref, mapOf(
                "helpfulVotes" to newHelpful,
                "totalVotes" to newTotal
            ))
            val userRef = db.collection("users").document(userId)
            tr.update(userRef, mapOf(
                "likedReviews" to if (like) FieldValue.arrayUnion(reviewId) else FieldValue.arrayRemove(reviewId)
            ))
        }.await()
    }

    suspend fun addReply(parentId: String, parentType: ParentType, reply: Reply): String {
        val newId = if (reply.id.isNotBlank()) reply.id else repliesCol.document().id
        val data = replyToMap(reply.copy(id = newId, parentId = parentId, parentType = parentType))
        // Write reply and attach to parent
        db.runTransaction { tr ->
            tr.set(repliesCol.document(newId), data)
            val parentRef = if (parentType == ParentType.REVIEW) reviewsCol.document(parentId) else repliesCol.document(parentId)
            tr.update(parentRef, mapOf("replies" to FieldValue.arrayUnion(newId)))
            val userRef = db.collection("users").document(reply.userId)
            tr.update(userRef, mapOf(
                "replies" to FieldValue.arrayUnion(newId)
            ))
        }.await()
        return newId
    }

    suspend fun getReplies(parentId: String, limit: Int = 50): List<Reply> {
        val snap = repliesCol
            .whereEqualTo("parentId", parentId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get()
            .await()
        return snap.documents.mapNotNull { mapToReply(it.data ?: emptyMap(), it.id) }
    }

    suspend fun likeReply(replyId: String, userId: String, like: Boolean) {
        db.runTransaction { tr ->
            val ref = repliesCol.document(replyId)
            val snapshot = tr.get(ref)
            val data = snapshot.data ?: emptyMap()
            val likeCount = (data["likeCount"] as? Number)?.toInt() ?: 0
            val newLikes = if (like) likeCount + 1 else (likeCount - 1).coerceAtLeast(0)
            tr.update(ref, mapOf("likeCount" to newLikes))
            val userRef = db.collection("users").document(userId)
            tr.update(userRef, mapOf(
                "likedReplies" to if (like) FieldValue.arrayUnion(replyId) else FieldValue.arrayRemove(replyId)
            ))
        }.await()
    }

    private fun reviewToMap(r: Review): Map<String, Any?> = mapOf(
        "id" to r.id,
        "movieId" to r.movieId,
        "userId" to r.userId,
        "userName" to r.userName,
        "userAvatar" to r.userAvatar,
        "rating" to r.rating,
        "title" to r.title,
        "content" to r.content,
        "dateCreated" to r.dateCreated,
        "dateModified" to r.dateModified,
        "isVerifiedPurchase" to r.isVerifiedPurchase,
        "helpfulVotes" to r.helpfulVotes,
        "totalVotes" to r.totalVotes,
        "isSpoiler" to r.isSpoiler,
        "isVerified" to r.isVerified,
        "reviewType" to r.reviewType.name,
        "pros" to r.pros,
        "cons" to r.cons,
        "wouldRecommend" to r.wouldRecommend,
        "watchedDate" to r.watchedDate,
        "watchedWith" to r.watchedWith,
        "watchedWhere" to r.watchedWhere,
        "tags" to r.tags,
        "replies" to r.replies
    )

    private fun mapToReview(m: Map<String, Any?>, id: String): Review? = try {
        Review(
            id = (m["id"] as? String) ?: id,
            movieId = m["movieId"] as String,
            userId = m["userId"] as String,
            userName = m["userName"] as? String ?: "",
            userAvatar = m["userAvatar"] as? String,
            rating = (m["rating"] as? Number)?.toFloat() ?: 0f,
            title = m["title"] as? String,
            content = m["content"] as? String ?: "",
            dateCreated = m["dateCreated"] as? String ?: "",
            dateModified = m["dateModified"] as? String,
            isVerifiedPurchase = m["isVerifiedPurchase"] as? Boolean ?: false,
            helpfulVotes = (m["helpfulVotes"] as? Number)?.toInt() ?: 0,
            totalVotes = (m["totalVotes"] as? Number)?.toInt() ?: 0,
            isSpoiler = m["isSpoiler"] as? Boolean ?: false,
            isVerified = m["isVerified"] as? Boolean ?: false,
            reviewType = (m["reviewType"] as? String)?.let { runCatching { ReviewType.valueOf(it) }.getOrNull() } ?: ReviewType.USER,
            pros = (m["pros"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            cons = (m["cons"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            wouldRecommend = m["wouldRecommend"] as? Boolean,
            watchedDate = m["watchedDate"] as? String,
            watchedWith = m["watchedWith"] as? String,
            watchedWhere = m["watchedWhere"] as? String,
            tags = (m["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            replies = (m["replies"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        )
    } catch (_: Exception) { null }

    private fun replyToMap(r: Reply): Map<String, Any?> = mapOf(
        "id" to r.id,
        "userId" to r.userId,
        "parentId" to r.parentId,
        "parentType" to r.parentType.name,
        "userProfilePhoto" to r.userProfilePhoto,
        "userName" to r.userName,
        "replyText" to r.replyText,
        "timestamp" to r.timestamp,
        "likeCount" to r.likeCount,
        "dislikeCount" to r.dislikeCount,
        "isLiked" to r.isLiked,
        "isDisliked" to r.isDisliked,
        "replies" to r.replies,
        "isEdited" to r.isEdited,
        "editedTimestamp" to r.editedTimestamp
    )

    private fun mapToReply(m: Map<String, Any?>, id: String): Reply? = try {
        Reply(
            id = (m["id"] as? String) ?: id,
            userId = m["userId"] as String,
            parentId = m["parentId"] as String,
            parentType = (m["parentType"] as? String)?.let { runCatching { ParentType.valueOf(it) }.getOrNull() } ?: ParentType.REVIEW,
            userProfilePhoto = m["userProfilePhoto"] as? String ?: "",
            userName = m["userName"] as? String ?: "",
            replyText = m["replyText"] as? String ?: "",
            timestamp = (m["timestamp"] as? Number)?.toLong() ?: 0L,
            likeCount = (m["likeCount"] as? Number)?.toInt() ?: 0,
            dislikeCount = (m["dislikeCount"] as? Number)?.toInt() ?: 0,
            isLiked = m["isLiked"] as? Boolean ?: false,
            isDisliked = m["isDisliked"] as? Boolean ?: false,
            replies = (m["replies"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            isEdited = m["isEdited"] as? Boolean ?: false,
            editedTimestamp = (m["editedTimestamp"] as? Number)?.toLong()
        )
    } catch (_: Exception) { null }
}


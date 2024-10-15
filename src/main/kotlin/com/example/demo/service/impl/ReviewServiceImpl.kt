package com.example.demo.service.impl

import com.example.demo.model.Rating
import com.example.demo.model.Review
import com.example.demo.service.ReviewService
import com.google.firebase.database.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.slf4j.Logger

@Service
class ReviewServiceImpl : ReviewService {

    private val logger: Logger = LoggerFactory.getLogger(ReviewServiceImpl::class.java)

    override fun review(userId: String?, review: Review?) {
        logger.info("Adding review for user with ID: {}", userId)

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(userId)

        val reviewId = reviewsRef.push().key
        review?.reviewId = reviewId

        logger.debug("Generated review ID: {}", reviewId)

        reviewsRef.child(reviewId!!).setValue(review, DatabaseReference.CompletionListener { error, _ ->
            if (error == null) {
                logger.info("Review added for user: {} with review ID: {}", userId, reviewId)
            } else {
                logger.error("Failed to add review for user: {} with error: {}", userId, error.message)
            }
        })

        if (review != null) {
            userRef.child("rating").runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    var rating = currentData.getValue(Rating::class.java)
                    if (rating == null) {
                        logger.info("No rating data found for user: {}. Initializing new rating.", userId)
                        rating = Rating(0, 0, 0.0)
                    }

                    rating.sum += review.rating
                    rating.count += 1
                    rating.average = rating.sum.toDouble() / rating.count

                    logger.debug("Updated rating for user: {} - Sum: {}, Count: {}, Average: {}", userId, rating.sum, rating.count, rating.average)
                    currentData.value = rating
                    return Transaction.success(currentData)
                }

                override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                    if (committed) {
                        logger.info("Rating transaction committed successfully for user: {}", userId)
                    } else {
                        logger.error("Rating transaction failed for user: {} with error: {}", userId, error?.message)
                    }
                }
            })
        }
    }

override fun deleteReview(userId: String?, reviewId: String?) {
    logger.info("Deleting review with ID: {} for user: {}", reviewId, userId)

    val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
    val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(userId).child(reviewId)

    reviewsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                val review = dataSnapshot.getValue(Review::class.java)
                reviewsRef.removeValue { error, _ ->
                    if (error == null) {
                        logger.info("Review deleted: {} for user: {}", reviewId, userId)
                        updateUserRatingAfterDeletion(userRef, review!!.rating, userId as String)
                    } else {
                        logger.error("Error deleting review with ID: {} for user: {} - {}", reviewId, userId, error.message)
                    }
                }
            } else {
                logger.warn("Review with ID: {} not found for user: {}", reviewId, userId)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            logger.error("Error retrieving review with ID: {} for user: {} - {}", reviewId, userId, databaseError.message)
        }
    })
}


    private fun updateUserRatingAfterDeletion(userRef: DatabaseReference, reviewRating: Int, userId: String) {
        userRef.child("rating").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val rating = currentData.getValue(Rating::class.java) ?: Rating(0, 0, 0.0)

                rating.sum -= reviewRating
                rating.count -= 1
                rating.average = if (rating.count > 0) rating.sum.toDouble() / rating.count else 0.0

                logger.debug("Updated rating after review deletion for user: {} - Sum: {}, Count: {}, Average: {}", userId, rating.sum, rating.count, rating.average)
                currentData.value = rating
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (committed) {
                    logger.info("Rating transaction after review deletion committed successfully for user: {}", userId)
                } else {
                    logger.error("Rating transaction after review deletion failed for user: {} with error: {}", userId, error?.message)
                }
            }
        })
    }
}

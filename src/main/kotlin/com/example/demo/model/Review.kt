package com.example.demo.model

import java.time.Instant

data class Review(
    var reviewId: String? = null,          // Unique ID for the review
    var fromUserId: String = "",           // The user who wrote the review
    var rating: Int = 0,                   // Rating score (e.g., 1-5)
    var comment: String = "",              // Review comment
    var timestamp: Long = Instant.now().epochSecond  // Timestamp when the review was created (Epoch time)
)

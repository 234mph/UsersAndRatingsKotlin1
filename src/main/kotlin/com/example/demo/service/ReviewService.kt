package com.example.demo.service

import com.example.demo.model.Review

interface ReviewService {
    fun review(userId: String?, review: Review?)

    fun deleteReview(userId: String?, reviewId: String?)
}

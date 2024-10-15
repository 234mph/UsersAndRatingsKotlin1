package com.example.demo.controller

import com.example.demo.model.RegistrationForm
import com.example.demo.model.Review
import com.example.demo.service.ReviewService
import com.example.demo.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
open class UserController(
    private val userService: UserService,
    private val reviewService: ReviewService
) {

    @PostMapping("/users")
    fun register(@RequestBody registrationForm: RegistrationForm): ResponseEntity<Any> {
        userService.register(registrationForm.email, registrationForm.password)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @PostMapping("/users/{userId}/reviews")
    fun review(@PathVariable userId: String, @RequestBody review: Review): ResponseEntity<Any> {
        reviewService.review(userId, review)
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("/users/{userId}/reviews/{reviewId}")
    @PreAuthorize("hasAuthority('admin')")
    fun deleteReview(@PathVariable userId: String, @PathVariable reviewId: String): ResponseEntity<Any> {
        reviewService.deleteReview(userId, reviewId)
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/users/{userId}")
    fun assignRole(@PathVariable userId: String, @RequestParam role: String): ResponseEntity<Any> {
        userService.assignUserRole(userId, role)
        return ResponseEntity(HttpStatus.OK)
    }
}

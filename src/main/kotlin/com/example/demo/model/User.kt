package com.example.demo.model

data class User(
    var uid: String = "",          // User ID
    var email: String = "",        // User email
    var rating: Rating = Rating()  // User rating object
)

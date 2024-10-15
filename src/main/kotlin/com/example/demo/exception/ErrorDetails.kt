package com.example.demo.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ErrorDetails(
    val status: HttpStatus,
    val message: String,
    val details: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

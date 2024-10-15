package com.example.demo.model

data class Rating(
    var sum: Int = 0,
    var count: Int = 0,
    var average: Double = 0.0
) {
    override fun toString(): String {
        return "Rating(sum=$sum, count=$count, average=$average)"
    }
}


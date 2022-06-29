package com.gomezdevlopment.chessnotationapp.model.data_classes

data class User(
    val email: String = "",
    val password: String = "",
    val games: List<String> = listOf(),
    val username: String = "",
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val puzzleRating: Int = 600
)

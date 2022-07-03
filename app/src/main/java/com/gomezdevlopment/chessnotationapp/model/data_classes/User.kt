package com.gomezdevlopment.chessnotationapp.model.data_classes

data class User(
    val email: String = "",
    val password: String = "",
    val games: List<Map<String, String>> = listOf(),
    val username: String = "",
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    var puzzleRating: Int = 600
)

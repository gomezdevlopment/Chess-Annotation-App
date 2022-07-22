package com.gomezdevlopment.chessnotationapp.realtime_database

data class User(
    val email: String = "",
    val password: String = "",
    var games: Games = Games(),
    val username: String = "",
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    var puzzleRating: Int = 600,
    var online: Boolean = true
)

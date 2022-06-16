package com.gomezdevlopment.chessnotationapp.model.data_classes

data class User(
    val email: String = "",
    val password: String = "",
    val games: List<String> = listOf(),
    val username: String = "",
)

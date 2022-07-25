package com.gomezdevlopment.chessnotationapp.realtime_database

data class Friends(
    val sender: String = "",
    val receiver: String = "",
    val status: String = "pending"
)

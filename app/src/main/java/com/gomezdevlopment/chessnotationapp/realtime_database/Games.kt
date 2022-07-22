package com.gomezdevlopment.chessnotationapp.realtime_database

data class Games(
    val games: MutableList<MutableMap<String, String>> = mutableListOf()
)

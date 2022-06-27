package com.gomezdevlopment.chessnotationapp.puzzle_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puzzle")
data class Puzzle(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "fen")
    val fen: String,
    @ColumnInfo(name = "moves")
    val moves: String,
    @ColumnInfo(name = "rating")
    val rating: Int,
    @ColumnInfo(name = "themes")
    val themes: String
)
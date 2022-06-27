package com.gomezdevlopment.chessnotationapp.puzzle_database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Puzzle::class], version = 1)
abstract class PuzzleDatabase : RoomDatabase() {
    abstract fun puzzleDAO(): PuzzleDAO
}
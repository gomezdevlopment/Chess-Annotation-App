package com.gomezdevlopment.chessnotationapp.puzzle_database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PuzzleDAO {

    @Query("SELECT * FROM puzzle ORDER BY rating")
    fun getPuzzles(): List<Puzzle>

}
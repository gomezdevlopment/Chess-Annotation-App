package com.gomezdevlopment.chessnotationapp.puzzle_database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Flow

@Dao
interface PuzzleDAO {

    @Query("SELECT * FROM puzzle ORDER BY rating")
    fun getPuzzles(): List<Puzzle>

}
package com.gomezdevlopment.chessnotationapp.puzzle_database

import android.media.Rating
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Flow

@Dao
interface PuzzleDAO {

    @Query("SELECT * FROM puzzle WHERE rating BETWEEN :userRating-400 AND :userRating+100")
    fun getPuzzles(userRating: Int): List<Puzzle>

    @Query("SELECT * FROM puzzle WHERE rating BETWEEN 500 AND 1000")
    fun getBeginnerPuzzles(): List<Puzzle>

    @Query("SELECT * FROM puzzle WHERE rating BETWEEN 1000 AND 1500")
    fun getIntermediatePuzzles(): List<Puzzle>

    @Query("SELECT * FROM puzzle WHERE rating BETWEEN 1500 AND 3000")
    fun getAdvancedPuzzles(): List<Puzzle>

}
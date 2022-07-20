package com.gomezdevlopment.chessnotationapp.puzzle_database

import com.gomezdevlopment.chessnotationapp.view.MainActivity
import javax.inject.Inject

class RoomRepository @Inject constructor(private val puzzleDAO: PuzzleDAO) {

    fun getPuzzles(rating: Int): List<Puzzle> {
        return puzzleDAO.getPuzzles(rating)
    }

    fun getBeginnerPuzzles(): List<Puzzle> {
        return puzzleDAO.getBeginnerPuzzles()
    }

    fun getIntermediatePuzzles(): List<Puzzle> {
        return puzzleDAO.getIntermediatePuzzles()
    }

    fun getAdvancedPuzzles(): List<Puzzle> {
        return puzzleDAO.getAdvancedPuzzles()
    }
}
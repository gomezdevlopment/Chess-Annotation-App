package com.gomezdevlopment.chessnotationapp.puzzle_database

import javax.inject.Inject

class RoomRepository @Inject constructor(private val puzzleDAO: PuzzleDAO) {

    fun getPuzzles(): List<Puzzle> {
        return puzzleDAO.getPuzzles()
    }
}
package com.gomezdevlopment.chessnotationapp.view_model

import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.puzzle_database.Puzzle
import com.gomezdevlopment.chessnotationapp.puzzle_database.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PuzzleViewModel @Inject constructor(private val roomRepository: RoomRepository): ViewModel() {
    private val puzzles = roomRepository.getPuzzles()

    fun getPuzzles(): List<Puzzle>{
        return puzzles
    }


}
package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.pieces.ChessPieces
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPiece
import com.gomezdevlopment.chessnotationapp.model.repositories.PuzzleRepository
import com.gomezdevlopment.chessnotationapp.puzzle_database.Puzzle
import com.gomezdevlopment.chessnotationapp.puzzle_database.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val puzzleRepository: PuzzleRepository
) : ViewModel() {


    val pieceClicked: MutableState<Boolean> = mutableStateOf(false)
    val occupiedSquares = puzzleRepository.occupiedSquares
    val userColor: String = "both"
    val piecesOnBoard: List<ChessPiece> = puzzleRepository.piecesOnBoard

    val selectedPiece: MutableState<ChessPiece> = mutableStateOf(ChessPieces().whiteKing(10,10))
    val legalMoves = selectedPiece.value.legalMoves

    private var puzzles = listOf<Puzzle>()


    private fun setPuzzle() {
        puzzleRepository.setPositionFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ")
        //puzzleRepository.setPositionFromFen(puzzles[0].fen)

    }

    fun getPlayerTurn(): String = puzzleRepository.playerTurn.value
    fun getCurrentSquare() = puzzleRepository.currentSquare
    fun getPreviousSquare() = puzzleRepository.previousSquare
    fun changePiecePosition(square: Square, value: ChessPiece, promotion: PromotionPiece?) {
        puzzleRepository.changePiecePosition(square, value, promotion)
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            puzzles = roomRepository.getPuzzles()
            println(puzzles)
            setPuzzle()
        }
    }
}
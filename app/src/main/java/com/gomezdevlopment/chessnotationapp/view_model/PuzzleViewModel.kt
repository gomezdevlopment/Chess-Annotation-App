package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.effects.sound.SoundPlayer
import com.gomezdevlopment.chessnotationapp.model.pieces.ChessPieces
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPiece
import com.gomezdevlopment.chessnotationapp.model.repositories.PuzzleRepository
import com.gomezdevlopment.chessnotationapp.puzzle_database.Puzzle
import com.gomezdevlopment.chessnotationapp.puzzle_database.RoomRepository
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import javax.inject.Inject

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val puzzleRepository: PuzzleRepository,
    context: Application
) : ViewModel() {


    val correct = puzzleRepository.correct
    val endOfPuzzle = puzzleRepository.endOfPuzzle
    val puzzleRating: MutableState<String> = mutableStateOf("")
    val pieceClicked: MutableState<Boolean> = mutableStateOf(false)
    val occupiedSquares = puzzleRepository.occupiedSquares
    val userColor = puzzleRepository.userColor
    val piecesOnBoard: List<ChessPiece> = puzzleRepository.piecesOnBoard

    val selectedPiece: MutableState<ChessPiece> = mutableStateOf(ChessPieces().whiteKing(10,10))
    val legalMoves = selectedPiece.value.legalMoves

    private var puzzles = listOf<Puzzle>()

    private lateinit var currentPuzzle: Puzzle
    private lateinit var correctMoveOrder: List<String>
    private val moveIndex by puzzleRepository.moveIndex
    var puzzleIndex = 0

    val playerRating = puzzleRepository.playerRating

    fun setPuzzle() {
        puzzleRepository.endOfPuzzle.value = false
        puzzleRepository.correct.value = false
        puzzleRepository.moveIndex.value = 0
        currentPuzzle = puzzles[puzzleIndex]
        correctMoveOrder = currentPuzzle.moves.split(" ")
        puzzleRepository.setPositionFromFen(currentPuzzle.fen)
        if(getPlayerTurn() == "black"){
            userColor.value = "white"
            MainActivity.userColor = "white"
        }else{
            userColor.value = "black"
            MainActivity.userColor = "black"
        }
        puzzleRating.value = "Puzzle Rating: ${currentPuzzle.rating}"
        makeFirstMove()
    }

    fun makeFirstMove(){
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            puzzleRepository.makeComputerMove(correctMoveOrder[moveIndex])
        }

    }

    fun getPlayerTurn(): String = puzzleRepository.playerTurn.value
    fun getCurrentSquare() = puzzleRepository.currentSquare
    fun getPreviousSquare() = puzzleRepository.previousSquare

    fun changePiecePosition(square: Square, piece: ChessPiece, promotion: PromotionPiece?) {
        puzzleRepository.checkIfMoveIsCorrect(square, piece, correctMoveOrder[moveIndex], currentPuzzle.rating.toFloat())
        puzzleRepository.changePiecePosition(square, piece, promotion)
        puzzleRepository.moveIndex.value += 1

        if(moveIndex >= correctMoveOrder.lastIndex || !correct.value){
            puzzleRepository.endOfPuzzle.value = true
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                puzzleRepository.makeComputerMove(correctMoveOrder[moveIndex])
            }
        }
    }


    init {
        CoroutineScope(Dispatchers.IO).launch {
            puzzles = roomRepository.getPuzzles()
            if(puzzles.isNotEmpty()){
                println(puzzles)
                setPuzzle()
            }
        }
    }

    fun getPieceSound(): MutableState<Boolean> {
        return puzzleRepository.pieceSound
    }

    fun getCheckSound(): MutableState<Boolean> {
        return puzzleRepository.checkSound
    }

    fun getCaptureSound(): MutableState<Boolean> {
        return puzzleRepository.captureSound
    }

    fun getCastlingSound(): MutableState<Boolean> {
        return puzzleRepository.castlingSound
    }

    private val soundPlayer: SoundPlayer = SoundPlayer(context)

    fun playSoundPool(soundName: String){
        soundPlayer.playSoundPool(soundName)
    }
}
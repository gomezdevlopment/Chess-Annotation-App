package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.effects.sound.SoundPlayer
import com.gomezdevlopment.chessnotationapp.model.pieces.ChessPieces
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPiece
import com.gomezdevlopment.chessnotationapp.model.repositories.PuzzleRepository
import com.gomezdevlopment.chessnotationapp.model.utils.UserSettings
import com.gomezdevlopment.chessnotationapp.puzzle_database.Puzzle
import com.gomezdevlopment.chessnotationapp.puzzle_database.RoomRepository
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val puzzleRepository: PuzzleRepository,
    context: Application,
    private val settings: UserSettings
) : ViewModel() {


    val correct = puzzleRepository.correct
    val endOfPuzzle = puzzleRepository.endOfPuzzle
    val puzzleRating: MutableState<String> = mutableStateOf("")
    val pieceClicked: MutableState<Boolean> = mutableStateOf(false)
    val kingInCheck = puzzleRepository.kingInCheck
    var kingSquare = puzzleRepository.kingSquare
    val occupiedSquares = puzzleRepository.occupiedSquares
    val userColor = puzzleRepository.userColor
    val piecesOnBoard: List<ChessPiece> = puzzleRepository.piecesOnBoard
    val selectedPiece: MutableState<ChessPiece> = mutableStateOf(ChessPieces().whiteKing(10,10))
    val legalMoves = selectedPiece.value.legalMoves

    var puzzles = listOf<Puzzle>()

    private lateinit var currentPuzzle: Puzzle
    private lateinit var correctMoveOrder: List<String>
    private val moveIndex by puzzleRepository.moveIndex
    var puzzleIndex = 0

    val playerRating = puzzleRepository.playerRating

    val hint = mutableStateOf(false)
    val correctPiece: MutableState<ChessPiece?> = puzzleRepository.correctPiece

    val message = puzzleRepository.message
    val image = puzzleRepository.image

    val showNoMorePuzzlesCard = mutableStateOf(false)

    val chessBoardTheme by settings.chessBoardTheme
    val pieceTheme by settings.pieceThemeMap
    val highlightStyle by settings.highlightStyle

    fun setPuzzle() {
        if(puzzleIndex <= puzzles.lastIndex){
            hint.value = false
            puzzleRepository.correctPromotionChar.value = null
            puzzleRepository.correctPiece.value = null
            puzzleRepository.correctSquare.value = null
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
        }else{
            showNoMorePuzzlesCard.value = true
        }
    }

    private fun makeFirstMove(){
        puzzleRepository.makeComputerMove(correctMoveOrder[moveIndex])
        puzzleRepository.setCorrectMove(correctMoveOrder[moveIndex])
    }

    fun getPlayerTurn(): String = puzzleRepository.playerTurn.value
    fun getCurrentSquare() = puzzleRepository.currentSquare
    fun getPreviousSquare() = puzzleRepository.previousSquare

    fun changePiecePosition(square: Square, piece: ChessPiece, promotion: PromotionPiece?) {
        hint.value = false
        puzzleRepository.setCorrectMove(correctMoveOrder[moveIndex])
        puzzleRepository.checkIfMoveIsCorrect(square, piece, promotion)
        puzzleRepository.changePiecePosition(square, piece, promotion)
        puzzleRepository.moveIndex.value += 1

        if(moveIndex >= correctMoveOrder.lastIndex || !correct.value){
            puzzleRepository.endOfPuzzle.value = true
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                delay(700)
                puzzleRepository.makeComputerMove(correctMoveOrder[moveIndex])
                puzzleRepository.setCorrectMove(correctMoveOrder[moveIndex])
            }
        }
    }


    fun initializePuzzles(difficultyLevel: Int){
        CoroutineScope(Dispatchers.IO).launch {
            when(difficultyLevel){
                1 -> puzzles = roomRepository.getBeginnerPuzzles().shuffled()
                2 -> puzzles = roomRepository.getIntermediatePuzzles().shuffled()
                3 -> puzzles = roomRepository.getAdvancedPuzzles().shuffled()
            }
            if(puzzles.isNotEmpty()){
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
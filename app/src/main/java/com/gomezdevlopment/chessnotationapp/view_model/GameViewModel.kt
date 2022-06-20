package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import android.os.CountDownTimer
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.*
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.effects.sound.SoundPlayer
import com.gomezdevlopment.chessnotationapp.model.game_logic.Clock
import com.gomezdevlopment.chessnotationapp.model.repositories.GameRepository
import com.gomezdevlopment.chessnotationapp.model.utils.FirestoreGameInteraction
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.formatTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val app: Application) : AndroidViewModel(app) {
    private var gameRepository: GameRepository = GameRepository() //GameRepository.getGameRepository()
    private var hashMap: MutableMap<Square, ChessPiece> = gameRepository.occupiedSquares
    private var previousSquare: MutableState<Square> = gameRepository.previousSquare
    private var selectedPiece: MutableState<ChessPiece> =
        mutableStateOf(ChessPiece("", "", R.drawable.ic_br_alpha, Square(10, 10), 0))
    private var pieceClicked: MutableState<Boolean> = mutableStateOf(false)
    private var promotionDialogShowing: MutableState<Boolean> = mutableStateOf(false)

    var selectedNotationIndex: MutableState<Int> = gameRepository.selectedNotationIndex
    var currentPosition: MutableState<Boolean> = mutableStateOf(true)
    var onUpdate = mutableStateOf(0)

    val cardVisible = gameRepository.endOfGameCardVisible
    val endOfGame = gameRepository.endOfGame
    val endOfGameResult = gameRepository.endOfGameResult
    val endOfGameMessage = gameRepository.endOfGameMessage

    val capturedPieces = mutableStateOf(gameRepository.capturedPieces)

    val openResignDialog = mutableStateOf(false)
    val openDrawOfferDialog = mutableStateOf(false)
    val openDrawOfferedDialog = gameRepository.openDrawOfferedDialog

    fun createNewGame(time: Long) {
        gameRepository.resetGame(time)
    }

    fun previousNotation() {
        if (selectedNotationIndex.value > 0) {
            selectedNotationIndex.value -= 1
            setGameState(selectedNotationIndex.value)
        }
    }

    fun nextNotation() {
        if (selectedNotationIndex.value < getAnnotations().size - 1) {
            selectedNotationIndex.value += 1
            setGameState(selectedNotationIndex.value)
        }
    }

    fun setGameState(index: Int) {
        isPieceClicked().value = false
        currentPosition.value = (selectedNotationIndex.value == getAnnotations().size - 1)
        gameRepository.setGameState(index)
    }

    //private val _piecesOnBoard = gameRepository.getPiecesOnBoard()
    //val piecesOnBoard: MutableState<List<ChessPiece>> = mutableStateOf(gameRepository.piecesOnBoard)
    val piecesOnBoard: List<ChessPiece> = gameRepository.piecesOnBoard

    fun setPromotionDialogState(clicked: Boolean) {
        promotionDialogShowing.value = clicked
    }

    fun resetGame() {
        //gameRepository.resetGame(300000L)
//        handleTimerValues(
//            false,
//            formatTime(initialTime),
//            gameRepository.whiteProgress.value,
//            _whiteTimeIsPlaying,
//            _whiteTime,
//            gameRepository.whiteProgress
//        )
//
//        handleTimerValues(
//            false,
//            formatTime(initialTime),
//            gameRepository.blackProgress.value,
//            _blackTimeIsPlaying,
//            _blackTime,
//            gameRepository.blackProgress
//        )
    }

    fun isPromotionDialogShowing(): MutableState<Boolean> {
        return promotionDialogShowing
    }

    fun setPieceClickedState(clicked: Boolean) {
        pieceClicked.value = clicked
    }

    fun isPieceClicked(): MutableState<Boolean> {
        return pieceClicked
    }

    fun selectPiece(piece: ChessPiece) {
        if (currentPosition.value && !endOfGame.value) {
            if (!isPromotionDialogShowing().value) {
                setPieceClickedState(false)
                selectedPiece.value = piece
                if (getPlayerTurn() == getSelectedPiece().value.color) {
                    setPieceClickedState(true)
//                    if (getPlayerTurn() == userColor) {
//                        setPieceClickedState(true)
//                    }
                }
            }
        }

    }

    fun getSelectedPiece(): MutableState<ChessPiece> {
        return selectedPiece
    }

    fun undoMove() {
        gameRepository.undoMove()
    }

    fun onEvent(event: GameEvent, piece: ChessPiece): MutableList<Square>? {
        when (event) {
            GameEvent.OnPieceClicked -> {
                return gameRepository.mapOfPiecesAndTheirLegalMoves[piece]
            }
        }
    }

    fun drawOffer(value: String){
        viewModelScope.launch {
            FirestoreGameInteraction().writeDrawOffer(getPlayerTurn(), value)
        }
    }

    fun rematchOffer(value: String){
        viewModelScope.launch {
            FirestoreGameInteraction().writeRematchOffer(getPlayerTurn(), value)
        }
    }



    fun resign(){
        viewModelScope.launch {
            FirestoreGameInteraction().writeResignation(getPlayerTurn(), userColor)
        }
    }

    fun getHashMap(): MutableMap<Square, ChessPiece> {
        return hashMap
    }

    fun endOfGameCard(title: String, message: String) {

    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece) {
        gameRepository.changePiecePosition(newSquare, piece, 0)
    }

    fun promotion(newSquare: Square, promotionSelection: ChessPiece) {
        gameRepository.promotion(newSquare, promotionSelection, 0)
        selectedNotationIndex.value += 1
    }

    fun movePiece(newSquare: Square, piece: ChessPiece) {
        gameRepository.movePiece(newSquare, piece, 0)
    }

    fun getPreviousSquare(): MutableState<Square> {
        return previousSquare
    }

    fun getCurrentSquare(): MutableState<Square> {
        return gameRepository.currentSquare
    }

    fun getPlayerTurn(): String {
        return gameRepository.playerTurn.value
    }

    fun kingSquare(): Square {
        return gameRepository.kingSquare().value
    }

    fun kingInCheck(): Boolean {
        return gameRepository.kingInCheck.value
    }

    fun xRays(): MutableList<Square> {
        return gameRepository.xRayAttacks
    }

    fun getSquaresToBlock(): MutableList<Square> {
        return gameRepository.squaresToBlock
    }

    fun getAttacks(): MutableList<Square> {
        return gameRepository.attacks
    }

    fun getPieceSound(): MutableState<Boolean> {
        return gameRepository.pieceSound
    }

    fun getCheckSound(): MutableState<Boolean> {
        return gameRepository.checkSound
    }

    fun getCaptureSound(): MutableState<Boolean> {
        return gameRepository.captureSound
    }

    fun getCastlingSound(): MutableState<Boolean> {
        return gameRepository.castlingSound
    }

    fun getGameEndSound(): MutableState<Boolean> {
        return gameRepository.gameEndSound
    }

    fun playSound(soundId: Int) {
        SoundPlayer().playSound(app, soundId)
    }

    fun getAnnotations(): MutableList<String> {
        return gameRepository.annotations
    }


    var whiteTimer = gameRepository.whiteTimer
    var blackTimer = gameRepository.blackTimer
    val whiteTime: StateFlow<String> = gameRepository.whiteTime
    val whiteProgress: StateFlow<Float> = gameRepository.whiteProgress
    val blackTime: StateFlow<String> = gameRepository.blackTime
    val blackProgress: StateFlow<Float> = gameRepository.blackProgress

}
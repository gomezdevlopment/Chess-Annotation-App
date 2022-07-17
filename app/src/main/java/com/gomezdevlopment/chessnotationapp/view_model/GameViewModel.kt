package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.*
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.effects.sound.SoundPlayer
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPiece
import com.gomezdevlopment.chessnotationapp.model.repositories.GameRepository
import com.gomezdevlopment.chessnotationapp.model.utils.UserSettings
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    app: Application,
    private val gameRepository: GameRepository,
    private val settings: UserSettings
) : AndroidViewModel(app) {

    //private var gameRepository: GameRepository = GameRepository() //GameRepository.getGameRepository()
    private var hashMap: MutableMap<Square, ChessPiece> = gameRepository.occupiedSquares
    private var previousSquare: MutableState<Square> = gameRepository.previousSquare
    private var selectedPiece: MutableState<ChessPiece> =
        mutableStateOf(
            ChessPiece(
                "",
                "",
                R.drawable.ic_br_alpha,
                Square(10, 10),
                0,
                mutableListOf(),
                mutableListOf(),
                mutableListOf(),
                mutableListOf(),
                mutableListOf()
            )
        )
    private var pieceClicked: MutableState<Boolean> = mutableStateOf(false)
    private var promotionDialogShowing: MutableState<Boolean> = mutableStateOf(false)
    var selectedNotationIndex: MutableState<Int> = gameRepository.selectedNotationIndex
    private var currentPosition: MutableState<Boolean> = mutableStateOf(true)
    val cardVisible = gameRepository.endOfGameCardVisible
    val endOfGame = gameRepository.endOfGame
    val endOfGameResult = gameRepository.endOfGameResult
    val endOfGameMessage = gameRepository.endOfGameMessage
    val piecesOnBoard: List<ChessPiece> = gameRepository.piecesOnBoard
    val capturedPieces = gameRepository.capturedPieces
    val openResignDialog = mutableStateOf(false)
    val openDrawOfferDialog = mutableStateOf(false)
    val openDrawOfferedDialog = gameRepository.openDrawOfferedDialog
    val isOnline = mutableStateOf(true)
    var whiteTimer = gameRepository.whiteTimer
    var blackTimer = gameRepository.blackTimer
    val whiteTime: StateFlow<String> = gameRepository.whiteTime
    val whiteProgress: StateFlow<Float> = gameRepository.whiteProgress
    val blackTime: StateFlow<String> = gameRepository.blackTime
    val blackProgress: StateFlow<Float> = gameRepository.blackProgress
    val kingSquare: MutableState<Square> = gameRepository.kingSquare

    private val soundPlayer: SoundPlayer = SoundPlayer(app)

    val previousGameStates = gameRepository.previousGameStates

    val chessBoardTheme by settings.chessBoardTheme
    val pieceTheme by settings.pieceThemeMap
    val pieceAnimationSpeed by settings.pieceAnimationSpeed
    val highlightStyle by settings.highlightStyle

    fun createNewGame(time: Long, isOnline: Boolean) {
        viewModelScope.launch {
            gameRepository.resetGame(time, isOnline)
        }
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

    fun isPromotionDialogShowing(): MutableState<Boolean> {
        return promotionDialogShowing
    }

    fun setPieceClickedState(clicked: Boolean) {
        pieceClicked.value = clicked
    }

    fun isPieceClicked(): MutableState<Boolean> {
        return pieceClicked
    }

    fun pieceIsClickable(): Boolean{
        return (currentPosition.value && !endOfGame.value && !isPromotionDialogShowing().value)
    }

    fun selectPiece(piece: ChessPiece) {
        if (currentPosition.value && !endOfGame.value) {
            if (!isPromotionDialogShowing().value) {
                setPieceClickedState(false)
                selectedPiece.value = piece
                if (getPlayerTurn() == getSelectedPiece().value.color) {
                    setPieceClickedState(true)
//                    if (isOnline.value) {
//                        if (getPlayerTurn() == userColor) {
//                            setPieceClickedState(true)
//                        }
//                    } else {
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
        //gameRepository.undoMove()
    }

    fun onEvent(event: GameEvent, piece: ChessPiece): MutableList<Square> {
        when (event) {
            GameEvent.OnPieceClicked -> {
                return piece.legalMoves
            }
        }
    }

    fun drawOffer(value: String) {
        viewModelScope.launch {
            gameRepository.firestore.writeDrawOffer(getPlayerTurn(), value)
        }
    }

    fun rematchOffer(value: String) {
        viewModelScope.launch {
            gameRepository.firestore.writeRematchOffer(getPlayerTurn(), value)
        }
    }

    fun resign() {
        viewModelScope.launch {
            gameRepository.firestore.writeResignation(userColor)
        }
    }

    fun getHashMap(): MutableMap<Square, ChessPiece> {
        return hashMap
    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece, promotionPiece: PromotionPiece?) {
        viewModelScope.launch {
            gameRepository.changePiecePosition(newSquare, piece, promotionPiece)
        }
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

//    fun kingSquare(): MutableState<Square> {
//        return gameRepository.kingSquare()
//    }

    fun kingInCheck(): Boolean {
        return gameRepository.kingInCheck.value
    }

    fun xRays(): MutableList<Square> {
        return gameRepository.xRayAttacks
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

    fun playSoundPool(soundName: String) {
        soundPlayer.playSoundPool(soundName)
    }

    val notations = gameRepository.annotations
    fun getAnnotations(): MutableList<String> {
        return gameRepository.annotations
    }

}
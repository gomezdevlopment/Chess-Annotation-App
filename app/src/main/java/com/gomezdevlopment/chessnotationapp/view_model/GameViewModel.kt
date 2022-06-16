package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import android.os.CountDownTimer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.*
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.effects.sound.SoundPlayer
import com.gomezdevlopment.chessnotationapp.model.repositories.GameRepository
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.formatTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel(private val app: Application) : AndroidViewModel(app) {
    private var gameRepository: GameRepository = GameRepository.getGameRepository()
    private var hashMap: MutableMap<Square, ChessPiece> = gameRepository.occupiedSquares
    private var previousSquare: MutableState<Square> = gameRepository.previousSquare
    private var selectedPiece: MutableState<ChessPiece> =
        mutableStateOf(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(7, 0), 5))
    private var pieceClicked: MutableState<Boolean> = mutableStateOf(false)
    private var promotionDialogShowing: MutableState<Boolean> = mutableStateOf(false)

    var selectedNotationIndex: MutableState<Int> = mutableStateOf(0)
    var currentPosition: MutableState<Boolean> = mutableStateOf(true)
    var onUpdate = mutableStateOf(0)

    val cardVisible = gameRepository.endOfGameCardVisible
    val endOfGame = gameRepository.endOfGame
    val endOfGameResult = gameRepository.endOfGameResult
    val endOfGameMessage = gameRepository.endOfGameMessage

    val capturedPieces = mutableStateOf(gameRepository.capturedPieces)

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
        gameRepository.resetGame()
        handleTimerValues(
            false,
            formatTime(initialTime),
            gameRepository.whiteProgress.value,
            _whiteTimeIsPlaying,
            _whiteTime,
            gameRepository.whiteProgress
        )
        handleTimerValues(
            false,
            formatTime(initialTime),
            gameRepository.blackProgress.value,
            _blackTimeIsPlaying,
            _blackTime,
            gameRepository.blackProgress
        )
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

    fun getHashMap(): MutableMap<Square, ChessPiece> {
        return hashMap
    }

    fun endOfGameCard(title: String, message: String) {

    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece) {
        gameRepository.changePiecePosition(newSquare, piece, 0)
        selectedNotationIndex.value += 1
        //setPlayerTurnClockValues()
        when (gameRepository.playerTurn.value) {
            "white" -> {
                pauseTimer(
                    _blackTimeIsPlaying,
                    _blackTime,
                    gameRepository.blackProgress,
                    blackTimer
                )
                startTimer(
                    _whiteTimeIsPlaying,
                    _whiteTime,
                    gameRepository.whiteProgress,
                    whiteTimer,
                    initialTime
                )
            }
            "black" -> {
                pauseTimer(
                    _whiteTimeIsPlaying,
                    _whiteTime,
                    gameRepository.whiteProgress,
                    whiteTimer
                )
                startTimer(
                    _blackTimeIsPlaying,
                    _blackTime,
                    gameRepository.blackProgress,
                    blackTimer,
                    initialTime
                )
            }
        }

        if (endOfGame.value) {
            pauseTimer(_whiteTimeIsPlaying, _whiteTime, gameRepository.whiteProgress, whiteTimer)
            pauseTimer(_blackTimeIsPlaying, _blackTime, gameRepository.blackProgress, blackTimer)
        }
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

    //Timer
    private var countDownTimer: CountDownTimer? = null
    var whiteTimer = gameRepository.whiteTimer
    var blackTimer = gameRepository.blackTimer
    private val initialTime = gameRepository.initialTime

    private var _whiteTime = MutableStateFlow(formatTime(whiteTimer.value))
    val whiteTime: StateFlow<String> = _whiteTime
    val whiteProgress: StateFlow<Float> = gameRepository.whiteProgress
    private val _whiteTimeIsPlaying = MutableStateFlow(false)
    val whiteTimeIsPlaying: StateFlow<Boolean> = _whiteTimeIsPlaying

    private val _blackTime = MutableStateFlow(formatTime(blackTimer.value))
    val blackTime: StateFlow<String> = _blackTime
    val blackProgress: StateFlow<Float> = gameRepository.blackProgress
    private val _blackTimeIsPlaying = MutableStateFlow(false)
    val blackTimeIsPlaying: StateFlow<Boolean> = _blackTimeIsPlaying

    fun handleCountDownTimer() {
        if (whiteTimeIsPlaying.value) {
            pauseTimer(_whiteTimeIsPlaying, _whiteTime, gameRepository.whiteProgress, whiteTimer)
            startTimer(
                _blackTimeIsPlaying,
                _blackTime,
                gameRepository.blackProgress,
                blackTimer,
                initialTime
            )
        } else if (blackTimeIsPlaying.value) {
            pauseTimer(_blackTimeIsPlaying, _blackTime, gameRepository.blackProgress, blackTimer)
            startTimer(
                _whiteTimeIsPlaying,
                _whiteTime,
                gameRepository.whiteProgress,
                whiteTimer,
                initialTime
            )
        }
    }

    private fun pauseTimer(
        _isPlaying: MutableStateFlow<Boolean>,
        _time: MutableStateFlow<String>,
        _progress: MutableStateFlow<Float>,
        time: MutableState<Long>
    ) {
        //time.value-=elapsedTime
        countDownTimer?.cancel()
        handleTimerValues(
            false,
            formatTime(time.value),
            _progress.value,
            _isPlaying,
            _time,
            _progress
        )
    }

    private fun startTimer(
        _isPlaying: MutableStateFlow<Boolean>,
        _time: MutableStateFlow<String>,
        _progress: MutableStateFlow<Float>,
        time: MutableState<Long>,
        initialTime: Long
    ) {
        _isPlaying.value = true
        countDownTimer = object : CountDownTimer(time.value, 1) {

            override fun onTick(millisRemaining: Long) {
                time.value = millisRemaining
                _progress.value = millisRemaining / initialTime.toFloat()
                handleTimerValues(
                    true,
                    formatTime(millisRemaining),
                    _progress.value,
                    _isPlaying,
                    _time,
                    _progress
                )
            }

            override fun onFinish() {
                pauseTimer(_isPlaying, _time, _progress, time)
                //gameRepository.timeOut.value = true
                gameRepository.timeout()
            }
        }.start()
    }

    private fun handleTimerValues(
        isPlaying: Boolean,
        text: String,
        progress: Float,
        _isPlaying: MutableStateFlow<Boolean>,
        _time: MutableStateFlow<String>,
        _progress: MutableStateFlow<Float>
    ) {
        _isPlaying.value = isPlaying
        _time.value = text
        _progress.value = progress
    }
}
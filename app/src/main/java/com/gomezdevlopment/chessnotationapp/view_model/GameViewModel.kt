package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.view.ContentInfoCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.*
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.effects.sound.SoundPlayer
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPiece
import com.gomezdevlopment.chessnotationapp.model.repositories.GameRepository
import com.gomezdevlopment.chessnotationapp.model.utils.UserSettings
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.lang.StringBuilder
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val app: Application,
    private val gameRepository: GameRepository,
    private val settings: UserSettings
) : AndroidViewModel(app) {
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
    private val pieceClicked: MutableState<Boolean> = mutableStateOf(false)
    private val promotionDialogShowing: MutableState<Boolean> = mutableStateOf(false)
    val selectedNotationIndex: MutableState<Int> = gameRepository.selectedNotationIndex
    private val currentPosition: MutableState<Boolean> = mutableStateOf(true)
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
    val whiteTimer = gameRepository.whiteTimer
    val blackTimer = gameRepository.blackTimer
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
        pieceClicked.value = false
        promotionDialogShowing.value = false
        //selectedNotationIndex = gameRepository.selectedNotationIndex
        currentPosition.value = true
        openResignDialog.value = false
        openDrawOfferDialog.value = false
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

    fun pieceIsClickable(): Boolean {
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

    val export: MutableLiveData<Boolean> = MutableLiveData(false)
    val uri: MutableState<Uri?> = mutableStateOf(null)

    fun exportGameFile() {
        val annotations = getAnnotations()
        if (annotations.isEmpty()) {
            Toast.makeText(app, "Cannot export game because there are no annotations!", Toast.LENGTH_SHORT).show()
        } else {
            uri.value = createPGNFile(app)
            export.postValue(true)
        }
    }

    private fun createPGNFile(context: Application): Uri {
        val filename = "chess_game.pgn"
        val path = context.getExternalFilesDir(null)
        val pgnFile = File(path, filename)
        pgnFile.delete()
        pgnFile.createNewFile()
        pgnFile.appendText(createPGNString())
        return FileProvider.getUriForFile(
            context,
            app.packageName.toString() + ".provider",
            pgnFile
        )
    }

    private fun createPGNString(): String {
        val pgnString = StringBuilder()
        val annotations = getAnnotations().subList(1, getAnnotations().size)
        annotations.forEachIndexed { index, notation ->
            var blackMove = ""
            if(index+1 <= annotations.lastIndex){
                blackMove = annotations[index+1]
            }

            if(index % 2 == 0){
                pgnString.append("${(index/2)+1}. $notation $blackMove ")
            }

        }
        return pgnString.toString()
    }
}
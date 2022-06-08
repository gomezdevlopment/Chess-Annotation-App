package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.*
import com.gomezdevlopment.chessnotationapp.model.effects.sound.SoundPlayer

class GameViewModel(private val app: Application) : AndroidViewModel(app) {
    private var gameRepository: GameRepository = GameRepository.getGameRepository()
    private var hashMap: MutableMap<Square, ChessPiece> = gameRepository.getHashMap()
    private var previousSquare: MutableState<Square> = gameRepository.getPreviousSquare()
    private var selectedPiece: MutableState<ChessPiece> =  mutableStateOf(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(7, 0)))
    private var pieceClicked: MutableState<Boolean> = mutableStateOf(false)
    private var promotionDialogShowing: MutableState<Boolean> = mutableStateOf(false)

    var selectedNotationIndex: MutableState<Int> = mutableStateOf(-1)
    var currentPosition: MutableState<Boolean> = mutableStateOf(true)
    var onUpdate = mutableStateOf(0)
    val endOfGame = mutableStateOf(false)

    fun previousNotation(){
        if(selectedNotationIndex.value > 0){
            currentPosition.value = false
            gameRepository.previousGameState()
            selectedNotationIndex.value-=1
        }
        updateUI()
    }

    fun nextNotation(){
        if(selectedNotationIndex.value < getAnnotations().size-1){
            gameRepository.nextGameState()
            selectedNotationIndex.value+=1
            if(selectedNotationIndex.value == getAnnotations().size-1){
                currentPosition.value = true
            }
        }
        updateUI()
    }


    private fun updateUI() {
        onUpdate.value = (0..1_000_000).random()
    }
    //private val _piecesOnBoard = gameRepository.getPiecesOnBoard()
    val piecesOnBoard: MutableState<List<ChessPiece>> = mutableStateOf(gameRepository.getPiecesOnBoard())

    fun setPromotionDialogState(clicked: Boolean){
        promotionDialogShowing.value = clicked
    }

    fun resetGame(){
        gameRepository.resetGame()
    }
    fun isPromotionDialogShowing(): MutableState<Boolean> {
        return promotionDialogShowing
    }

    fun setPieceClickedState(clicked: Boolean){
        pieceClicked.value = clicked
    }

    fun isPieceClicked(): MutableState<Boolean> {
        return pieceClicked
    }

    fun selectPiece(piece: ChessPiece){
        if(currentPosition.value){
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
        gameRepository.previousGameState()
    }

    fun onEvent(event: GameEvent, piece: ChessPiece): MutableList<Square>? {
        when (event) {
            GameEvent.OnPieceClicked -> {
                return gameRepository.getLegalMoves(piece)
            }
        }
    }

    fun getHashMap(): MutableMap<Square, ChessPiece> {
        return hashMap
    }

    fun endOfGameCard(title: String, message:String) {

    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece) {
        gameRepository.changePiecePosition(newSquare, piece, 0)
        selectedNotationIndex.value +=1
        updateUI()
    }

    fun promotion(newSquare: Square, promotionSelection: ChessPiece) {
        gameRepository.promotion(newSquare, promotionSelection, 0)
        selectedNotationIndex.value +=1
        updateUI()
    }

    fun movePiece(newSquare: Square, piece: ChessPiece) {
        gameRepository.movePiece(newSquare, piece, 0)
        updateUI()
    }

    fun getPreviousSquare(): MutableState<Square> {
        return previousSquare
    }

    fun getCurrentSquare(): MutableState<Square> {
        return gameRepository.getCurrentSquare()
    }

    fun getPlayerTurn(): String {
        return gameRepository.getPlayerTurn().value
    }

    fun kingSquare(): Square {
        return gameRepository.kingSquare().value
    }

    fun kingInCheck(): Boolean {
        return gameRepository.kingInCheck().value
    }

    fun xRays(): MutableList<Square> {
        return gameRepository.getXRayAttacks()
    }

    fun getSquaresToBlock(): MutableList<Square> {
        return gameRepository.getSquaresToBlock()
    }

    fun getAttacks(): MutableList<Square> {
        return gameRepository.getAttacks()
    }

    fun getCheckmate(): MutableState<Boolean> {
        return gameRepository.getCheckmate()
    }

    fun getStalemate(): MutableState<Boolean> {
        return gameRepository.getStalemate()
    }

    fun getInsufficientMaterial(): MutableState<Boolean> {
        return gameRepository.getInsufficientMaterial()
    }

    fun getThreeFoldRepetition(): MutableState<Boolean> {
        return gameRepository.getThreeFoldRepetition()
    }

    fun getFiftyMoveRule(): MutableState<Boolean> {
        return gameRepository.getFiftyMoveRule()
    }

    fun getPieceSound(): MutableState<Boolean> {
        return gameRepository.getPieceSound()
    }

    fun getCheckSound(): MutableState<Boolean> {
        return gameRepository.getCheckSound()
    }

    fun getCaptureSound(): MutableState<Boolean> {
        return gameRepository.getCaptureSound()
    }

    fun getCastlingSound(): MutableState<Boolean> {
        return gameRepository.getCastlingSound()
    }

    fun getGameEndSound(): MutableState<Boolean> {
        return gameRepository.getGameEndSound()
    }

    fun playSound(soundId: Int){
        SoundPlayer().playSound(app, soundId)
    }

    fun getAnnotations(): MutableList<String> {
        //val annotations: LiveData<MutableList<String>> = gameRepository.getAnnotations()
        return gameRepository.getAnnotations()
    }
}
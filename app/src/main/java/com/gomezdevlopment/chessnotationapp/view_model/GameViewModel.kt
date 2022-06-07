package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.*

class GameViewModel(private val app: Application) : AndroidViewModel(app) {
    private var gameRepository: GameRepository = GameRepository.getGameRepository()
    private var hashMap: MutableMap<Square, ChessPiece> = gameRepository.getHashMap()
    private var previousSquare: MutableState<Square> = gameRepository.getPreviousSquare()
    private var selectedPiece: MutableState<ChessPiece> =  mutableStateOf(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(7, 0)))
    private var pieceClicked: MutableState<Boolean> = mutableStateOf(false)

    fun setPieceClickedState(clicked: Boolean){
        pieceClicked.value = clicked
    }

    fun isPieceClicked(): MutableState<Boolean> {
        return pieceClicked
    }

    fun selectPiece(piece: ChessPiece){
        selectedPiece.value = piece
    }

    fun getSelectedPiece(): MutableState<ChessPiece> {
        return selectedPiece
    }

    fun getPiecesOnBoard(): MutableList<ChessPiece> {
        return gameRepository.getPiecesOnBoard()
    }

    fun resetGame() {
        //gameRepository.undoChangePiecePosition(pieceTemp, originalSquare, currentSquare, previousSquareTemp, kingSquare, gameRepository.getCurrentSquare().value)
        //gameRepository.resetGame()
        //gameRepository.previousGameState(gameState)
    }

    fun undoMove() {
        gameRepository.previousGameState()
        ///gameRepository.setPositionFromFen(fen)
        //gameRepository.undoChangePiecePosition(pieceTemp, originalSquare, currentSquare, previousSquareTemp, kingSquare, gameRepository.getCurrentSquare().value, gameRepository.castleKingSide(), gameRepository.castleQueenSide())
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

    fun changePiecePosition(newSquare: Square, piece: ChessPiece) {
//        pieceTemp = piece
//        originalSquare = piece.square
//        currentSquare = gameRepository.getCurrentSquare().value
//        previousSquareTemp = gameRepository.getPreviousSquare().value
//        kingSquare = gameRepository.kingSquare().value
        gameRepository.changePiecePosition(newSquare, piece, 0)
    }

    fun promotion(newSquare: Square, promotionSelection: ChessPiece) {
        gameRepository.promotion(newSquare, promotionSelection, 0)
    }

    fun movePiece(newSquare: Square, piece: ChessPiece) {
        gameRepository.movePiece(newSquare, piece, 0)
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
        val player = MediaPlayer.create(app, R.raw.piece_sound)
        if(player.isPlaying){
            player.release()
        }
        player.start()
        player.setOnCompletionListener {
            player.release()
        }
    }

    fun getAnnotations(): MutableList<String> {
        //val annotations: LiveData<MutableList<String>> = gameRepository.getAnnotations()
        return gameRepository.getAnnotations()
    }
}
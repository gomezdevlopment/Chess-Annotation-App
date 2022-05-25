package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.*

class GameViewModel: ViewModel() {
    private var gameRepository: GameRepository = GameRepository.getGameRepository()
    private var piecesOnBoard: MutableList<ChessPiece> = gameRepository.getPiecesOnBoard()
    private var  hashMap : MutableMap<Square, ChessPiece> = gameRepository.getHashMap()
    private var previousSquare : MutableState<Square> = gameRepository.getPreviousSquare()

    fun getPiecesOnBoard(): MutableList<ChessPiece> {
        return piecesOnBoard
    }

    fun resetGame() {
        gameRepository.resetGame()
    }

    fun onEvent(event: GameEvent, piece: ChessPiece): List<Square> {
        when(event) {
            GameEvent.OnPieceClicked -> {
                return gameRepository.checkLegalMoves(piece, false)
            }
        }
    }

    fun getHashMap(): MutableMap<Square, ChessPiece> {
        return hashMap
    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece){
        gameRepository.changePiecePosition(newSquare, piece)
    }

    fun getPreviousSquare(): MutableState<Square>{
        return previousSquare
    }

    fun getCurrentSquare(): MutableState<Square>{
        return gameRepository.getCurrentSquare()
    }

    fun getPlayerTurn(): String{
        return gameRepository.getPlayerTurn().value
    }

    fun getSquaresToBlock(): MutableList<Square> {
        return gameRepository.getSquaresToBlock()
    }

    fun getWhiteAttacks() : List<Square>{
        return gameRepository.getWhiteAttacks()
    }

    fun getBlackAttacks() : List<Square>{
        return gameRepository.getBlackAttacks()
    }

}
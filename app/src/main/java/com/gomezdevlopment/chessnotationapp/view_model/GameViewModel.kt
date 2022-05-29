package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.*

class GameViewModel: ViewModel() {
    private var gameRepository: GameRepository = GameRepository.getGameRepository()
    private var piecesOnBoard: MutableList<ChessPiece> = gameRepository.getPiecesOnBoard()
    private var  hashMap : MutableMap<Square, ChessPiece> = gameRepository.getHashMap()
    private var previousSquare : MutableState<Square> = gameRepository.getPreviousSquare()

    private lateinit var pieceTemp: ChessPiece
    private lateinit var originalSquare: Square
    private lateinit var currentSquare: Square
    private lateinit var previousSquareTemp: Square
    private lateinit var kingSquare: Square

    fun getPiecesOnBoard(): MutableList<ChessPiece> {
        return piecesOnBoard
    }

    fun resetGame() {
        //gameRepository.undoChangePiecePosition(pieceTemp, originalSquare, currentSquare, previousSquareTemp, kingSquare, gameRepository.getCurrentSquare().value)
        gameRepository.resetGame()
    }

    fun undoMove() {
        gameRepository.undoChangePiecePosition(pieceTemp, originalSquare, currentSquare, previousSquareTemp, kingSquare, gameRepository.getCurrentSquare().value)
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
        pieceTemp = piece
        originalSquare = piece.square
        currentSquare = gameRepository.getCurrentSquare().value
        previousSquareTemp = gameRepository.getPreviousSquare().value
        kingSquare = gameRepository.kingSquare().value
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

    fun kingSquare(): Square{
        return gameRepository.kingSquare().value
    }

    fun kingInCheck(): Boolean{
        return gameRepository.kingInCheck().value
    }

    fun xRays(): MutableList<Square>{
        return gameRepository.getChecksOnKing()
    }
    fun getSquaresToBlock(): MutableList<Square> {
        return gameRepository.getSquaresToBlock()
    }

    fun getAttacks(): MutableList<Square> {
        return gameRepository.getAttacks()
    }
}
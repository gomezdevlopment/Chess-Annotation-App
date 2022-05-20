package com.gomezdevlopment.chessnotationapp.view_model

import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.GameRepository
import com.gomezdevlopment.chessnotationapp.model.Square

class GameViewModel: ViewModel() {
    private var gameRepository: GameRepository = GameRepository()
    private var piecesOnBoard: MutableList<ChessPiece> = mutableListOf()
    private var  hashMap : HashMap<Square, ChessPiece> = HashMap()

    init {
        piecesOnBoard = gameRepository.getPiecesOnBoard()
        hashMap = gameRepository.getHashMap()
    }

    fun getPiecesOnBoard(): MutableList<ChessPiece> {
        return piecesOnBoard
    }

    fun onEvent(event: GameEvent, piece: ChessPiece): List<Square> {
        when(event) {
            GameEvent.OnPieceClicked -> {
                return piece.checkLegalMoves(hashMap)
            }
        }
    }

    fun getHashMap(): HashMap<Square, ChessPiece> {
        return hashMap
    }
}
package com.gomezdevlopment.chessnotationapp.view_model

import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.GameRepository

class GameViewModel: ViewModel() {
    private var gameRepository: GameRepository = GameRepository()
    private var piecesOnBoard: MutableList<ChessPiece> = mutableListOf()

    init {
        piecesOnBoard = gameRepository.getPiecesOnBoard()
    }

    fun getPiecesOnBoard(): MutableList<ChessPiece> {
        return piecesOnBoard
    }

    fun onEvent(event: GameEvent, piece: ChessPiece){
        when(event) {
            GameEvent.OnPieceClicked -> {
                piece.checkLegalMoves(piecesOnBoard)
            }
        }
    }
}
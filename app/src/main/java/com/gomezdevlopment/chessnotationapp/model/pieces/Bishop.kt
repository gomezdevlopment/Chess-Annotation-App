package com.gomezdevlopment.chessnotationapp.model.pieces

import androidx.compose.runtime.MutableState
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic

class Bishop {

    fun moves(
        piece: ChessPiece,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        kingInCheck: MutableState<Boolean>,
        kingSquare: Square,
        piecesCheckingKing: MutableList<ChessPiece>,
        pinnedPieces: MutableList<ChessPiece>,
    ) {
        if(!pinnedPieces.contains(piece)){
            piece.pinnedMoves.clear()
        }

        GameLogic().clearMoves(piece)
        val listOfMoves = mutableListOf<Square>()

        fun addMoves() {
            val gameLogic = GameLogic()
            val moves = mutableListOf<Square>()
            listOfMoves.forEach { move ->
                moves.add(move)
                gameLogic.addMove(
                    occupiedSquares,
                    move,
                    kingSquare,
                    piece,
                    moves,
                    kingInCheck,
                    piecesCheckingKing,
                    pinnedPieces
                )
            }
            listOfMoves.clear()
        }

        for (rank in piece.square.rank + 1..7) {
            val moveSquare = Square(rank, piece.square.file + (rank - piece.square.rank))
            listOfMoves.add(moveSquare)
        }
        addMoves()
        for (rank in piece.square.rank + 1..7) {
            val moveSquare = Square(rank, piece.square.file - (rank - piece.square.rank))
            listOfMoves.add(moveSquare)
        }
        addMoves()
        for (rank in piece.square.rank - 1 downTo 0) {
            val moveSquare = Square(rank, piece.square.file + (rank - piece.square.rank))
            listOfMoves.add(moveSquare)
        }
        addMoves()
        for (rank in piece.square.rank - 1 downTo 0) {
            val moveSquare = Square(rank, piece.square.file - (rank - piece.square.rank))
            listOfMoves.add(moveSquare)
        }
        addMoves()
        piece.pinnedMoves.clear()
    }
}
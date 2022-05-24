package com.gomezdevlopment.chessnotationapp.model.pieces

import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic

class Bishop {
    private val gameLogic = GameLogic()

    fun moves(piece: ChessPiece, hashMap: MutableMap<Square, ChessPiece>, squaresToBlock: MutableList<Square>): MutableList<Square> {
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square
        for (rank in piece.square.rank + 1..7) {
            moveSquare = Square(rank, piece.square.file + (rank - piece.square.rank))
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) {
                println("List of Moves result $listOfMoves")
                break
            }
        }
        for (rank in piece.square.rank + 1..7) {
            moveSquare = Square(rank, piece.square.file - (rank - piece.square.rank))
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) break
        }
        for (rank in piece.square.rank - 1 downTo 0) {
            moveSquare = Square(rank, piece.square.file + (rank - piece.square.rank))
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) break
        }
        for (rank in piece.square.rank - 1 downTo 0) {
            moveSquare = Square(rank, piece.square.file - (rank - piece.square.rank))
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) break
        }

        val moves = mutableListOf<Square>()
        for (move in listOfMoves) {
            if (!gameLogic.illegalMove(move, hashMap, piece, squaresToBlock)) {
                moves.add(move)
            }
        }
        return moves
    }
}
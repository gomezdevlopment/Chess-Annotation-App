package com.gomezdevlopment.chessnotationapp.model.game_logic

import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.Square

class GameLogic {
    private fun isPieceInPath(hashMap: MutableMap<Square, ChessPiece>, square: Square): Boolean {
        if (hashMap.contains(square)) {
            return true
        }
        return false
    }

    fun pieceInPath(
        hashMap: MutableMap<Square, ChessPiece>,
        listOfMoves: MutableList<Square>,
        square: Square
    ): Boolean {
        listOfMoves.add(square)
        return (isPieceInPath(hashMap, square))
    }

    fun illegalMove(
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        piece: ChessPiece,
        squaresToBlock: MutableList<Square>
    ): Boolean {
        //prevent king from entering into check
        if (squaresToBlock.isNotEmpty()) {
            if (squaresToBlock.contains(square) && piece.piece == "king") return true

            if (!squaresToBlock.contains(square) && piece.piece != "king") return true
        }
        if (square.rank > 7 || square.rank < 0 || square.file > 7 || square.file < 0) return true
        if (!isCapture(square, occupiedSquares, piece)) return true
        return false
    }

    private fun isCapture(
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        piece: ChessPiece
    ): Boolean {
        var isLegalMove = true
        if (occupiedSquares.containsKey(square)) {
            isLegalMove = occupiedSquares[square]?.color != piece.color
            if(piece.piece == "pawn"){
                if(piece.square.file == square.file){
                    isLegalMove = false
                }
            }
        }
        return isLegalMove
    }

    fun isEnPassant(
        previousSquare: Square,
        currentSquare: Square,
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        piece: ChessPiece
    ): Boolean {
        if (piece.color == "white") {
            if (previousSquare.rank == 6 && currentSquare.rank == 4) {
                if (square.rank == currentSquare.rank + 1 && square.file == currentSquare.file) {
                    if (occupiedSquares[currentSquare]?.piece == "pawn" && occupiedSquares[currentSquare]?.color == "black") {
                        return true
                    }
                }
            }
        } else {
            if (piece.color == "black") {
                if (previousSquare.rank == 1 && currentSquare.rank == 3) {
                    if (square.rank == currentSquare.rank - 1 && square.file == currentSquare.file) {
                        if (occupiedSquares[currentSquare]?.piece == "pawn" && occupiedSquares[currentSquare]?.color == "white") {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }
}
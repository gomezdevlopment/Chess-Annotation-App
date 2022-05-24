package com.gomezdevlopment.chessnotationapp.model.game_logic

import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.Square

class GameLogic {
    private fun isPieceInPath(hashMap: MutableMap<Square, ChessPiece>, square: Square): Boolean{
        if(hashMap.contains(square)){
            return true
        }
        return false
    }

    fun pieceInPath(hashMap: MutableMap<Square, ChessPiece>, listOfMoves: MutableList<Square>, square: Square): Boolean{
        listOfMoves.add(square)
        return (isPieceInPath(hashMap, square))
    }

    fun illegalMove(square: Square, occupiedSquares: MutableMap<Square, ChessPiece>, piece: ChessPiece, squaresToBlock: MutableList<Square>): Boolean {
        //prevent king from entering into check
        if(squaresToBlock.isNotEmpty()){
            if(squaresToBlock.contains(square) && piece.piece == "king") return true

            if(!squaresToBlock.contains(square) && piece.piece != "king") return true
        }
        if(square.rank > 7 || square.rank < 0 || square.file > 7 || square.file < 0) return true
        if(!isCapture(square, occupiedSquares, piece)) return true
        return false
    }

    fun isCapture(square: Square, occupiedSquares: MutableMap<Square, ChessPiece>, piece: ChessPiece): Boolean{
        var isLegalMove = true
        if(occupiedSquares.containsKey(square)){
            isLegalMove = occupiedSquares[square]?.color != piece.color
        }
        return isLegalMove
    }
}
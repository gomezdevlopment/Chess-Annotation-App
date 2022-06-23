package com.gomezdevlopment.chessnotationapp.model.pieces

import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic2

class Knight {
    private val gameLogic = GameLogic()
    private val gameLogic2 = GameLogic2()

    fun moves(
        piece: ChessPiece,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        piecesCheckingKing: MutableList<ChessPiece>,
        pinnedPieces: MutableList<ChessPiece>
    ){

        if(!pinnedPieces.contains(piece)){
            piece.pinnedMoves.clear()
        }

        gameLogic2.clearMoves(piece)

        var moveSquare = Square(piece.square.rank + 2, piece.square.file + 1)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank + 2, piece.square.file - 1)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank - 2, piece.square.file + 1)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank - 2, piece.square.file - 1)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank + 1, piece.square.file + 2)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank - 1, piece.square.file + 2)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank + 1, piece.square.file - 2)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank - 1, piece.square.file - 2)
        piece.pseudoLegalMoves.add(moveSquare)

        piece.pseudoLegalMoves.forEach {
            if(gameLogic2.isOnBoard(it)){
                piece.attacks.add(it)
            }
            if(gameLogic2.isLegalMove(it, occupiedSquares, piece, piecesCheckingKing)){
                piece.legalMoves.add(it)
            }
        }
        piece.pinnedMoves.clear()
    }
}
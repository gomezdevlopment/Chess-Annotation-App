package com.gomezdevlopment.chessnotationapp.model.pieces

import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic

class King {
    private val gameLogic = GameLogic()

    fun moves(
        piece: ChessPiece,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        attackedSquares: MutableList<Square>,
        kingCanCastleKingSide: Boolean,
        kingCanCastleQueenSide: Boolean,
        kingSquare: Square,
        piecesCheckingKing: MutableList<ChessPiece>
    ) {
        gameLogic.clearMoves(piece)
        var moveSquare = Square(piece.square.rank + 1, piece.square.file + 1)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank + 1, piece.square.file - 1)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank + 1, piece.square.file)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank - 1, piece.square.file)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank, piece.square.file + 1)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank, piece.square.file - 1)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank - 1, piece.square.file - 1)
        piece.pseudoLegalMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank - 1, piece.square.file + 1)
        piece.pseudoLegalMoves.add(moveSquare)

        piece.pseudoLegalMoves.forEach {
            if(gameLogic.isOnBoard(it)){
                piece.attacks.add(it)
            }
            if(gameLogic.isLegalMove(it, occupiedSquares, piece, piecesCheckingKing)
                && !attackedSquares.contains(it)){
                piece.legalMoves.add(it)
            }
        }
        
        //King Side Castling
        moveSquare = Square(piece.square.rank, piece.square.file + 2)
        if(kingCanCastleKingSide){
            if(gameLogic.canKingSideCastle(moveSquare, occupiedSquares, attackedSquares)){
                piece.legalMoves.add(moveSquare)
            }
        }
        //Queen Side Castling
        moveSquare = Square(piece.square.rank, piece.square.file - 2)
        if(kingCanCastleQueenSide){
            if(gameLogic.canQueenSideCastle(moveSquare, occupiedSquares, attackedSquares)){
                piece.legalMoves.add(moveSquare)
            }
        }
    }
}
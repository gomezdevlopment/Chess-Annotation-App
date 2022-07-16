package com.gomezdevlopment.chessnotationapp.model.pieces

import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic

class Pawn {
    private val gameLogic = GameLogic()

    fun moves(
        piece: ChessPiece,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        previousSquare: Square,
        currentSquare: Square,
        piecesCheckingKing: MutableList<ChessPiece>,
        pinnedPieces: MutableList<ChessPiece>,
        pinnedPiecesPreviousTurn: List<ChessPiece>
    ) {

        var wasPinned = pinnedPiecesPreviousTurn.contains(piece)

        pinnedPieces.forEach {
            if(it.piece == piece.piece){
                if(it.color == piece.color){
                    if(it.square == piece.square){
                        wasPinned = true
                    }
                }
            }
        }
        if (!pinnedPieces.contains(piece)) {
            piece.pinnedMoves.clear()
        }

        gameLogic.clearMoves(piece)

        var moveSquare: Square
        if (piece.color == "white") {
            moveSquare = Square(piece.square.rank + 1, piece.square.file)
            if (!occupiedSquares.containsKey(moveSquare)) {
                piece.pseudoLegalMoves.add(moveSquare)
                if (piece.square.rank == 1) {
                    moveSquare = Square(piece.square.rank + 2, piece.square.file)
                    if (!occupiedSquares.containsKey(moveSquare)) {
                        piece.pseudoLegalMoves.add(moveSquare)
                    }
                }
            }
        }

        if (piece.color == "black") {
            moveSquare = Square(piece.square.rank - 1, piece.square.file)
            if (!occupiedSquares.containsKey(moveSquare)) {
                piece.pseudoLegalMoves.add(moveSquare)
                if (piece.square.rank == 6) {
                    moveSquare = Square(piece.square.rank - 2, piece.square.file)
                    if (!occupiedSquares.containsKey(moveSquare)) {
                        piece.pseudoLegalMoves.add(moveSquare)
                    }
                }
            }
        }

        piece.pseudoLegalMoves.forEach {
            if (gameLogic.isLegalMove(
                    it,
                    occupiedSquares,
                    piece,
                    piecesCheckingKing,
                )
            ) {
                piece.legalMoves.add(it)
            }
        }

        //Check for Diagonal Captures
        if (piece.color == "white") {
            moveSquare = Square(piece.square.rank + 1, piece.square.file - 1)
            piece.pseudoLegalMoves.add(moveSquare)
            piece.attacks.add(moveSquare)
            moveSquare = Square(piece.square.rank + 1, piece.square.file + 1)
            piece.pseudoLegalMoves.add(moveSquare)
            piece.attacks.add(moveSquare)
        } else {
            moveSquare = Square(piece.square.rank - 1, piece.square.file - 1)
            piece.pseudoLegalMoves.add(moveSquare)
            piece.attacks.add(moveSquare)
            moveSquare = Square(piece.square.rank - 1, piece.square.file + 1)
            piece.pseudoLegalMoves.add(moveSquare)
            piece.attacks.add(moveSquare)
        }

        piece.attacks.forEach { move ->
            if (occupiedSquares.containsKey(move)) {
                if (gameLogic.isLegalMove(move, occupiedSquares, piece, piecesCheckingKing)) {
                    piece.legalMoves.add(move)
                }
            }
            if (gameLogic.isEnPassant(previousSquare, currentSquare, move, occupiedSquares, piece, wasPinned)) {
                piece.legalMoves.add(move)
            }
        }
        piece.pinnedMoves.clear()
    }
}
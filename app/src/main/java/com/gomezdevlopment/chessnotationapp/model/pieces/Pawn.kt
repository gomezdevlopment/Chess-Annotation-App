package com.gomezdevlopment.chessnotationapp.model.pieces

import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic

class Pawn {
    private val gameLogic = GameLogic()

    fun moves(
        piece: ChessPiece,
        hashMap: MutableMap<Square, ChessPiece>,
        squaresToBlock: MutableList<Square>,
        previousSquare: Square,
        currentSquare: Square,
        xRayAttacks: MutableList<Square>,
        kingSquare:Square,
        piecesCheckingKing: MutableList<Square>,
        checkDefendedPieces: Boolean
    ): MutableList<Square> {
        val listOfMoves = mutableListOf<Square>()

        var moveSquare: Square

        if (piece.color == "white") {
            moveSquare = Square(piece.square.rank + 1, piece.square.file)
            if(!gameLogic.isPieceInPath(hashMap, moveSquare)){
                listOfMoves.add(moveSquare)
                if (piece.square.rank == 1) {
                    moveSquare = Square(piece.square.rank + 2, piece.square.file)
                    listOfMoves.add(moveSquare)
                }
            }
        }

        if (piece.color == "black") {
            moveSquare = Square(piece.square.rank - 1, piece.square.file)
            if(!gameLogic.isPieceInPath(hashMap, moveSquare)){
                listOfMoves.add(moveSquare)
                if (piece.square.rank == 6) {
                    moveSquare = Square(piece.square.rank - 2, piece.square.file)
                    listOfMoves.add(moveSquare)
                }
            }
        }

        //Check for Diagonal Captures
        if (piece.color == "white") {
            moveSquare = Square(piece.square.rank + 1, piece.square.file - 1)
            if (hashMap.containsKey(moveSquare)
                || gameLogic.isEnPassant(previousSquare, currentSquare, moveSquare, hashMap, piece, kingSquare, squaresToBlock)
            ) {
                listOfMoves.add(moveSquare)
            }

            moveSquare = Square(piece.square.rank + 1, piece.square.file + 1)
            if (hashMap.containsKey(moveSquare)
                || gameLogic.isEnPassant(previousSquare, currentSquare, moveSquare, hashMap, piece, kingSquare, squaresToBlock)
            ) {
                listOfMoves.add(moveSquare)
            }

        } else {
            moveSquare = Square(piece.square.rank - 1, piece.square.file - 1)
            if (hashMap.containsKey(moveSquare)
               || gameLogic.isEnPassant(previousSquare, currentSquare, moveSquare, hashMap, piece, kingSquare, squaresToBlock)
            ) {
                listOfMoves.add(moveSquare)
            }
            moveSquare = Square(piece.square.rank - 1, piece.square.file + 1)
            if (hashMap.containsKey(moveSquare)
                || gameLogic.isEnPassant(previousSquare, currentSquare, moveSquare, hashMap, piece, kingSquare, squaresToBlock)
            ) {
                listOfMoves.add(moveSquare)
            }
        }

        val moves = mutableListOf<Square>()
        for (move in listOfMoves) {
            if (!gameLogic.illegalMove(move, hashMap, piece, squaresToBlock, xRayAttacks, kingSquare, piecesCheckingKing)) {
                moves.add(move)
            }
        }
        return moves
    }

    fun pawnAttacks(piece: ChessPiece): MutableList<Square> {
        val attacks = mutableListOf<Square>()
        var moveSquare: Square
        if (piece.color == "white") {
            moveSquare = Square(piece.square.rank + 1, piece.square.file - 1)
            attacks.add(moveSquare)
            moveSquare = Square(piece.square.rank + 1, piece.square.file + 1)
            attacks.add(moveSquare)
        } else {
            moveSquare = Square(piece.square.rank - 1, piece.square.file - 1)
            attacks.add(moveSquare)
            moveSquare = Square(piece.square.rank - 1, piece.square.file + 1)
            attacks.add(moveSquare)
        }
        return attacks
    }
}
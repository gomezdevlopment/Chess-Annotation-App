package com.gomezdevlopment.chessnotationapp.model.pieces

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic

class Rook {
    private val gameLogic = GameLogic()

    fun moves(
        piece: ChessPiece,
        hashMap: MutableMap<Square, ChessPiece>,
        squaresToBlock: MutableList<Square>,
        checkDefendedPieces: Boolean,
        xRayAttacks: MutableList<Square>,
        kingSquare: Square
    ): MutableList<Square> {
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square
        for (rank in piece.square.rank + 1..7) {
            moveSquare = Square(rank, piece.square.file)
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) break
        }
        for (rank in piece.square.rank - 1 downTo 0) {
            moveSquare = Square(rank, piece.square.file)
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) break
        }
        for (file in piece.square.file + 1..7) {
            moveSquare = Square(piece.square.rank, file)
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) break
        }
        for (file in piece.square.file - 1 downTo 0) {
            moveSquare = Square(piece.square.rank, file)
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) break
        }

        val moves = mutableListOf<Square>()
        for (move in listOfMoves) {
            if (!gameLogic.illegalMove(
                    move,
                    hashMap,
                    piece,
                    squaresToBlock,
                    xRayAttacks,
                    kingSquare
                )
            ) {
                moves.add(move)

            }
            if (checkDefendedPieces && gameLogic.isDefending(move, hashMap)) {
                moves.add(move)
            }
        }
        return moves
    }

    fun xRayAttacks(
        piece: ChessPiece,
        hashMap: MutableMap<Square, ChessPiece>
    ): MutableList<Square> {
        val listOfMoves = mutableListOf<Square>()
        val listOfXRays = mutableListOf<Square>()
        var moveSquare: Square
        var firstPieceAlreadyFound = false
        for (rank in piece.square.rank + 1..7) {
            moveSquare = Square(rank, piece.square.file)
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) {
                if (firstPieceAlreadyFound) {
                    if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
                        gameLogic.addXRayMoves(listOfXRays, listOfMoves)
                    }
                    break
                } else {
                    firstPieceAlreadyFound = true
                }
            }
        }
        listOfMoves.clear()
        firstPieceAlreadyFound = false
        for (rank in piece.square.rank - 1 downTo 0) {
            moveSquare = Square(rank, piece.square.file)
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) {
                if (firstPieceAlreadyFound) {
                    if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
                        gameLogic.addXRayMoves(listOfXRays, listOfMoves)
                    }
                    break
                } else {
                    firstPieceAlreadyFound = true
                }
            }
        }
        listOfMoves.clear()
        firstPieceAlreadyFound = false
        for (file in piece.square.file + 1..7) {
            moveSquare = Square(piece.square.rank, file)
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) {
                if (firstPieceAlreadyFound) {
                    if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
                        gameLogic.addXRayMoves(listOfXRays, listOfMoves)
                    }
                    break
                } else {
                    firstPieceAlreadyFound = true
                }
            }
        }
        listOfMoves.clear()
        firstPieceAlreadyFound = false
        for (file in piece.square.file - 1 downTo 0) {
            moveSquare = Square(piece.square.rank, file)
            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) {
                if (firstPieceAlreadyFound) {
                    if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
                        gameLogic.addXRayMoves(listOfXRays, listOfMoves)
                    }
                    break
                } else {
                    firstPieceAlreadyFound = true
                }
            }
        }
        return listOfXRays
    }
}


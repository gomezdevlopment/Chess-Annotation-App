package com.gomezdevlopment.chessnotationapp.model.pieces

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic2

class Rook {
    private val gameLogic = GameLogic()
    //private val gameLogic2 = GameLogic2()

    fun moves(
        piece: ChessPiece,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        kingInCheck: MutableState<Boolean>,
        kingSquare: Square,
        piecesCheckingKing: MutableList<Square>,
        pinnedPieces: MutableList<ChessPiece>,
    ) {

        if(!pinnedPieces.contains(piece)){
            piece.pinnedMoves.clear()
        }

        GameLogic2().clearMoves(piece)
        val listOfMoves = mutableListOf<Square>()

        fun addMoves() {
            val gameLogic = GameLogic2()
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
            val moveSquare = Square(rank, piece.square.file)
            listOfMoves.add(moveSquare)
        }
        addMoves()

        for (rank in piece.square.rank - 1 downTo 0) {
            val moveSquare = Square(rank, piece.square.file)
            listOfMoves.add(moveSquare)
        }
        addMoves()

        for (file in piece.square.file + 1..7) {
            val moveSquare = Square(piece.square.rank, file)
            listOfMoves.add(moveSquare)
        }
        addMoves()

        for (file in piece.square.file - 1 downTo 0) {
            val moveSquare = Square(piece.square.rank, file)
            listOfMoves.add(moveSquare)
        }
        addMoves()
        piece.pinnedMoves.clear()
    }

//    fun xRayAttacks(
//        piece: ChessPiece,
//        hashMap: MutableMap<Square, ChessPiece>,
//        isKingInCheck: Boolean,
//        squaresToBlock: MutableList<Square>
//    ): MutableList<Square> {
//        val listOfMoves = mutableListOf<Square>()
//        val listOfXRays = mutableListOf<Square>()
//        var moveSquare: Square
//        var firstPieceAlreadyFound = false
//        var kingFound = false
//        for (rank in piece.square.rank + 1..7) {
//            moveSquare = Square(rank, piece.square.file)
//            if (kingFound && isKingInCheck) {
//                listOfXRays.add(moveSquare)
//                continue
//            }
//            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) {
//                if (firstPieceAlreadyFound) {
//                    if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
//                        gameLogic.addXRayMoves(listOfXRays, listOfMoves)
//                    }
//                    break
//                } else {
//                    firstPieceAlreadyFound = true
//                    if (isKingInCheck) {
//                        if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
//                            gameLogic.addXRayMoves(listOfXRays, listOfMoves)
//                            squaresToBlock.addAll(listOfXRays)
//                            kingFound = true
//                        } else {
//                            break
//                        }
//                    }
//                }
//            }
//        }
//        listOfMoves.clear()
//        firstPieceAlreadyFound = false
//        kingFound = false
//        for (rank in piece.square.rank - 1 downTo 0) {
//            moveSquare = Square(rank, piece.square.file)
//            if (kingFound && isKingInCheck) {
//                listOfXRays.add(moveSquare)
//                continue
//            }
//            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) {
//                if (firstPieceAlreadyFound) {
//                    if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
//                        gameLogic.addXRayMoves(listOfXRays, listOfMoves)
//                    }
//                    break
//                } else {
//                    firstPieceAlreadyFound = true
//                    if (isKingInCheck) {
//                        if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
//                            gameLogic.addXRayMoves(listOfXRays, listOfMoves)
//                            squaresToBlock.addAll(listOfXRays)
//                            kingFound = true
//                        } else {
//                            break
//                        }
//                    }
//                }
//            }
//        }
//        listOfMoves.clear()
//        firstPieceAlreadyFound = false
//        kingFound = false
//        for (file in piece.square.file + 1..7) {
//            moveSquare = Square(piece.square.rank, file)
//            if (kingFound && isKingInCheck) {
//                listOfXRays.add(moveSquare)
//                continue
//            }
//            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) {
//                if (firstPieceAlreadyFound) {
//                    if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
//                        gameLogic.addXRayMoves(listOfXRays, listOfMoves)
//                    }
//                    break
//                } else {
//                    firstPieceAlreadyFound = true
//                    if (isKingInCheck) {
//                        if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
//                            gameLogic.addXRayMoves(listOfXRays, listOfMoves)
//                            squaresToBlock.addAll(listOfXRays)
//                            kingFound = true
//                        } else {
//                            break
//                        }
//                    }
//                }
//            }
//        }
//        listOfMoves.clear()
//        firstPieceAlreadyFound = false
//        kingFound = false
//        for (file in piece.square.file - 1 downTo 0) {
//            moveSquare = Square(piece.square.rank, file)
//            if (kingFound && isKingInCheck) {
//                listOfXRays.add(moveSquare)
//                continue
//            }
//            if (gameLogic.pieceInPath(hashMap, listOfMoves, moveSquare)) {
//                if (firstPieceAlreadyFound) {
//                    if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
//                        gameLogic.addXRayMoves(listOfXRays, listOfMoves)
//                    }
//                    break
//                } else {
//                    firstPieceAlreadyFound = true
//                    if (isKingInCheck) {
//                        if (gameLogic.squareContainsEnemyKing(hashMap, moveSquare, piece)) {
//                            gameLogic.addXRayMoves(listOfXRays, listOfMoves)
//                            squaresToBlock.addAll(listOfXRays)
//                            kingFound = true
//                        } else {
//                            break
//                        }
//                    }
//                }
//            }
//        }
//        return listOfXRays
//    }
}


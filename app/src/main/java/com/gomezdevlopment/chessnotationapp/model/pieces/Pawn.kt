package com.gomezdevlopment.chessnotationapp.model.pieces

import androidx.compose.runtime.mutableStateOf
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic2

class Pawn {
    private val gameLogic = GameLogic()
    private val gameLogic2 = GameLogic2()

    private val wasPinned = mutableStateOf(false)

    fun moves(
        piece: ChessPiece,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        squaresToBlock: MutableList<Square>,
        previousSquare: Square,
        currentSquare: Square,
        piecesCheckingKing: MutableList<Square>,
    ) {
        if(piece.pinnedMoves.isNotEmpty()){
            wasPinned.value = true
        }

        gameLogic2.clearMoves(piece)
        var moveSquare: Square
        if (piece.color == "white") {
            moveSquare = Square(piece.square.rank + 1, piece.square.file)
            if(!occupiedSquares.containsKey(moveSquare)){
                piece.pseudoLegalMoves.add(moveSquare)
                if (piece.square.rank == 1) {
                    moveSquare = Square(piece.square.rank + 2, piece.square.file)
                    if(!occupiedSquares.containsKey(moveSquare)){
                        piece.pseudoLegalMoves.add(moveSquare)
                    }
                }
            }
        }

        if (piece.color == "black") {
            moveSquare = Square(piece.square.rank - 1, piece.square.file)
            if(!occupiedSquares.containsKey(moveSquare)){
                piece.pseudoLegalMoves.add(moveSquare)
                if (piece.square.rank == 6) {
                    moveSquare = Square(piece.square.rank - 2, piece.square.file)
                    if(!occupiedSquares.containsKey(moveSquare)){
                        piece.pseudoLegalMoves.add(moveSquare)
                    }
                }
            }
        }

        piece.pseudoLegalMoves.forEach {
            if(gameLogic2.isLegalMove(it, occupiedSquares, piece, piecesCheckingKing, squaresToBlock)){
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

        if (occupiedSquares.containsKey(moveSquare)) {
            if(gameLogic2.isLegalMove(moveSquare, occupiedSquares, piece, piecesCheckingKing, squaresToBlock)){
                piece.legalMoves.add(moveSquare)
            }
            if(gameLogic2.isEnPassant(previousSquare, currentSquare,moveSquare, occupiedSquares, piece, wasPinned.value)){
                piece.legalMoves.add(moveSquare)
            }
        }
        piece.pinnedMoves.clear()
    }

//    fun pawnAttacks(piece: ChessPiece): MutableList<Square> {
//        val attacks = mutableListOf<Square>()
//        var moveSquare: Square
//        if (piece.color == "white") {
//            moveSquare = Square(piece.square.rank + 1, piece.square.file - 1)
//            attacks.add(moveSquare)
//            moveSquare = Square(piece.square.rank + 1, piece.square.file + 1)
//            attacks.add(moveSquare)
//        } else {
//            moveSquare = Square(piece.square.rank - 1, piece.square.file - 1)
//            attacks.add(moveSquare)
//            moveSquare = Square(piece.square.rank - 1, piece.square.file + 1)
//            attacks.add(moveSquare)
//        }
//        return attacks
//    }
}
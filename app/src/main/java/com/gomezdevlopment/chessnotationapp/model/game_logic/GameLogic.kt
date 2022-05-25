package com.gomezdevlopment.chessnotationapp.model.game_logic

import androidx.compose.runtime.mutableStateOf
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.model.pieces.King

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
        //prevent king from entering into check when in check
        if (squaresToBlock.isNotEmpty()) {
            if (squaresToBlock.contains(square) && piece.piece == "king") return true

            if (!squaresToBlock.contains(square) && piece.piece != "king") return true

            //if(isPinned()) return true
        }
        if (square.rank > 7 || square.rank < 0 || square.file > 7 || square.file < 0) return true
        if (!isCapture(square, occupiedSquares, piece)) return true
        //if(isPinned(occupiedSquares, piece, kingSquare)) return true
        return false
    }

    private fun isPinned(
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        piece: ChessPiece,
        kingSquare: Square
    ) : Boolean{

        var pinned = mutableStateOf(false)
        //if(squaresToBlock.contains())
        if(kingSquare.rank == piece.square.rank){
            //Pinned left side
            if(kingSquare.file > piece.square.file){
                var attacked = mutableStateOf(false)
                for(file in 0 until kingSquare.file){
                    val square = Square(kingSquare.rank, file)
                    if(occupiedSquares.containsKey(square)){
                        if(occupiedSquares[square] == piece && !attacked.value){
                            pinned.value = false
                            break
                        }else if(occupiedSquares[square] == piece && attacked.value){
                            pinned.value = true
                        }else if((occupiedSquares[square]?.piece == "queen" || occupiedSquares[square]?.piece == "rook") && occupiedSquares[square]?.color != piece.color){
                            attacked.value = true
                        }else{
                            pinned.value = false
                        }
                    }
                }
                if(pinned.value){
                    if(square.rank == kingSquare.rank){
                        return false
                    }
                }
            }
        }

        return pinned.value
    }

    private fun isCapture(
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        piece: ChessPiece
    ): Boolean {
        var isLegalMove = true
        if (occupiedSquares.containsKey(square)) {
            isLegalMove = occupiedSquares[square]?.color != piece.color
            if (piece.piece == "pawn") {
                if (piece.square.file == square.file) {
                    isLegalMove = false
                }
            }
        }
        return isLegalMove
    }

    fun isDefending(
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>
    ): Boolean {
        var isDefending = false
        if (occupiedSquares.containsKey(square)) {
            isDefending = true
        }
        return isDefending
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

    fun canKingSideCastle(
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        attackedSquares: MutableList<Square>,
    ): Boolean {
        if (
            occupiedSquares.containsKey(Square(square.rank, square.file))
            || occupiedSquares.containsKey(Square(square.rank, square.file - 1))
            || attackedSquares.contains(Square(square.rank, square.file))
            || attackedSquares.contains(Square(square.rank, square.file - 1))
            || attackedSquares.contains(Square(square.rank, square.file - 2))
        ) {
            return false
        }

        return true
    }

    fun canQueenSideCastle(
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        attackedSquares: MutableList<Square>,
    ): Boolean {
        if (
            occupiedSquares.containsKey(Square(square.rank, square.file))
            || occupiedSquares.containsKey(Square(square.rank, square.file - 1))
            || occupiedSquares.containsKey(Square(square.rank, square.file + 1))
            || attackedSquares.contains(Square(square.rank, square.file))
            || attackedSquares.contains(Square(square.rank, square.file + 1))
            || attackedSquares.contains(Square(square.rank, square.file + 2))
        ) {
            return false
        }
        return true
    }
}
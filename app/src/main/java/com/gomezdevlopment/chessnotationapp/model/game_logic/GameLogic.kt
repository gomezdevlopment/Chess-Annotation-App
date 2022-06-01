package com.gomezdevlopment.chessnotationapp.model.game_logic

import androidx.compose.runtime.mutableStateOf
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.model.pieces.*

class GameLogic {
    fun isPieceInPath(hashMap: MutableMap<Square, ChessPiece>, square: Square): Boolean {
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
        squaresToBlock: MutableList<Square>,
        xRayAttacks: MutableList<Square>,
        kingSquare: Square,
        piecesCheckingKing: MutableList<Square>
    ): Boolean {
        //prevent king from entering into check when in check
        if (piecesCheckingKing.isNotEmpty()) {
            if (squaresToBlock.contains(square) && piece.piece == "king"){
                return true
            }

            if (!squaresToBlock.contains(square) && !piecesCheckingKing.contains(square) && piece.piece != "king") {
                return true
            }

            if(piecesCheckingKing.size > 1 && piece.piece != "king"){
                return true }

            //if(isPinned()) return true
        }
        if (square.rank > 7 || square.rank < 0 || square.file > 7 || square.file < 0) return true
        if (!isCapture(square, occupiedSquares, piece)) return true
        if (isPinned(square, xRayAttacks, piece, kingSquare)) return true
        return false
    }

    fun isPinned(
        square: Square,
        xRayAttacks: MutableList<Square>,
        piece: ChessPiece,
        kingSquare: Square
    ): Boolean {
        if (xRayAttacks.contains(kingSquare) && xRayAttacks.contains(piece.square) && piece.piece != "king") {
            //Pinned Vertically
            if (kingSquare.file == piece.square.file) {
                if (kingSquare.file != square.file) {
                    return true
                }
            }
            //Pinned Horizontally
            else if (kingSquare.rank == piece.square.rank) {
                if (square.rank != kingSquare.rank) {
                    return true
                }
            }
            //Pinned Upper Left Diagonal
            else if (piece.square.rank > kingSquare.rank && piece.square.file < kingSquare.file) {
                if (square.rank - kingSquare.rank != kingSquare.file - square.file) {
                    return true
                }
            }
            //Pinned Upper Right Diagonal
            else if (piece.square.rank > kingSquare.rank && piece.square.file > kingSquare.file) {
                //println("pawn pinned")
                if (square.rank - kingSquare.rank != square.file - kingSquare.file) {
                    return true
                }
            }
            //Pinned Lower Left Diagonal
            else if (piece.square.rank < kingSquare.rank && piece.square.file < kingSquare.file) {
                if (kingSquare.rank - square.rank != kingSquare.file - square.file) {
                    return true
                }
            }
            //Pinned Lower Right Diagonal
            else if (piece.square.rank < kingSquare.rank && piece.square.file > kingSquare.file) {
                if (kingSquare.rank - square.rank != square.file - kingSquare.file) {
                    return true
                }
            }
        }
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
        piece: ChessPiece,
        kingSquare: Square
    ): Boolean {
        if(piece.piece == "pawn"){
            if (piece.color == "white") {
                if (previousSquare.rank == 6 && currentSquare.rank == 4) {
                    if (square.rank == currentSquare.rank + 1 && square.file == currentSquare.file) {
                        if (occupiedSquares[currentSquare]?.piece == "pawn" && occupiedSquares[currentSquare]?.color == "black") {
                            if(!kingInCheckIfEnPassant(piece, occupiedSquares, kingSquare)){
                                return true
                            }
                        }
                    }
                }
            } else {
                if (piece.color == "black") {
                    if (previousSquare.rank == 1 && currentSquare.rank == 3) {
                        if (square.rank == currentSquare.rank - 1 && square.file == currentSquare.file) {
                            if (occupiedSquares[currentSquare]?.piece == "pawn" && occupiedSquares[currentSquare]?.color == "white") {
                                if(!kingInCheckIfEnPassant(piece, occupiedSquares, kingSquare)){
                                    return true
                                }
                            }
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

    fun squareContainsEnemyKing(
        hashMap: MutableMap<Square, ChessPiece>,
        square: Square,
        piece: ChessPiece
    ): Boolean {
        if (hashMap[square]?.piece == "king" && hashMap[square]?.color != piece.color) {
            return true
        }
        return false
    }

    fun addXRayMoves(listOfXRays: MutableList<Square>, listOfMoves: MutableList<Square>) {
        listOfXRays.addAll(listOfMoves)
        listOfMoves.clear()
    }

    private fun kingInCheckIfEnPassant(
        pawn: ChessPiece,
        hashMap: MutableMap<Square, ChessPiece>,
        kingSquare: Square
    ): Boolean {
        val xRayAttacksIfEnPassant = mutableListOf<Square>()
        val hashMapIfEnPassant: MutableMap<Square, ChessPiece> = HashMap()
        hashMapIfEnPassant.putAll(hashMap)
        hashMapIfEnPassant.remove(pawn.square)
        val iterator = hashMap.keys.iterator()
        while (iterator.hasNext()) {
            val square = iterator.next()
            val piece = hashMap[square]
            if (piece?.color != pawn.color) {
                when (piece?.piece) {
                    "queen" -> { xRayAttacksIfEnPassant.addAll(Queen().xRayAttacks(piece, hashMapIfEnPassant, false))}
                    "rook" -> { xRayAttacksIfEnPassant.addAll(Rook().xRayAttacks(piece, hashMapIfEnPassant, false)) }
                    "bishop" -> { xRayAttacksIfEnPassant.addAll(Bishop().xRayAttacks(piece, hashMapIfEnPassant, false))}
                }
            }
        }
        return xRayAttacksIfEnPassant.contains(kingSquare) && xRayAttacksIfEnPassant.contains(pawn.square)
    }
}
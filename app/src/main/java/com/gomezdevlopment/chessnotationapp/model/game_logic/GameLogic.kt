package com.gomezdevlopment.chessnotationapp.model.game_logic

import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import kotlin.math.abs

class GameLogic() {

    private val kingFound = mutableStateOf(false)
    private val firstPieceFound = mutableStateOf(false)
    private val secondPieceFound = mutableStateOf(false)
    private val pinnedPiece = mutableListOf<ChessPiece>()

    fun addMove(
        occupiedSquares: MutableMap<Square, ChessPiece>,
        square: Square,
        enemyKing: Square,
        piece: ChessPiece,
        moves: List<Square>,
        kingInCheck: MutableState<Boolean>,
        piecesCheckingKing: MutableList<ChessPiece>,
        pinnedPieces: MutableList<ChessPiece>
    ) {
        piece.pseudoLegalMoves.add(square)

        if (!firstPieceFound.value) {
            piece.attacks.add(square)
            if (isLegalMove(square, occupiedSquares, piece, piecesCheckingKing)) {
                piece.legalMoves.add(square)
            }
        }

        if ((firstPieceFound.value && secondPieceFound.value) || kingFound.value) return

        if (occupiedSquares.contains(square)) {
            if (!firstPieceFound.value) {

                val pieceOnSquare = occupiedSquares[square]
                if (pieceOnSquare?.color != piece.color && pieceOnSquare?.piece != "king") {
                    if (pieceOnSquare != null) {
                        pinnedPiece.add(pieceOnSquare)
                    }
                }

                piece.attacks.add(square)

                firstPieceFound.value = true

                if (square == enemyKing) {
                    piece.xRays.addAll(moves)
                    kingFound.value = true
                    kingInCheck.value = true
                }
                return
            }

            if (!secondPieceFound.value) {
                secondPieceFound.value = true
                if (square == enemyKing) {
                    piece.xRays.addAll(moves)
                    if(pinnedPiece.isNotEmpty()){
                        pinnedPieces.addAll(pinnedPiece)
                        pinnedPieces.last().pinnedMoves.add(piece.square)
                        pinnedPieces.last().pinnedMoves.addAll(moves)
                    }
                    kingFound.value = true
                }
                return
            }
        }
        return
    }

    fun isLegalMove(
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        piece: ChessPiece,
        piecesCheckingKing: MutableList<ChessPiece>,
    ): Boolean {
        if (!isOnBoard(square)) return false
        if (squareContainsFriendlyPiece(square, occupiedSquares, piece)) return false

        if (piece.pinnedMoves.isNotEmpty()) {
            if (piece.pinnedMoves.contains(square)) return true
            return false
        }

        if (piecesCheckingKing.isNotEmpty()) {
            if (piece.piece == "king") {
                piecesCheckingKing.forEach {
                    val checkingPiece = it
                    if (checkingPiece.piece != "pawn") {
                        if (checkingPiece.pseudoLegalMoves.contains(square)) {
                            return false
                        }
                    }
                }
                return true
            }

            if(piecesCheckingKing.size > 1) return false

            if (piecesCheckingKing[0].square == square) return true

            if(piecesCheckingKing[0].xRays.contains(square)) return true
            return false
        }

        return true
    }

    fun isOnBoard(square: Square): Boolean {
        if (square.rank > 7 || square.rank < 0 || square.file > 7 || square.file < 0) return false
        return true
    }

    private fun squareContainsFriendlyPiece(
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        piece: ChessPiece
    ): Boolean {
        if (occupiedSquares.containsKey(square)) {
            if (piece.color == occupiedSquares[square]?.color) return true
        }
        return false
    }

    fun clearMoves(piece: ChessPiece) {
        piece.legalMoves.clear()
        piece.pseudoLegalMoves.clear()
        piece.attacks.clear()
        piece.xRays.clear()
    }

    fun isEnPassant(
        previousSquare: Square,
        currentSquare: Square,
        square: Square,
        occupiedSquares: MutableMap<Square, ChessPiece>,
        piece: ChessPiece,
        wasPinned: Boolean
    ): Boolean {
        if (abs(previousSquare.rank - currentSquare.rank) == 2) {
            val rank = (previousSquare.rank + currentSquare.rank) / 2
            if (square == Square(rank, currentSquare.file)) {
                if (occupiedSquares[currentSquare]?.piece == "pawn" && occupiedSquares[currentSquare]?.color != piece.color) {
                    if (!wasPinned) {
                        return true
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
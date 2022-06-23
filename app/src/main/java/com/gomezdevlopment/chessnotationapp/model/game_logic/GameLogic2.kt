package com.gomezdevlopment.chessnotationapp.model.game_logic

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import kotlin.math.abs

class GameLogic2() {

    private val kingFound = mutableStateOf(false)
    private val firstPieceFound = mutableStateOf(false)
    private val secondPieceFound = mutableStateOf(false)

    private var pinnedPiece: ChessPiece? = null

    fun addMove(
        occupiedSquares: MutableMap<Square, ChessPiece>,
        square: Square,
        enemyKing: Square,
        piece: ChessPiece,
        moves: List<Square>,
        kingInCheck: MutableState<Boolean>,
        piecesCheckingKing: MutableList<Square>,
        pinnedPieces: MutableList<ChessPiece>
    ) {

        piece.pseudoLegalMoves.add(square)

        if (!firstPieceFound.value) {
            if (isLegalMove(square, occupiedSquares, piece, piecesCheckingKing)) {
                piece.legalMoves.add(square)
            }
        }

        if ((firstPieceFound.value && secondPieceFound.value) || kingFound.value) return

        if (occupiedSquares.contains(square)) {
            if (!firstPieceFound.value) {

                val pieceOnSquare = occupiedSquares[square]
                if (pieceOnSquare?.color != piece.color && pieceOnSquare?.piece != "king") {
                    pinnedPiece = pieceOnSquare
                    if (pieceOnSquare != null) {
                        pinnedPieces.add(pieceOnSquare)
                    }
                }

                piece.attacks.addAll(moves)

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
                    pinnedPiece?.pinnedMoves?.addAll(moves)
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
        piecesCheckingKing: MutableList<Square>,
    ): Boolean {
        if (!isOnBoard(square)) return false
        if (squareContainsFriendlyPiece(square, occupiedSquares, piece)) return false
        if (piece.pinnedMoves.isNotEmpty()) {
            if (piece.pinnedMoves.contains(square)) {
                if (piecesCheckingKing.contains(square)) {
                    return true
                }
                return false
            }
            return false

        }
        if (piecesCheckingKing.isNotEmpty()) {
            if (piece.piece == "king") {
                piecesCheckingKing.forEach {
                    val checkingPiece = occupiedSquares[it]
                    if (checkingPiece?.piece != "pawn") {
                        if (checkingPiece?.pseudoLegalMoves?.contains(square) == true) {
                            return false
                        }
                    }
                }
                return true
            }
            if (piecesCheckingKing.contains(square)) {
                return true
            }
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
                if (occupiedSquares[currentSquare]?.piece == "pawn") {
                    if (!wasPinned) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
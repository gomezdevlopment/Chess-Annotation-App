package com.gomezdevlopment.chessnotationapp.model.pieces

import androidx.compose.runtime.MutableState
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square

class Piece(
    private val piece: ChessPiece,
    private val occupiedSquares: MutableMap<Square, ChessPiece>,
    private val squaresToBlock: MutableList<Square>,
    private val attacks: MutableList<Square>,
    private val canCastleKingSide: Boolean,
    private val canCastleQueenSide: Boolean,
    private val kingInCheck: MutableState<Boolean>,
    private val kingSquare: Square,
    private val checksOnKing: MutableList<Square>,
    private val piecesCheckingKing: MutableList<Square>,
    private val previousSquare: Square,
    private val currentSquare: Square
) {
    fun moves(): MutableList<Square> {
        val listOfMoves = mutableListOf<Square>()

        when (piece.piece) {
            "pawn" -> {listOfMoves.addAll(pawnMoves())}
            "rook" -> {listOfMoves.addAll(rookMoves())}
            "knight" -> {listOfMoves.addAll(knightMoves())}
            "bishop" -> {listOfMoves.addAll(bishopMoves())}
            "king" -> {listOfMoves.addAll(kingMoves())}
            "queen" -> {listOfMoves.addAll(queenMoves())}
        }
        return listOfMoves
    }

    private fun pawnMoves(): MutableList<Square> {
        Pawn().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            previousSquare,
            currentSquare,
            piecesCheckingKing
        )
        return piece.legalMoves
    }

    private fun kingMoves(): MutableList<Square> {
        King().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            attacks,
            canCastleKingSide,
            canCastleQueenSide,
            kingSquare,
            checksOnKing,
            piecesCheckingKing,
        )
        return piece.legalMoves
    }

    private fun bishopMoves(): MutableList<Square> {
        Bishop().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            kingInCheck,
            kingSquare,
            piecesCheckingKing
        )
        return piece.legalMoves
    }

    private fun rookMoves(): MutableList<Square> {
        Rook().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            kingInCheck,
            kingSquare,
            piecesCheckingKing
        )
        return piece.legalMoves
    }

    private fun queenMoves(): MutableList<Square> {
        Queen().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            kingInCheck,
            kingSquare,
            piecesCheckingKing
        )
        return piece.legalMoves
    }

    private fun knightMoves(): MutableList<Square> {
        Knight().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            piecesCheckingKing
        )
        return piece.legalMoves
    }
}
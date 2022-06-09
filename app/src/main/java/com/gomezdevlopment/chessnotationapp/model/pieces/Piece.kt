package com.gomezdevlopment.chessnotationapp.model.pieces

import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.Square

class Piece(
    private val piece: ChessPiece,
    private val occupiedSquares: MutableMap<Square, ChessPiece>,
    private val squaresToBlock: MutableList<Square>,
    private val attacks: MutableList<Square>,
    private val canCastleKingSide: Boolean,
    private val canCastleQueenSide: Boolean,
    private val xRayAttacks: MutableList<Square>,
    private val kingSquare: Square,
    private val checksOnKing: MutableList<Square>,
    private val piecesCheckingKing: MutableList<Square>,
    private val checkDefendedPieces: Boolean,
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
        return Pawn().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            previousSquare,
            currentSquare,
            xRayAttacks,
            kingSquare,
            piecesCheckingKing,
            checkDefendedPieces
        )
    }

    private fun kingMoves(): MutableList<Square> {
        return King().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            attacks,
            canCastleKingSide,
            canCastleQueenSide,
            xRayAttacks,
            kingSquare,
            checksOnKing,
            piecesCheckingKing,
            checkDefendedPieces
        )
    }

    private fun bishopMoves(): MutableList<Square> {
        return Bishop().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            checkDefendedPieces,
            xRayAttacks,
            kingSquare,
            piecesCheckingKing
        )
    }

    private fun rookMoves(): MutableList<Square> {
        return Rook().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            checkDefendedPieces,
            xRayAttacks,
            kingSquare,
            piecesCheckingKing
        )
    }

    private fun queenMoves(): MutableList<Square> {
        return Queen().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            checkDefendedPieces,
            xRayAttacks,
            kingSquare,
            piecesCheckingKing
        )
    }

    private fun knightMoves(): MutableList<Square> {
        return Knight().moves(
            piece,
            occupiedSquares,
            squaresToBlock,
            checkDefendedPieces,
            xRayAttacks,
            kingSquare,
            piecesCheckingKing
        )
    }
}
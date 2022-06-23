package com.gomezdevlopment.chessnotationapp.model.game_logic

import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square

class Notation(private val stringBuilder: StringBuilder, private val square: Square) {

    private lateinit var pieceMoved: String

    fun piece(
        piece: ChessPiece,
        piecesOnBoard: MutableList<ChessPiece>
    ) {
        pieceMoved = piece.piece
        when (piece.piece) {
            "king" -> stringBuilder.append("K")
            "queen" -> stringBuilder.append("Q")
            "rook" -> stringBuilder.append("R")
            "bishop" -> stringBuilder.append("B")
            "knight" -> stringBuilder.append("N")
            "pawn" -> stringBuilder.append(fileLetter(piece.square.file))
        }
        for (otherPiece in piecesOnBoard) {
            if (piece != otherPiece) {
                if (piece.piece == otherPiece.piece && piece.color == otherPiece.color && piece.piece != "pawn") {
                    if (otherPiece.legalMoves.contains(square)) {
                        if (piece.square.rank == otherPiece.square.rank) {
                            stringBuilder.append(fileLetter(piece.square.file))
                        } else {
                            stringBuilder.append(piece.square.rank + 1)
                        }
                    }
                }
            }
        }
    }

    fun castleKingSide() {
        stringBuilder.append("0-0")
    }

    fun castleQueenSide() {
        stringBuilder.append("0-0-0")
    }

    fun capture() {
        stringBuilder.append("x")
        square(true)
    }

    fun square(capture: Boolean) {
        if (pieceMoved == "pawn" && !capture) {
            stringBuilder.delete(0, 1)
        }
        stringBuilder.append(fileLetter(square.file))
        stringBuilder.append(square.rank + 1)
    }

    fun promotion(promotionPiece: ChessPiece) {
        when (promotionPiece.piece) {
            "queen" -> stringBuilder.append("=Q")
            "rook" -> stringBuilder.append("=R")
            "bishop" -> stringBuilder.append("=B")
            "knight" -> stringBuilder.append("=N")
        }
    }

    fun checkmateOrCheck(checkmate: Boolean, check: Boolean) {
        if (checkmate) {
            stringBuilder.append("#")
        } else if (check) {
            stringBuilder.append("+")
        }
    }

    private fun fileLetter(file: Int): String {
        when (file) {
            0 -> return "a"
            1 -> return "b"
            2 -> return "c"
            3 -> return "d"
            4 -> return "e"
            5 -> return "f"
            6 -> return "g"
            7 -> return "h"
        }
        return ""
    }
}

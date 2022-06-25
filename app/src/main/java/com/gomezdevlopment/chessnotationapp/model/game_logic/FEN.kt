package com.gomezdevlopment.chessnotationapp.model.game_logic

import androidx.compose.runtime.MutableState
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.pieces.ChessPieces

class FEN {
    fun getGameStateAsFEN(
        hashMap: MutableMap<Square, ChessPiece>,
        playerTurn: MutableState<String>,
        whiteCanCastleKingSide: MutableState<Boolean>,
        whiteCanCastleQueenSide: MutableState<Boolean>,
        blackCanCastleKingSide: MutableState<Boolean>,
        blackCanCastleQueenSide: MutableState<Boolean>
    ): String {
        val fenString = StringBuilder()
        for (rank in 7 downTo 0) {
            var emptySquares = 0
            for (file in 0..7) {
                if (hashMap.containsKey(Square(rank, file))) {
                    if (emptySquares != 0) {
                        fenString.append(emptySquares)
                    }
                    emptySquares = 0
                    val piece = hashMap[Square(rank, file)]
                    when (piece?.piece) {
                        "rook" -> {
                            if (piece.color == "white") {
                                fenString.append("R")
                            } else {
                                fenString.append("r")
                            }
                        }
                        "knight" -> {
                            if (piece.color == "white") {
                                fenString.append("N")
                            } else {
                                fenString.append("n")
                            }
                        }
                        "bishop" -> {
                            if (piece.color == "white") {
                                fenString.append("B")
                            } else {
                                fenString.append("b")
                            }
                        }
                        "queen" -> {
                            if (piece.color == "white") {
                                fenString.append("Q")
                            } else {
                                fenString.append("q")
                            }
                        }
                        "king" -> {
                            if (piece.color == "white") {
                                fenString.append("K")
                            } else {
                                fenString.append("k")
                            }
                        }
                        "pawn" -> {
                            if (piece.color == "white") {
                                fenString.append("P")
                            } else {
                                fenString.append("p")
                            }
                        }
                    }
                } else {
                    emptySquares += 1
                    if (file == 7) {
                        if (emptySquares != 0) {
                            fenString.append(emptySquares)
                        }
                    }
                }
            }
            if (rank != 0) {
                fenString.append("/")
            }
        }
        fenString.append(" ")
        when (playerTurn.value) {
            "white" -> fenString.append("w ")
            "black" -> fenString.append("b ")
        }

        if (!whiteCanCastleKingSide.value && !whiteCanCastleQueenSide.value && !blackCanCastleKingSide.value && !blackCanCastleQueenSide.value) {
            fenString.append("- ")
        } else {
            if (whiteCanCastleKingSide.value) {
                fenString.append("K")
            }
            if (whiteCanCastleQueenSide.value) {
                fenString.append("Q")
            }
            if (blackCanCastleKingSide.value) {
                fenString.append("k")
            }
            if (blackCanCastleQueenSide.value) {
                fenString.append("q")
            }
            fenString.append(" ")
        }
        return fenString.toString()
    }

    fun parseFEN(
        fen: String,
        playerTurn: MutableState<String>,
        whiteCanCastleKingSide: MutableState<Boolean>,
        whiteCanCastleQueenSide: MutableState<Boolean>,
        blackCanCastleKingSide: MutableState<Boolean>,
        blackCanCastleQueenSide: MutableState<Boolean>,
        whiteKingSquare: MutableState<Square>,
        blackKingSquare: MutableState<Square>,
    ): MutableList<ChessPiece> {
        val pieces = mutableListOf<ChessPiece>()
        val splitFen = fen.split(" ")
        val piecePositions = splitFen[0].split("/")
        var rank = 8
        for (string in piecePositions) {
            rank--
            var file = 0
            for (char in string) {
                if (char.isDigit()) {
                    file += char.digitToInt()
                } else {
                    when (char) {
                        'r' -> {
                            val newPiece = ChessPieces().blackRook(rank, file)
                            pieces.add(newPiece)
                        }
                        'n' -> {
                            val newPiece = ChessPieces().blackKnight(rank, file)
                            pieces.add(newPiece)
                        }
                        'b' -> {
                            val newPiece = ChessPieces().blackBishop(rank, file)
                            pieces.add(newPiece)
                        }
                        'q' -> {
                            val newPiece = ChessPieces().blackQueen(rank, file)
                            pieces.add(newPiece)
                        }
                        'k' -> {
                            val newPiece = ChessPieces().blackKing(rank, file)
                            blackKingSquare.value = Square(rank, file)
                            pieces.add(newPiece)
                        }
                        'p' -> {
                            val newPiece = ChessPieces().blackPawn(rank, file)
                            pieces.add(newPiece)
                        }
                        'R' -> {
                            val newPiece = ChessPieces().whiteRook(rank, file)
                            pieces.add(newPiece)
                        }
                        'N' -> {
                            val newPiece = ChessPieces().whiteKnight(rank, file)
                            pieces.add(newPiece)
                        }
                        'B' -> {
                            val newPiece = ChessPieces().whiteBishop(rank, file)
                            pieces.add(newPiece)
                        }
                        'Q' -> {
                            val newPiece = ChessPieces().whiteQueen(rank, file)
                            pieces.add(newPiece)
                        }
                        'K' -> {
                            val newPiece = ChessPieces().whiteKing(rank, file)
                            whiteKingSquare.value = Square(rank, file)
                            pieces.add(newPiece)
                        }
                        'P' -> {
                            val newPiece = ChessPieces().whitePawn(rank, file)
                            pieces.add(newPiece)
                        }
                    }
                    file++
                }
            }
        }

        when (splitFen[1]) {
            "w" -> playerTurn.value = "white"
            "b" -> playerTurn.value = "black"
        }

        for (char in splitFen[2]) {
            when (char) {
                'K' -> whiteCanCastleKingSide.value = true
                'Q' -> whiteCanCastleQueenSide.value = true
                'k' -> blackCanCastleKingSide.value = true
                'q' -> blackCanCastleQueenSide.value = true
                '-' -> {
                    whiteCanCastleKingSide.value = false
                    whiteCanCastleQueenSide.value = false
                    blackCanCastleKingSide.value = false
                    blackCanCastleQueenSide.value = false
                }
            }
        }
        return pieces
    }
}
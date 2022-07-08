package com.gomezdevlopment.chessnotationapp.model.game_logic

import androidx.compose.runtime.MutableState
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.pieces.ChessPieces
import com.gomezdevlopment.chessnotationapp.view.theming.alpha

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
        val chessPieces = ChessPieces()
        for (string in piecePositions) {
            rank--
            var file = 0
            for (char in string) {
                if (char.isDigit()) {
                    file += char.digitToInt()
                } else {
                    var newPiece = chessPieces.blackRook(rank, file)
                    when (char) {
                        'r' -> { newPiece = chessPieces.blackRook(rank, file) }
                        'n' -> { newPiece = chessPieces.blackKnight(rank, file) }
                        'b' -> { newPiece = chessPieces.blackBishop(rank, file) }
                        'q' -> { newPiece = chessPieces.blackQueen(rank, file) }
                        'k' -> { newPiece = chessPieces.blackKing(rank, file)
                            blackKingSquare.value = Square(rank, file)
                        }
                        'p' -> { newPiece = chessPieces.blackPawn(rank, file) }
                        'R' -> { newPiece = chessPieces.whiteRook(rank, file) }
                        'N' -> { newPiece = chessPieces.whiteKnight(rank, file) }
                        'B' -> { newPiece = chessPieces.whiteBishop(rank, file) }
                        'Q' -> { newPiece = chessPieces.whiteQueen(rank, file) }
                        'K' -> { newPiece = chessPieces.whiteKing(rank, file)
                            whiteKingSquare.value = Square(rank, file)
                        }
                        'P' -> { newPiece = chessPieces.whitePawn(rank, file) }
                    }
                    pieces.add(newPiece)
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
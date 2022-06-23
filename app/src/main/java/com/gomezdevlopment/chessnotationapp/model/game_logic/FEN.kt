package com.gomezdevlopment.chessnotationapp.model.game_logic

import androidx.compose.runtime.MutableState
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square

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
        blackKingSquare: MutableState<Square>
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
                            pieces.add(
                                ChessPiece(
                                    "black",
                                    "rook",
                                    R.drawable.ic_br_alpha,
                                    Square(rank, file),
                                    5, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                        }
                        'n' -> {
                            pieces.add(
                                ChessPiece(
                                    "black",
                                    "knight",
                                    R.drawable.ic_bn_alpha,
                                    Square(rank, file),
                                    3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                        }
                        'b' -> {
                            pieces.add(
                                ChessPiece(
                                    "black",
                                    "bishop",
                                    R.drawable.ic_bb_alpha,
                                    Square(rank, file),
                                    3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                        }
                        'q' -> {
                            pieces.add(
                                ChessPiece(
                                    "black",
                                    "queen",
                                    R.drawable.ic_bq_alpha,
                                    Square(rank, file),
                                    9, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                        }
                        'k' -> {
                            pieces.add(
                                ChessPiece(
                                    "black",
                                    "king",
                                    R.drawable.ic_bk_alpha,
                                    Square(rank, file),
                                    0, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                            blackKingSquare.value = Square(rank, file)
                        }
                        'p' -> {
                            pieces.add(
                                ChessPiece(
                                    "black",
                                    "pawn",
                                    R.drawable.ic_bp_alpha,
                                    Square(rank, file),
                                    1, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                        }
                        'R' -> {
                            pieces.add(
                                ChessPiece(
                                    "white",
                                    "rook",
                                    R.drawable.ic_wr_alpha,
                                    Square(rank, file),
                                    5, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                        }
                        'N' -> {
                            pieces.add(
                                ChessPiece(
                                    "white",
                                    "knight",
                                    R.drawable.ic_wn_alpha,
                                    Square(rank, file),
                                    3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                        }
                        'B' -> {
                            pieces.add(
                                ChessPiece(
                                    "white",
                                    "bishop",
                                    R.drawable.ic_wb_alpha,
                                    Square(rank, file),
                                    3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                        }
                        'Q' -> {
                            pieces.add(
                                ChessPiece(
                                    "white",
                                    "queen",
                                    R.drawable.ic_wq_alpha,
                                    Square(rank, file),
                                    9, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                        }
                        'K' -> {
                            pieces.add(
                                ChessPiece(
                                    "white",
                                    "king",
                                    R.drawable.ic_wk,
                                    Square(rank, file),
                                    0, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
                            whiteKingSquare.value = Square(rank, file)
                        }
                        'P' -> {
                            pieces.add(
                                ChessPiece(
                                    "white",
                                    "pawn",
                                    R.drawable.ic_wp_alpha,
                                    Square(rank, file),
                                    1, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
                                )
                            )
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
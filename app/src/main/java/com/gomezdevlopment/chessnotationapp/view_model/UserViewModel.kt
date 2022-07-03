package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.FEN
import com.gomezdevlopment.chessnotationapp.model.pieces.ChessPieces

class UserViewModel: ViewModel() {
    var destination = mutableStateOf("Games")
    var games = mutableListOf<Map<String, String>>(
        mapOf(
        "result" to "Loss",
        "opponent" to "Sai"),
        mapOf(
            "result" to "Draw",
            "opponent" to "Hikaru"),
        mapOf(
            "result" to "Win",
            "opponent" to "Fuku"),
        mapOf(
            "result" to "Win",
            "opponent" to "Akira"),
        )

    var finalBoardState = "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq "
    var color = "white"

    fun setBoard(){
       val pieces = parseFEN(finalBoardState)
    }

    fun parseFEN(
        fen: String,
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
                    var newPiece = ChessPieces().blackRook(rank, file)
                    when (char) {
                        'r' -> { newPiece = ChessPieces().blackRook(rank, file) }
                        'n' -> { newPiece = ChessPieces().blackKnight(rank, file) }
                        'b' -> { newPiece = ChessPieces().blackBishop(rank, file) }
                        'q' -> { newPiece = ChessPieces().blackQueen(rank, file) }
                        'k' -> { newPiece = ChessPieces().blackKing(rank, file) }
                        'p' -> { newPiece = ChessPieces().blackPawn(rank, file) }
                        'R' -> { newPiece = ChessPieces().whiteRook(rank, file) }
                        'N' -> { newPiece = ChessPieces().whiteKnight(rank, file) }
                        'B' -> { newPiece = ChessPieces().whiteBishop(rank, file) }
                        'Q' -> { newPiece = ChessPieces().whiteQueen(rank, file) }
                        'K' -> { newPiece = ChessPieces().whiteKing(rank, file) }
                        'P' -> { newPiece = ChessPieces().whitePawn(rank, file) }
                    }
                    pieces.add(newPiece)
                    file++
                }
            }
        }
        return pieces
    }
}
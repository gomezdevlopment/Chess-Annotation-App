package com.gomezdevlopment.chessnotationapp.model.repositories

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.firestore_interaction.FirestoreInteraction
import com.gomezdevlopment.chessnotationapp.model.pieces.ChessPieces
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserRepository @Inject constructor(val firestore: FirestoreInteraction): ViewModel() {
    fun goToGameReview(gameViewModel: GameViewModel, game: Map<String, String>, homeNavController: NavController){
        val annotations = mutableListOf<String>()
        for (i in 0..game.size - 4) {
            val fen = game[i.toString()]
            if (fen != null) {
                annotations.add(i, fen)
            }
        }
        gameViewModel.selectedNotationIndex.value = 0
        gameViewModel.previousGameStates.clear()
        annotations.forEach {
            gameViewModel.previousGameStates.add(
                GameState(
                    Square(10, 10),
                    Square(10, 10),
                    it
                )
            )
        }
        val notations = game["notations"]?.split(" ")?.filter { it.isNotBlank() }
        gameViewModel.notations.clear()
        if (notations != null) {
            gameViewModel.notations.addAll(notations)
        }
        gameViewModel.setGameState(0)
        homeNavController.navigate("gameReview")
    }

    fun parseFEN(
        fen: String,
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
                        'r' -> {
                            newPiece = chessPieces.blackRook(rank, file)
                        }
                        'n' -> {
                            newPiece = chessPieces.blackKnight(rank, file)
                        }
                        'b' -> {
                            newPiece = chessPieces.blackBishop(rank, file)
                        }
                        'q' -> {
                            newPiece = chessPieces.blackQueen(rank, file)
                        }
                        'k' -> {
                            newPiece = chessPieces.blackKing(rank, file)
                        }
                        'p' -> {
                            newPiece = chessPieces.blackPawn(rank, file)
                        }
                        'R' -> {
                            newPiece = chessPieces.whiteRook(rank, file)
                        }
                        'N' -> {
                            newPiece = chessPieces.whiteKnight(rank, file)
                        }
                        'B' -> {
                            newPiece = chessPieces.whiteBishop(rank, file)
                        }
                        'Q' -> {
                            newPiece = chessPieces.whiteQueen(rank, file)
                        }
                        'K' -> {
                            newPiece = chessPieces.whiteKing(rank, file)
                        }
                        'P' -> {
                            newPiece = chessPieces.whitePawn(rank, file)
                        }
                    }
                    pieces.add(newPiece)
                    file++
                }
            }
        }
        return pieces
    }
}
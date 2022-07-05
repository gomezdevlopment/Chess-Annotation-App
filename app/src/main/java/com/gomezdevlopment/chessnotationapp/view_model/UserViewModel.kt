package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.firestore_interaction.FirestoreInteraction
import com.gomezdevlopment.chessnotationapp.model.game_logic.FEN
import com.gomezdevlopment.chessnotationapp.model.pieces.ChessPieces
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    var destination = mutableStateOf("Games")
    var userGames = mutableListOf<Map<String, String>>()
    var color = "white"
    val friendsList = mutableListOf<String>()
    val search = mutableStateOf("")

    fun initializeGamesList(){
        if(userGames.isEmpty()){
            MainActivity.user?.games?.forEachIndexed() { index, game ->
                userGames.add(index, game)
            }
            userGames.reverse()
        }
    }

    fun initializeFriendRequestListener(){
        FirestoreInteraction().friendRequestListener(this)
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

    fun onSearchChanged(newValue: String) {
        search.value = newValue
    }

    fun newSearch(friend: String) {
        viewModelScope.launch {
            FirestoreInteraction().sendFriendRequest(friend)
        }
    }
}
package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.data_classes.User
import com.gomezdevlopment.chessnotationapp.model.firestore_interaction.FirestoreInteraction
import com.gomezdevlopment.chessnotationapp.model.game_logic.FEN
import com.gomezdevlopment.chessnotationapp.model.pieces.ChessPieces
import com.gomezdevlopment.chessnotationapp.model.repositories.UserRepository
import com.gomezdevlopment.chessnotationapp.model.utils.UserSettings
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settings: UserSettings
) : ViewModel() {
    var destination = mutableStateOf("Games")
    var userGames = mutableListOf<Map<String, String>>()
    var color = "white"
    val friendsList = mutableListOf<String>()
    val search = mutableStateOf("")
    val chessBoardTheme by settings.chessBoardTheme

    val pieceThemeSelection by settings.pieceTheme

    fun setChessBoardTheme(boardTheme: Int){
        viewModelScope.launch {
            settings.setBoardTheme(boardTheme)
        }
    }

    fun setChessPieceTheme(pieceTheme: String){
        viewModelScope.launch {
            settings.setPieceTheme(pieceTheme)
        }
    }

    fun initializeGamesList() {
        if (userGames.isEmpty()) {
            MainActivity.user?.games?.forEachIndexed() { index, game ->
                userGames.add(index, game)
            }
            userGames.reverse()
        }
    }

    fun initializeFriendRequestListener() {
        userRepository.firestore.friendRequestListener(this)
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

    fun onSearchChanged(newValue: String) {
        search.value = newValue
    }

    fun newSearch(friend: String) {
        viewModelScope.launch {
            userRepository.firestore.sendFriendRequest(friend)
        }
    }
}
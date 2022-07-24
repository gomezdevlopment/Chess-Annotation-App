package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.repositories.UserRepository
import com.gomezdevlopment.chessnotationapp.model.utils.UserSettings
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settings: UserSettings
) : ViewModel() {
    var destination = mutableStateOf("Games")

    var color = "white"
    val friendsList = mutableListOf<String>()
    val search = mutableStateOf("")
    val chessBoardTheme by settings.chessBoardTheme
    val pieceThemeSelection by settings.pieceTheme
    val pieceAnimationSpeed by settings.pieceAnimationSpeed
    val themeSelection by settings.theme
    val highlightStyle by settings.highlightStyle
    var initialized by mutableStateOf(false)

    companion object {
        val userGames = mutableListOf<Map<String, String>>()
    }

    fun setChessBoardTheme(boardTheme: Int) {
        viewModelScope.launch {
            settings.setBoardTheme(boardTheme)
        }
    }

    fun setChessPieceTheme(pieceTheme: String) {
        viewModelScope.launch {
            settings.setPieceTheme(pieceTheme)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settings.setTheme(theme)
        }
    }

    fun setHighlightStyle(theme: String) {
        viewModelScope.launch {
            settings.setHighlightStyle(theme)
        }
    }

    fun setPieceAnimationSpeed(speed: Int) {
        viewModelScope.launch {
            settings.setPieceAnimationSpeed(speed)
        }
    }

    fun initializeGamesList() {
        val games = MainActivity.user?.games?.games
        if((games?.size ?: 0) > userGames.size){
            println("adding games")
            val newGames = games?.subList(userGames.size, games.size)
            newGames?.asReversed()?.forEachIndexed { index, game ->
                userGames.add(index, game)
            }
        }
//        MainActivity.user?.games?.games?.forEach { game ->
//            userGames.add(game)
//        }
//        userGames.reverse()
    }

//    fun initializeFriendRequestListener() {
//        userRepository.firestore.friendRequestListener(this)
//    }

    fun onSearchChanged(newValue: String) {
        search.value = newValue
    }

//    fun newSearch(friend: String) {
//        viewModelScope.launch {
//            userRepository.firestore.sendFriendRequest(friend)
//        }
//    }

    fun parseFEN(
        fen: String,
    ): MutableList<ChessPiece> {
        return userRepository.parseFEN(fen)
    }

    fun goToGameReview(
        gameViewModel: GameViewModel,
        game: Map<String, String>,
        homeNavController: NavController
    ) {
        userRepository.goToGameReview(gameViewModel, game, homeNavController)
    }
}
package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.repositories.UserRepository
import com.gomezdevlopment.chessnotationapp.model.utils.UserSettings
import com.gomezdevlopment.chessnotationapp.realtime_database.Friends
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
    val search = mutableStateOf("")
    val chessBoardTheme by settings.chessBoardTheme
    val pieceThemeSelection by settings.pieceTheme
    val pieceAnimationSpeed by settings.pieceAnimationSpeed
    val themeSelection by settings.theme
    val highlightStyle by settings.highlightStyle
    var initialized by mutableStateOf(false)
    val friendsList = userRepository.friendsList
    val requests = userRepository.requests
    val pending = userRepository.pending

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
        initializeFriendRequestListener()
    }

    fun initializeFriendRequestListener() {
        if(!initialized){
            val username = MainActivity.user?.username
            if(username != null){
                userRepository.checkFriends(username)
            }
            initialized = true
        }
    }

    fun onSearchChanged(newValue: String) {
        search.value = newValue
    }

    fun acceptFriendRequest(request: Friends){
        userRepository.acceptFriendRequest(request)
    }

    fun declineFriendRequest(request: Friends){
        userRepository.declineFriendRequest(request)
    }

    fun newSearch(sender: String, receiver: String) {
        viewModelScope.launch {
            val request = Friends(sender = sender, receiver = receiver)
            userRepository.sendFriendRequest(request)
        }
    }


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
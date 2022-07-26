package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.repositories.UserRepository
import com.gomezdevlopment.chessnotationapp.model.utils.UserSettings
import com.gomezdevlopment.chessnotationapp.realtime_database.Friends
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.grpc.InternalChannelz.id
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
    val openPrivacyPolicy = mutableStateOf(false)
    val signedOut: MutableLiveData<Boolean> = MutableLiveData(false)
    val signedOutStatusBarColorChange = mutableStateOf(false)
    val showDeleteAccountDialog = mutableStateOf(false)
    val deleteAccount = mutableStateOf(false)

    companion object {
        val userGames = mutableListOf<Map<String, String>>()
    }

    fun signOut() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
        signedOutStatusBarColorChange.value = true
        signedOut.postValue(true)
    }

    fun deleteAccount() {
        userRepository.deleteUserData()
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.delete()
        signedOut.postValue(true)
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
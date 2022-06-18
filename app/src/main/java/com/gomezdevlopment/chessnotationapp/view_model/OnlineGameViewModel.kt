package com.gomezdevlopment.chessnotationapp.view_model

import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.repositories.OnlineGameRepository
import kotlinx.coroutines.flow.MutableStateFlow

class OnlineGameViewModel: ViewModel() {
    private var onlineGameRepository = OnlineGameRepository()

    val enterPregameLobby = onlineGameRepository.enterPregameLobby
    val startGame = onlineGameRepository.startGame

    val navDestination = onlineGameRepository.navDestination


    fun joinGame(timeControl: Long) {
        onlineGameRepository.joinGame(timeControl)
    }


}
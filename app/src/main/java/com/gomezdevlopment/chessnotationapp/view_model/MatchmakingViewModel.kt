package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.model.repositories.MatchmakingRepository
import kotlinx.coroutines.launch

class MatchmakingViewModel: ViewModel() {
    private var matchmakingRepository = MatchmakingRepository()

    val navDestination = matchmakingRepository.navDestination
    val time by matchmakingRepository.time
    val resetSearch by matchmakingRepository.resetSearch
    //val gameDocumentReference = matchmakingRepository.gameDocumentReference

    fun joinGame(timeControl: Long) {
        viewModelScope.launch {
            matchmakingRepository.joinGame(timeControl)
        }
    }

    fun cancelSearch(){
        viewModelScope.launch {
            matchmakingRepository.cancelSearch()
        }
    }
}
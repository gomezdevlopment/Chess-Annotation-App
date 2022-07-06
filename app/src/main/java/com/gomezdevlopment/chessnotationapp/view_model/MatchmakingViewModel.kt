package com.gomezdevlopment.chessnotationapp.view_model

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.model.repositories.MatchmakingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchmakingViewModel @Inject constructor(private var matchmakingRepository: MatchmakingRepository): ViewModel() {
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
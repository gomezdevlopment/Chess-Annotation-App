package com.gomezdevlopment.chessnotationapp.realtime_database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RealtimeDatabaseRepository @Inject constructor(private val realtimeDatabaseDAO: RealtimeDatabaseDAO) :

    ViewModel() {
    fun addUserToDatabase(user: User) {
        viewModelScope.launch {
            realtimeDatabaseDAO.addUser(user)
        }
    }

    fun addGameToDatabase(onlineGame: OnlineGame, id: String, waitForMatch: () -> Unit) {
        viewModelScope.launch {
            realtimeDatabaseDAO.addGame(onlineGame, id) {
                waitForMatch()
            }
        }
    }

    fun getUserMap(email: String) {
        return realtimeDatabaseDAO.getUserMap(email)
    }
}
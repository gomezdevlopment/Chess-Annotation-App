package com.gomezdevlopment.chessnotationapp.realtime_database

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
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

    fun sendFriendRequest(request: Friends) {
        realtimeDatabaseDAO.sendFriendRequest(request)
    }

    fun checkFriends(
        username: String, friendsList: SnapshotStateList<Friends>,
        pending: SnapshotStateList<Friends>,
        requests: SnapshotStateList<Friends>
    ) {
        realtimeDatabaseDAO.checkFriends(username, friendsList, pending, requests)
    }

    fun acceptFriendRequest(request: Friends) {
        realtimeDatabaseDAO.acceptFriendRequest(request)
    }

    fun declineFriendRequest(request: Friends) {
        realtimeDatabaseDAO.declineFriendRequest(request)
    }
}
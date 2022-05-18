package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.model.AuthenticationRepository
import kotlinx.coroutines.launch

class SignOutViewModel(application: Application): AndroidViewModel(application) {

    private val appRepository = AuthenticationRepository(application)

    fun signOut() {
        viewModelScope.launch {
            appRepository.signOut()
        }
    }
}
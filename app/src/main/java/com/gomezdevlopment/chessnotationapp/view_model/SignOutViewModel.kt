package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.model.repositories.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel @Inject constructor(private val appRepository: AuthenticationRepository) : ViewModel() {

    //private val appRepository = AuthenticationRepository(application)

    fun signOut() {
        viewModelScope.launch {
            appRepository.signOut()
        }
    }
}
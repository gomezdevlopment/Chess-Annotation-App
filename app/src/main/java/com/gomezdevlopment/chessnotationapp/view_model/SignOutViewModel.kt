package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.gomezdevlopment.chessnotationapp.model.AuthenticationRepository

class SignOutViewModel(application: Application): AndroidViewModel(application) {

    private val appRepository = AuthenticationRepository(application)

    fun signOut() {
        appRepository.signOut()
    }
}
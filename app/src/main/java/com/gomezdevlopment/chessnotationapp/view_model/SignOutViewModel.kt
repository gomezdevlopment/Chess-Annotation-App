package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gomezdevlopment.chessnotationapp.model.AuthenticationRepository

class SignOutViewModel(application: Application): AndroidViewModel(application) {

    private val appRepository = AuthenticationRepository(application)
    private val signedOutMutableLiveData: MutableLiveData<Boolean> = appRepository.getSignedOutMutableLiveData()

    fun signOut() {
        appRepository.signOut()
    }

    fun getSignedOutMutableLiveData(): MutableLiveData<Boolean>{
        return signedOutMutableLiveData
    }
}
package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gomezdevlopment.chessnotationapp.model.AppRepository

class SignOutViewModel(application: Application): AndroidViewModel(application) {

    private val appRepository = AppRepository(application)
    private val signedOutMutableLiveData: MutableLiveData<Boolean> = appRepository.getSignedOutMutableLiveData()

    fun signOut() {
        appRepository.signOut()
    }

    fun getSignedOutMutableLiveData(): MutableLiveData<Boolean>{
        return signedOutMutableLiveData
    }
}
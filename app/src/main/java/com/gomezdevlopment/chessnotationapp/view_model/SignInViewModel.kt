package com.gomezdevlopment.chessnotationapp.view_model

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gomezdevlopment.chessnotationapp.model.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AuthenticationRepository(application)
    private val userMutableLiveData: MutableLiveData<FirebaseUser> = appRepository.getUserMutableLiveData()

    fun signUp(email: String, password: String, confirmPassword: String){
        appRepository.signUp(email, password, confirmPassword)
    }

    fun signIn(email: String, password: String){
        appRepository.signIn(email, password)
    }

    fun getUserMutableLiveDate(): MutableLiveData<FirebaseUser>{
        return userMutableLiveData
    }

    fun checkIfUserIsSignedIn(view: View){
        appRepository.checkIfUserIsSignedIn(view)
    }
}
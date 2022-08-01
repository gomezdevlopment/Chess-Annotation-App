package com.gomezdevlopment.chessnotationapp.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.model.repositories.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(private val appRepository: AuthenticationRepository) : ViewModel() {

    //private val appRepository = AuthenticationRepository(application)
    private val userMutableLiveData: MutableLiveData<FirebaseUser?> = appRepository.getUserMutableLiveData()
    val signedOut:MutableLiveData<Boolean> = appRepository.getSignedOutMutableLiveData()

    fun signUp(email: String, password: String, confirmPassword: String){
        viewModelScope.launch {
            appRepository.signUp(email, password, confirmPassword)
        }
    }

    fun signIn(email: String, password: String){
        viewModelScope.launch {
            appRepository.signIn(email, password)
        }
    }

    fun getUserMutableLiveDate(): MutableLiveData<FirebaseUser?> {
        return userMutableLiveData
    }

    fun checkIfUserIsSignedIn(){
        appRepository.checkIfUserIsSignedIn()
    }
}
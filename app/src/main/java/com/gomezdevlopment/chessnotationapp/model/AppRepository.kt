package com.gomezdevlopment.chessnotationapp.model

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AppRepository(private val application: Application) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()


    fun checkIfUserIsSignedIn(view: View){
        if(firebaseAuth.currentUser != null){
            Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }


    fun signUp(email: String, password: String, confirmPassword: String){
        if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
            if(password == confirmPassword){
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        userMutableLiveData.postValue(firebaseAuth.currentUser)
                        Toast.makeText(application, "Account Creation Successful", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(application, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(application, "Passwords do not match!", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(application, "Fields cannot be left empty!", Toast.LENGTH_LONG).show()
        }
    }

    fun signIn(email: String, password: String){
        if (email.isNotEmpty() && password.isNotEmpty()) {

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    userMutableLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    Toast.makeText(application, it.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(application, "Fields cannot be left empty!", Toast.LENGTH_LONG).show()
        }
    }

    fun getUserMutableLiveDate(): MutableLiveData<FirebaseUser>{
        return userMutableLiveData
    }
}
package com.gomezdevlopment.chessnotationapp.model.repositories

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.gomezdevlopment.chessnotationapp.realtime_database.RealtimeDatabaseRepository
import com.gomezdevlopment.chessnotationapp.realtime_database.User
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable
import javax.inject.Inject


class AuthenticationRepository @Inject constructor(private val dbRepository: RealtimeDatabaseRepository, private val application: Application) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val signedOutMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()


    fun checkIfUserIsSignedIn() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            userMutableLiveData.postValue(currentUser)
            currentUser.email?.let { dbRepository.getUserMap(it) }
        }else{
            userMutableLiveData.postValue(null)
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        userMutableLiveData.postValue(firebaseAuth.currentUser)
                        dbRepository.addUserToDatabase(createUser(email, password))
                        Toast.makeText(
                            application,
                            "Account Creation Successful",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(application, it.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            } else {
                Toast.makeText(application, "Passwords do not match!", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(application, "Fields cannot be left empty!", Toast.LENGTH_LONG).show()
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        userMutableLiveData.postValue(firebaseAuth.currentUser)
                        currentUser.email?.let { dbRepository.getUserMap(it) }
                        println("****************${user}")
                    }
                } else {
                    Toast.makeText(application, task.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(application, "Fields cannot be left empty!", Toast.LENGTH_LONG).show()
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        userMutableLiveData.postValue(null)
        signedOutMutableLiveData.postValue(true)
    }

    fun getUserMutableLiveData(): MutableLiveData<FirebaseUser> {
        return userMutableLiveData
    }

    fun getSignedOutMutableLiveData(): MutableLiveData<Boolean> {
        return signedOutMutableLiveData
    }

    private fun createUser(email: String, password: String): User {
        return User(email = email, password = password)
    }
}
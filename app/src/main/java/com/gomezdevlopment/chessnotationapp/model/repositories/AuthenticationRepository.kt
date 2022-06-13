package com.gomezdevlopment.chessnotationapp.model.repositories

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable


class AuthenticationRepository(private val application: Application) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val signedOutMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val db = Firebase.firestore


    fun checkIfUserIsSignedIn(view: View) {
        if (firebaseAuth.currentUser != null) {
            println("Home")
            Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }


    fun signUp(email: String, password: String, confirmPassword: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        userMutableLiveData.postValue(firebaseAuth.currentUser)
                        addFirestoreDocument(createUser(email, password))
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

    fun signOut() {
        firebaseAuth.signOut()
        signedOutMutableLiveData.postValue(true)
    }

    fun getUserMutableLiveData(): MutableLiveData<FirebaseUser> {
        return userMutableLiveData
    }

    fun getSignedOutMutableLiveData(): MutableLiveData<Boolean> {
        return signedOutMutableLiveData
    }

    private fun createUser(email: String, password: String): HashMap<String, Serializable> {
        return hashMapOf(
            "email" to email,
            "password" to password,
            "games" to arrayListOf<String>()
        )
    }

    private fun addFirestoreDocument(user: HashMap<String, Serializable>) {
        db.collection("users")
            .add(user)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
            }
    }
}
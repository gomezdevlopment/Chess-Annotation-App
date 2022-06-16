package com.gomezdevlopment.chessnotationapp.model.repositories

import android.app.Application
import android.content.ContentValues
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable


class AuthenticationRepository(private val application: Application) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val signedOutMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val db = Firebase.firestore


    fun checkIfUserIsSignedIn() {
        if (firebaseAuth.currentUser != null) {
            db.collection("users")
                .whereEqualTo("email", firebaseAuth.currentUser?.email)
                .get()
                .addOnSuccessListener {
                    if(it.isEmpty){
                        signOut()
                    }else{
                        userMutableLiveData.postValue(firebaseAuth.currentUser)
                    }
                }
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
                    createUsername()
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
        //val username = "Player${getRandomString()}"
        return hashMapOf(
            "email" to email,
            "password" to password,
            "games" to arrayListOf<String>(),
            "username" to "Player"
        )
    }

    private fun createUsername() {
        var username = "Player"

        fun checkIfUsernameIsUnique(name: String) {
            db.collection("users")
                .whereEqualTo("username", name)
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        println("creating new username")
                        username = "Player${getRandomString()}"
                        println(username)
                        checkIfUsernameIsUnique(username)
                    }else{
                        db.collection("users")
                            .whereEqualTo("email", firebaseAuth.currentUser?.email)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val user = document.toObject(User::class.java)
                                    println(user)
                                    if(user.username == "Player"){
                                        val docIdRef = db.collection("users").document(document.id)

                                        docIdRef.update("username", username)
                                            .addOnCompleteListener {
                                                println("Username updated to : $username")
                                            }
                                    }

                                }
                            }
                    }
                }
        }

        checkIfUsernameIsUnique(username)
    }

    private fun getRandomString(): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return List(7) { charset.shuffled().random() }
            .joinToString("")
    }

    private fun addFirestoreDocument(user: HashMap<String, Serializable>) {
        db.collection("users")
            .add(user)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                Toast.makeText(application, "Error Updating Database", Toast.LENGTH_LONG).show()
            }
    }
}
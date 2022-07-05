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
import com.gomezdevlopment.chessnotationapp.model.firestore_interaction.FirestoreInteraction
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.user
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userDocumentReference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.io.Serializable


class AuthenticationRepository(private val application: Application) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val signedOutMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val db = Firebase.firestore


    fun checkIfUserIsSignedIn() {
        if (firebaseAuth.currentUser != null) {
            getUserDocument()
            userMutableLiveData.postValue(firebaseAuth.currentUser)
        }else{
            userMutableLiveData.postValue(null)
        }

    }

    private fun getUserDocument() {
        db.collection("users")
            .whereEqualTo("email", firebaseAuth.currentUser?.email)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    signOut()
                } else {
                    val docRef = db.collection("users").document(it.documents[0].id)
                    userDocumentReference = docRef
                    val userDocumentToObject = it.documents[0].toObject(User::class.java)
                    if (userDocumentToObject != null) {
                        user = userDocumentToObject
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
                    getUserDocument()
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
        userMutableLiveData.postValue(null)
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
            "games" to arrayListOf<String>(),
            "username" to createUsername(),
            "wins" to 0,
            "losses" to 0,
            "draws" to 0,
            "puzzleRating" to 600,)
    }

    private fun createUsername(): String {
        var username = "Player${getRandomString()}"

        fun checkIfUsernameIsUnique(name: String) {
            db.collection("users")
                .whereEqualTo("username", name)
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        username = "Player${getRandomString()}"
                        checkIfUsernameIsUnique(username)
                    } else {
//                        userDocumentReference?.update("username", username)?.addOnCompleteListener {
//
//                        }
                    }
                }
        }
        checkIfUsernameIsUnique(username)
        return username
    }

    private fun getRandomString(): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return List(7) { charset.shuffled().random() }
            .joinToString("")
    }

    private fun createFriendsDocument(username: String): HashMap<String, Serializable> {
        return hashMapOf(
            "user" to username,
            "friends" to arrayListOf<String>())
    }

    private fun addFirestoreDocument(user: HashMap<String, Serializable>) {
        val username = user["username"].toString()
        db.collection("friends")
            .whereEqualTo("user", user["username"])
            .get()
            .addOnSuccessListener { query ->
                if(query.isEmpty){
                    db.collection("friends").add(createFriendsDocument(username)).addOnFailureListener {
                        println("failed to add doc")
                    }
                }
            }
            .addOnFailureListener {

            }
        db.collection("users")
            .add(user)
            .addOnFailureListener { e ->
                Toast.makeText(application, e.toString(), Toast.LENGTH_LONG).show()
            }

    }
}
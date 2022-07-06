package com.gomezdevlopment.chessnotationapp.model.repositories

import android.app.Application
import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder
import javax.inject.Inject

class HomeRepository( ) : ViewModel() {
//    private val firebaseAuth = FirebaseAuth.getInstance()
//    private val whiteMovesData: ArrayList<String> = arrayListOf()
//    private val blackMovesData: ArrayList<String> = arrayListOf()
//    private val pgnData: StringBuilder = StringBuilder("")
//    private val db = Firebase.firestore

    companion object {
        @Volatile
        private var INSTANCE: HomeRepository? = null

        fun getHomeRepository(): HomeRepository {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = HomeRepository()
                INSTANCE = instance
                return instance
            }
        }
    }

//    fun getWhiteMoves(): MutableLiveData<ArrayList<String>> {
//        return MutableLiveData(whiteMovesData)
//    }
//
//    fun getBlackMoves(): MutableLiveData<ArrayList<String>> {
//        return MutableLiveData(blackMovesData)
//    }
//
//    fun addWhiteMove(move: String) {
//        whiteMovesData.add(move)
//    }
//
//    fun addBlackMove(move: String) {
//        blackMovesData.add(move)
//    }
//
//    fun clearMoves() {
//        whiteMovesData.clear()
//        blackMovesData.clear()
//    }
//
//    fun createPGNString(): String {
//        pgnData.clear()
//        var index = 1
//        for (notation in whiteMovesData) {
//            pgnData.append("${index}. $notation ${blackMovesData[index - 1]} ")
//            index++
//        }
//        return pgnData.toString()
//    }
//
//    fun addGameToDatabase(application: Application, pgnString: String) {
//        db.collection("users")
//            .whereEqualTo("email", firebaseAuth.currentUser?.email)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val docIdRef = db.collection("users").document(document.id)
//                    docIdRef.update("games", FieldValue.arrayUnion(pgnString))
//                        .addOnCompleteListener {
//                            Toast.makeText(
//                                application,
//                                "Game Saved to Cloud Database",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
//            }
//    }
//
//    fun generateUserID() {
//
//    }
//
//    private fun getRandomString(): String {
//        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
//
//        return List(7) { charset.random() }
//            .joinToString("")
//    }
}


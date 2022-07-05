package com.gomezdevlopment.chessnotationapp.model.firestore_interaction

import com.gomezdevlopment.chessnotationapp.model.data_classes.OnlineGame
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.data_classes.User
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.user
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.Serializable

class FirestoreInteraction {
    fun writePromotion(playerTurn: String, string: String, promotionSelection: String) {
        if (MainActivity.gameDocumentReference != null) {
            if (playerTurn == MainActivity.userColor) {
                MainActivity.gameDocumentReference?.update(
                    "previousMove",
                    "$string$promotionSelection"
                )
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
        }
    }

    fun writeMove(playerTurn: String, string: String) {
        if (MainActivity.gameDocumentReference != null) {
            if (playerTurn == MainActivity.userColor) {
                MainActivity.gameDocumentReference?.update("previousMove", string)
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            } else {
                println("not your turn ${MainActivity.userColor}")
            }
        } else {
            println("document null")
        }
    }

    fun writeDrawOffer(playerTurn: String, value: String) {
        if (MainActivity.gameDocumentReference != null) {
            if (value == "accept" || value == "decline") {
                MainActivity.gameDocumentReference?.update("drawOffer", value)
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
            if (playerTurn == MainActivity.userColor) {
                MainActivity.gameDocumentReference?.update("drawOffer", value)
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
        }
    }

    fun writeRematchOffer(playerTurn: String, value: String) {
        if (MainActivity.gameDocumentReference != null) {
            if (playerTurn == MainActivity.userColor) {
                MainActivity.gameDocumentReference?.update("rematchOffer", value)
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
        }
    }

    fun writeResignation(userColor: String) {
        if (MainActivity.gameDocumentReference != null) {
            //if (playerTurn == MainActivity.userColor) {
            MainActivity.gameDocumentReference?.update("resignation", userColor)
                ?.addOnFailureListener { e ->
                    println(e)
                }
        }
    }

    fun writeGame(fenPositions: Map<String, String>) {
        if (MainActivity.userDocumentReference != null) {
            MainActivity.userDocumentReference?.update("games", FieldValue.arrayUnion(fenPositions))
                ?.addOnFailureListener {
                    println("Fail")
                }

        }
    }
    //MainActivity.userDocumentReference?.update("games", FieldValue.increment(1))

    fun incrementWins() {
        if (MainActivity.userDocumentReference != null) {
            MainActivity.userDocumentReference?.update("wins", FieldValue.increment(1))
        }
    }

    fun incrementLosses() {
        if (MainActivity.userDocumentReference != null) {
            MainActivity.userDocumentReference?.update("losses", FieldValue.increment(1))
        }
    }

    fun incrementDraws() {
        if (MainActivity.userDocumentReference != null) {
            MainActivity.userDocumentReference?.update("draws", FieldValue.increment(1))
        }
    }

    fun writePuzzleRating(rating: Int) {
        if (MainActivity.userDocumentReference != null) {
            MainActivity.userDocumentReference?.update("puzzleRating", rating)
        }
    }

    private val db = Firebase.firestore

    private fun friendRequest(
        sender: String,
        receiver: String
    ): HashMap<String, Serializable> {
        return hashMapOf(
            "friends" to arrayListOf(sender, receiver),
            "status" to "pending"
        )
    }

    fun sendFriendRequest(username: String) {
        val sendersUsername = user?.username
        if (sendersUsername != null) {
            if(username != sendersUsername){
                db.collection("friends")
                    .whereEqualTo("user", username)
                    .get()
                    .addOnSuccessListener { query ->
                        if(query.isEmpty){
                            println("user doesnt exist")
                        }
                        query.forEach { document ->
                            val docRef = db.collection("friends").document(document.id)
                            docRef.update("friends", FieldValue.arrayUnion(sendersUsername))
                            docRef.update("friends", FieldValue.arrayUnion("pending"))
                        }
                    }
            }else{
                println("That is your own username")
            }
        }
    }

    data class Friends(val username: String = "", val friends: MutableList<String> = mutableListOf())

    fun friendRequestListener(viewModel: UserViewModel) {
        val username = user?.username
        if (username != null) {
            db.collection("friends")
                .whereEqualTo("user", username)
                .get()
                .addOnSuccessListener { query ->
                    if(!query.isEmpty){
                        println("success")
                        query.forEach() { document ->
                            val docRef = db.collection("friends").document(document.id)
                            docRef.addSnapshotListener { value, error ->
                                val friendsListObject = document.toObject(Friends::class.java)
                                viewModel.friendsList.clear()
                                viewModel.friendsList.addAll(friendsListObject.friends)
                            }
                        }
                    }
                }
        }

    }
}


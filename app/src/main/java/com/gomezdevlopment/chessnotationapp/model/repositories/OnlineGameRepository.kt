package com.gomezdevlopment.chessnotationapp.model.repositories



import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.data_classes.OnlineGame
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OnlineGameRepository : ViewModel() {
    private val db = Firebase.firestore

    private val gamePoolCollection = "gamePool"

    val startGame = mutableStateOf(false)
    val enterPregameLobby = mutableStateOf(false)

    val navDestination = mutableStateOf("")


    fun joinGame(timeControl: Long) {
        db.collection(gamePoolCollection)
            .whereEqualTo("joinable", true)
            .whereEqualTo("timeControl", timeControl)
            .limit(1)
            .get()
            .addOnSuccessListener { gamePool ->
                if (gamePool.isEmpty) {
                    createGame(timeControl, MainActivity.user.username)
                } else {
                    val docIdRef = db.collection(gamePoolCollection).document(gamePool.documents[0].id)
                    val game = gamePool.documents[0].toObject(OnlineGame::class.java)
                    docIdRef.update("joinable", false)
                    if (game?.blackPlayer == "") {
                        println("Assigned black")
                        docIdRef.update("blackPlayer", MainActivity.user.username)
                            .addOnSuccessListener {
                                startGame.value = true
                                navDestination.value = "game"
                                println("Success")
                            }
                            .addOnFailureListener { e ->
                                println(e)
                            }
                    } else {
                        println("Assigned white")
                        docIdRef.update("whitePlayer", MainActivity.user.username)
                            .addOnSuccessListener {
                                startGame.value = true
                                navDestination.value = "game"
                                println("Success")
                            }
                            .addOnFailureListener { e ->
                                println(e)
                            }
                    }
                }
            }
            .addOnFailureListener {
                println("Fail")
            }
    }

    private fun createGame(timeControl: Long, username: String) {
        val randomColor = (0..1).shuffled().random()
        var whitePlayer = ""
        var blackPlayer = ""
        var playerColor = ""
        var opponentColor =""

        when (randomColor) {
            0 -> {
                whitePlayer = username
                playerColor = "whitePlayer"
                opponentColor = "blackPlayer"
            }
            1 -> {
                blackPlayer = username
                playerColor = "blackPlayer"
                opponentColor = "whitePlayer"
            }
        }

        val newGame = hashMapOf(
            "joinable" to true,
            "whitePlayer" to whitePlayer,
            "blackPlayer" to blackPlayer,
            "timeControl" to timeControl,
            "previousMove" to ""
        )
        db.collection(gamePoolCollection)
            .add(newGame)
            .addOnSuccessListener {
                db.collection(gamePoolCollection)
                    .whereEqualTo(playerColor, username)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { gamePool ->
                        val docRef = db.collection(gamePoolCollection).document(gamePool.documents[0].id)
                        //enterPregameLobby.value = true
                        waitForMatch(docRef, opponentColor)
                    }
                    .addOnFailureListener {
                        println("Fail")
                    }
            }
            .addOnFailureListener { e ->

            }
    }

    private fun waitForMatch(docRef: DocumentReference, opponentColor: String){
        var feedback: ListenerRegistration? = null
        feedback = docRef.addSnapshotListener { value, error ->
            if(value?.get(opponentColor) != ""){
                navDestination.value = "game"
                feedback?.remove()
            }

//            value?.let {
//                if(it.get(opponentColor) != ""){
//                    navDestination.value = "game"
//                    //navDestination.value = "game"
//                    println(navDestination.value)
//                    //feedback?.remove()
//                }
//            }
        }
    }


}
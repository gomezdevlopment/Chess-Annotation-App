package com.gomezdevlopment.chessnotationapp.model.repositories



import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.data_classes.OnlineGame
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.gameDocumentReference
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MatchmakingRepository : ViewModel() {

    private val db = Firebase.firestore
    private val gamePoolCollection = "gamePool"
    val navDestination = mutableStateOf("")
    val time = mutableStateOf(60000L)

    var matchSearch: ListenerRegistration? = null

    var resetSearch = mutableStateOf(0)
    //lateinit var gameDocumentReference: DocumentReference

    fun joinGame(timeControl: Long) {
        time.value = timeControl
        db.collection(gamePoolCollection)
            .whereEqualTo("joinable", true)
            .whereEqualTo("timeControl", timeControl)
            .limit(1)
            .get()
            .addOnSuccessListener { gamePool ->
                if (gamePool.isEmpty) {
                    println("empty")
                    createGame(timeControl, MainActivity.user?.username.toString())
                } else {
                    val docRef = db.collection(gamePoolCollection).document(gamePool.documents[0].id)
                    val game = gamePool.documents[0].toObject(OnlineGame::class.java)
                    gameDocumentReference = docRef
                    docRef.update("joinable", false)
                    if (game?.blackPlayer == "") {
                        userColor = "black"
                        docRef.update("blackPlayer", MainActivity.user?.username)
                            .addOnSuccessListener {
                                navDestination.value = "game"
                            }
                            .addOnFailureListener { e ->
                                println(e)
                            }
                    } else {
                        userColor ="white"
                        docRef.update("whitePlayer", MainActivity.user?.username)
                            .addOnSuccessListener {
                                navDestination.value = "game"
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
        var opponentColor = ""

        when (randomColor) {
            0 -> {
                whitePlayer = username
                playerColor = "whitePlayer"
                userColor = "white"
                opponentColor = "blackPlayer"
            }
            1 -> {
                blackPlayer = username
                playerColor = "blackPlayer"
                userColor = "black"
                opponentColor = "whitePlayer"
            }
        }

        val newGame = hashMapOf(
            "joinable" to true,
            "whitePlayer" to whitePlayer,
            "blackPlayer" to blackPlayer,
            "timeControl" to timeControl,
            "previousMove" to "",
            "resignation" to "",
            "drawOffer" to "",
            "rematchOffer" to "",
            "cancel" to false
        )
        db.collection(gamePoolCollection)
            .add(newGame)
            .addOnSuccessListener {
                waitForMatch(it, opponentColor)
            }
            .addOnFailureListener { e ->
                println(e.message)
            }
    }

    private fun waitForMatch(docRef: DocumentReference, opponentColor: String){
        matchSearch = docRef.addSnapshotListener { value, error ->
            val game = value?.toObject(OnlineGame::class.java)
            gameDocumentReference = docRef
            if(opponentColor == "whitePlayer"){
                if(game?.whitePlayer != ""){
                    navDestination.value = "game"
                    matchSearch?.remove()
                }
            }else{
                if(game?.blackPlayer != ""){
                    navDestination.value = "game"
                    matchSearch?.remove()
                }
            }

            if(game?.cancel == true){
                matchSearch?.remove()
            }
        }
    }

    fun cancelSearch(){
        gameDocumentReference?.update("cancel", true)
        gameDocumentReference?.delete()?.addOnFailureListener {
            println(it.message)
        }
        navDestination.value = "home"
        resetSearch.value += 1
    }
}
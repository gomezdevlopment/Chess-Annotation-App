package com.gomezdevlopment.chessnotationapp.model.repositories


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.realtime_database.OnlineGame
import com.gomezdevlopment.chessnotationapp.realtime_database.RealtimeDatabaseRepository
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.gameID
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.user
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.google.firebase.database.*
import javax.inject.Inject

class MatchmakingRepository @Inject constructor(
    private val db: FirebaseDatabase,
    private val dbRepository: RealtimeDatabaseRepository
) : ViewModel() {

    val navDestination = mutableStateOf("")
    val time = mutableStateOf(60000L)

    private var matchSearch: ValueEventListener? = null

    var resetSearch = mutableStateOf(0)
    val dbReference = db.getReference(OnlineGame::class.java.simpleName)
    var gameReference: DatabaseReference? = null

    fun joinGame(timeControl: Long) {
        time.value = timeControl
        val query = dbReference.orderByChild("timeControl").equalTo(timeControl.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var gameAvailable = false
                    snapshot.children.forEach { game ->
                        val onlineGame = game.getValue(OnlineGame::class.java)
                        if (onlineGame?.joinable == true) {
                            gameID = game.key
                            val key = gameID
                            val username = user?.username
                            if (key != null && username != null) {
                                if (onlineGame.blackPlayer == "") {
                                    gameAvailable = true
                                    userColor = "black"
                                    val gameCopy = onlineGame.copy(joinable = false, blackPlayer = username, players = 2)
                                    dbReference.child(key).updateChildren(gameToMap(gameCopy))
                                    navDestination.value = "game"
                                    MainActivity.game = gameCopy
                                } else {
                                    gameAvailable = true
                                    userColor = "white"
                                    val gameCopy = onlineGame.copy(joinable = false, whitePlayer = username, players = 2)
                                    dbReference.child(key).updateChildren(gameToMap(gameCopy))
                                    navDestination.value = "game"
                                    MainActivity.game = gameCopy
                                }
                            }
                        }
                    }
                    if(!gameAvailable){
                        createGame(timeControl, user?.username.toString())
                    }

                } else {
                    createGame(timeControl, user?.username.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun gameToMap(onlineGame: OnlineGame): Map<String, Any> {
        return mapOf<String, Any>(
            "joinable" to onlineGame.joinable,
            "whitePlayer" to onlineGame.whitePlayer,
            "blackPlayer" to onlineGame.blackPlayer,
            "timeControl" to onlineGame.timeControl,
            "previousMove" to onlineGame.previousMove,
            "resignation" to onlineGame.resignation,
            "drawOffer" to onlineGame.drawOffer,
            "rematchOffer" to onlineGame.rematchOffer,
            "cancel" to onlineGame.cancel,
            "players" to onlineGame.players
        )
    }

    private fun createGame(timeControl: Long, username: String) {
        val randomColor = (0..1).shuffled().random()
        var whitePlayer = ""
        var blackPlayer = ""
        var opponentColor = ""

        when (randomColor) {
            0 -> {
                whitePlayer = username
                userColor = "white"
                opponentColor = "blackPlayer"
            }
            1 -> {
                blackPlayer = username
                userColor = "black"
                opponentColor = "whitePlayer"
            }
        }


        val newGame = OnlineGame(
            joinable = true,
            whitePlayer = whitePlayer,
            blackPlayer = blackPlayer,
            timeControl = timeControl,
        )

        val id = "$username${System.currentTimeMillis()}"
        dbRepository.addGameToDatabase(newGame, id) {
            gameID = id
            waitForMatch(id, opponentColor)
        }

    }

    private fun waitForMatch(id: String, opponentColor: String) {
        gameReference = dbReference.child(id)
        gameReference?.onDisconnect()?.removeValue()
        matchSearch =
            dbReference.child(id).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val game = snapshot.getValue(OnlineGame::class.java)
                        if (opponentColor == "whitePlayer") {
                            if (game?.whitePlayer != "") {
                                MainActivity.game = game
                                navDestination.value = "game"
                                matchSearch?.let {
                                    dbReference.child(id).removeEventListener(it)
                                }
                            }
                        } else {
                            if (game?.blackPlayer != "") {
                                MainActivity.game = game
                                navDestination.value = "game"
                                matchSearch?.let {
                                    dbReference.child(id).removeEventListener(it)
                                }
                            }
                        }

                        if (game?.cancel == true) {
                            matchSearch?.let { dbReference.child(id).removeEventListener(it) }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun cancelSearch() {
        gameReference?.updateChildren(mapOf("cancel" to true))
        gameReference?.removeValue()
        navDestination.value = "home"
        resetSearch.value += 1
    }

    override fun onCleared() {
        cancelSearch()
        super.onCleared()
    }
}
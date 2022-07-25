package com.gomezdevlopment.chessnotationapp.realtime_database

import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.gameID
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.user
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.google.firebase.database.*
import java.io.Serializable
import javax.inject.Inject


class RealtimeDatabaseGameInteraction @Inject constructor(
    val db: FirebaseDatabase
) {
    val dbGameReference = db.getReference(OnlineGame::class.java.simpleName)
    private val dbUserReference = db.getReference(User::class.java.simpleName)

    fun writePromotion(
        playerTurn: String,
        string: String,
        promotionSelection: String
    ) {
        if (gameID != null) {
            if (playerTurn == MainActivity.userColor) {
                val previousMove = mapOf(
                    "previousMove" to "$string$promotionSelection"
                )
                dbGameReference.child(gameID.toString()).updateChildren(previousMove)
                    .addOnFailureListener {
                        println(it)
                    }
            }
        }
    }


    fun writeMove(playerTurn: String, string: String) {
        if (gameID != null) {
            if (playerTurn == MainActivity.userColor) {
                val previousMove = mapOf(
                    "previousMove" to string
                )
                dbGameReference.child(gameID.toString()).updateChildren(previousMove)
                    .addOnFailureListener {
                        println(it)
                    }
            }
        }
    }

    fun writeDrawOffer(playerTurn: String, value: String) {
        if (gameID != null) {
            val drawOffer = mapOf("drawOffer" to value)

            if (value == "accept" || value == "decline") {
                dbGameReference.child(gameID.toString()).updateChildren(drawOffer)
                    .addOnFailureListener {
                        println(it)
                    }
            }
            if (playerTurn == MainActivity.userColor) {
                dbGameReference.child(gameID.toString()).updateChildren(drawOffer)
                    .addOnFailureListener {
                        println(it)
                    }
            }
        }
    }

//    fun writeRematchOffer(playerTurn: String, value: String) {
//        if (gameID != null) {
//            if (playerTurn == MainActivity.userColor) {
//                MainActivity.gameDocumentReference?.update("rematchOffer", value)
//                    ?.addOnFailureListener { e ->
//                        println(e)
//                    }
//            }
//        }
//    }

    fun writeResignation(userColor: String) {
        if (gameID != null) {
            val resignation = mapOf("resignation" to userColor)
            dbGameReference.child(gameID.toString()).updateChildren(resignation)
                .addOnFailureListener {
                    println(it)
                }
        }
    }

    fun writeGame(game: Map<String, String>) {
        if (gameID != null) {
            val username = user?.username
            if (username != null) {
                dbUserReference.child(username).child("games").push().setValue(game)
                    .addOnFailureListener {
                        println(it)
                    }
            }
        }
    }

    fun incrementWins() {
        if (gameID != null) {
            val username = user?.username
            if (username != null) {
                val map = user
                val wins = map?.wins
                if (wins != null) {
                    val newWinCount = mapOf("wins" to wins + 1)
                    dbUserReference.child(username).updateChildren(newWinCount)
                        .addOnFailureListener { e ->
                            println(e)
                        }
                }
            }
        }
    }

    fun incrementLosses() {
        if (gameID != null) {
            val username = user?.username
            if (username != null) {
                val map = user
                val losses = map?.losses
                if (losses != null) {
                    val newLossCount = mapOf("losses" to losses + 1)
                    dbUserReference.child(username).updateChildren(newLossCount)
                        .addOnFailureListener { e ->
                            println(e)
                        }
                }
            }
        }
    }

    fun incrementDraws() {
        if (gameID != null) {
            val username = user?.username
            if (username != null) {
                val map = user
                val draws = map?.draws
                if (draws != null) {
                    val newDrawCount = mapOf("draws" to draws + 1)
                    dbUserReference.child(username).updateChildren(newDrawCount)
                        .addOnFailureListener { e ->
                            println(e)
                        }
                }
            }
        }
    }

//    fun writePuzzleRating(rating: Int) {
//        if (MainActivity.userDocumentReference != null) {
//            MainActivity.userDocumentReference?.update("puzzleRating", rating)
//        }
//    }
}
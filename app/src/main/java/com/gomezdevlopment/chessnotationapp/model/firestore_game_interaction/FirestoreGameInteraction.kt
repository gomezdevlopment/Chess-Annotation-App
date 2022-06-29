package com.gomezdevlopment.chessnotationapp.model.firestore_game_interaction

import android.media.Rating
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.google.firebase.firestore.FieldValue

class FirestoreGameInteraction {
    fun writePromotion(playerTurn: String, string: String, promotionSelection: String){
        if (MainActivity.gameDocumentReference != null) {
            if (playerTurn == MainActivity.userColor) {
                MainActivity.gameDocumentReference?.update("previousMove", "$string$promotionSelection")
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
        }
    }

    fun writeMove(playerTurn: String, string: String){
        if (MainActivity.gameDocumentReference != null) {
            if (playerTurn == MainActivity.userColor) {
                MainActivity.gameDocumentReference?.update("previousMove", string)
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
            else{
                println("not your turn ${MainActivity.userColor}")
            }
        }else{
            println("document null")
        }
    }

    fun writeDrawOffer(playerTurn: String, value: String){
        if (MainActivity.gameDocumentReference != null) {
            if(value == "accept" || value == "decline"){
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

    fun writeRematchOffer(playerTurn: String, value: String){
        if (MainActivity.gameDocumentReference != null) {
            if (playerTurn == MainActivity.userColor) {
                MainActivity.gameDocumentReference?.update("rematchOffer", value)
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
        }
    }

    fun writeResignation(userColor: String){
        if (MainActivity.gameDocumentReference != null) {
            //if (playerTurn == MainActivity.userColor) {
                MainActivity.gameDocumentReference?.update("resignation", userColor)
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
    }

    fun incrementWins(){
        if (MainActivity.userDocumentReference != null) {
            MainActivity.userDocumentReference?.update("wins", FieldValue.increment(1))
        }
    }

    fun incrementLosses(){
        if (MainActivity.userDocumentReference != null) {
            MainActivity.userDocumentReference?.update("losses", FieldValue.increment(1))
        }
    }

    fun incrementDraws(){
        if (MainActivity.userDocumentReference != null) {
            MainActivity.userDocumentReference?.update("draws", FieldValue.increment(1))
        }
    }

    fun writePuzzleRating(rating: Int){
        if (MainActivity.userDocumentReference != null) {
            MainActivity.userDocumentReference?.update("puzzleRating", rating)
        }
    }

}
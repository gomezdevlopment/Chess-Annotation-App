package com.gomezdevlopment.chessnotationapp.model.utils

import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.view.MainActivity

class FirestoreGameInteraction {
    fun writePromotion(playerTurn: String, string: String, promotionSelection: ChessPiece){
        if (MainActivity.gameDocumentReference != null) {
            if (playerTurn == MainActivity.userColor) {
                //val string = convertPromotionToString(previousSquare.value, promotionSelection.square)
                MainActivity.gameDocumentReference?.update("previousMove", "$string${promotionSelection.piece}")
                    ?.addOnSuccessListener {
                        println("Success")
                    }
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
                    ?.addOnSuccessListener {
                        println("Success")
                    }
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
        }
    }

    fun writeDrawOffer(playerTurn: String, value: String){
        if (MainActivity.gameDocumentReference != null) {
            if(value == "accept" || value == "decline"){
                MainActivity.gameDocumentReference?.update("drawOffer", value)
                    ?.addOnSuccessListener {
                        println("Success")
                    }
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
            if (playerTurn == MainActivity.userColor) {
                MainActivity.gameDocumentReference?.update("drawOffer", value)
                    ?.addOnSuccessListener {
                        println("Success")
                    }
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
                    ?.addOnSuccessListener {
                        println("Success")
                    }
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
        }
    }

    fun writeResignation(playerTurn: String, userColor: String){
        if (MainActivity.gameDocumentReference != null) {
            //if (playerTurn == MainActivity.userColor) {
                MainActivity.gameDocumentReference?.update("resignation", userColor)
                    ?.addOnSuccessListener {
                        println("Success")
                    }
                    ?.addOnFailureListener { e ->
                        println(e)
                    }
            }
       // }
    }
}
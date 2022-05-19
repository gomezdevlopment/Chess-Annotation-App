package com.gomezdevlopment.chessnotationapp.model

import android.graphics.drawable.VectorDrawable

class ChessPiece(
    val color: String,
    private val piece: String,
    val pieceDrawable: VectorDrawable,
    val rank: Int,
    val file: Int,
) {

    fun checkLegalMoves(piece: String){
        when (piece) {

            
        }
    }
}





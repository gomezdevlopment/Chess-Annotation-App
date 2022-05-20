package com.gomezdevlopment.chessnotationapp.model

import android.graphics.drawable.VectorDrawable

class ChessPiece(
    val color: String,
    private val piece: String,
    val pieceDrawable: Int,
    val square: Square
) {

    fun checkLegalMoves(pieces: List<ChessPiece>){
        when (piece) {
            "pawn" -> {
                pawnMoves(pieces)
            }
        }
    }

    private fun pawnMoves(pieces: List<ChessPiece>){
        val listOfMoves = mutableListOf<Square>()
        var square = Square(square.rank+1, square.file)
        val occupiedSquares = getPiecesOnBoard(pieces)
        if(!occupiedSquares.contains(square)){
            listOfMoves.add(square)
        }
        if(square.rank == 2){
            square = Square(square.rank+2, square.file)
            if(!occupiedSquares.contains(square)){
                listOfMoves.add(square)
            }
        }
        println(listOfMoves[0].file)
        println(listOfMoves[0].rank)
    }

    private fun getPiecesOnBoard(pieces: List<ChessPiece>): MutableList<Square> {
        val occupiedSquares = mutableListOf<Square>()
        for(piece in pieces){
            occupiedSquares.add(square)
        }
        return occupiedSquares
    }

}





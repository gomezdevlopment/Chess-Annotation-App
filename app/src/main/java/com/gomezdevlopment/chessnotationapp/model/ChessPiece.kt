package com.gomezdevlopment.chessnotationapp.model

import android.graphics.drawable.VectorDrawable

class ChessPiece(
    val color: String,
    private val piece: String,
    val pieceDrawable: Int,
    val square: Square
) {

    fun checkLegalMoves(occupiedSquares: HashMap<Square, ChessPiece>): List<Square>{
        var listOfMoves = mutableListOf<Square>()
        when (piece) {
            "pawn" -> {
                listOfMoves = pawnMoves(occupiedSquares) as MutableList<Square>
            }
            "rook" -> {
                listOfMoves = rookMoves(occupiedSquares) as MutableList<Square>
            }
            "knight" -> {
                listOfMoves = knightMoves(occupiedSquares) as MutableList<Square>
            }
        }
        return listOfMoves
    }

    private fun pawnMoves(occupiedSquares: HashMap<Square, ChessPiece>): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare = Square(square.rank+1, square.file)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        if(square.rank == 1){
            moveSquare = Square(square.rank+2, square.file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
        }
        return listOfMoves
    }

    private fun rookMoves(occupiedSquares: HashMap<Square, ChessPiece>): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square
        for(rank in 0..7){
            moveSquare = Square(rank, square.file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
        }
        for(file in 0..7){
            moveSquare = Square(square.rank, file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
        }
        return listOfMoves
    }

    private fun knightMoves(occupiedSquares: HashMap<Square, ChessPiece>): List<Square> {
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square = Square(square.rank+2, square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank+2, square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-2, square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-2, square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank+1, square.file+2)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-1, square.file+2)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank+1, square.file-2)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-1, square.file-2)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        println(listOfMoves)
        return listOfMoves
    }

    fun illegalMove(square: Square, occupiedSquares: HashMap<Square, ChessPiece>): Boolean {
        return square.rank > 7 || square.rank < 0 || square.file > 7 || square.file < 0 || occupiedSquares.contains(square)
    }

}





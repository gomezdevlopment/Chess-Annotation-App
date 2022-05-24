package com.gomezdevlopment.chessnotationapp.model.pieces

import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic

class Pawn {
    fun moves(piece: ChessPiece, hashMap: MutableMap<Square, ChessPiece>, squaresToBlock: MutableList<Square>): MutableList<Square> {
        val listOfMoves = mutableListOf<Square>()
        val gameLogic = GameLogic()

        var moveSquare = Square(piece.square.rank + 1, piece.square.file)
        if (piece.color == "black") {
            moveSquare = Square(piece.square.rank - 1, piece.square.file)
        }
        listOfMoves.add(moveSquare)

        if (piece.square.rank == 1 && piece.color == "white") {
            moveSquare = Square(piece.square.rank + 2, piece.square.file)
            listOfMoves.add(moveSquare)
        } else if (piece.square.rank == 6 && piece.color == "black") {
            moveSquare = Square(piece.square.rank - 2, piece.square.file)
            listOfMoves.add(moveSquare)
        }

        //Check for Diagonal Captures
        if (piece.color == "white") {
            moveSquare = Square(piece.square.rank + 1, piece.square.file - 1)
            if(hashMap.containsKey(moveSquare)) {
                listOfMoves.add(moveSquare)
            }

            moveSquare = Square(piece.square.rank + 1, piece.square.file + 1)
            if(hashMap.containsKey(moveSquare)){
                listOfMoves.add(moveSquare)
            }

        } else {
            moveSquare = Square(piece.square.rank - 1, piece.square.file - 1)
            if(hashMap.containsKey(moveSquare)){
                listOfMoves.add(moveSquare)
            }
            moveSquare = Square(piece.square.rank - 1, piece.square.file + 1)
            if(hashMap.containsKey(moveSquare)){
                listOfMoves.add(moveSquare)
            }
        }

        val moves = mutableListOf<Square>()
        for(move in listOfMoves){
            if(!gameLogic.illegalMove(move, hashMap, piece, squaresToBlock)){
                moves.add(move)
            }
        }
        return moves
    }
}
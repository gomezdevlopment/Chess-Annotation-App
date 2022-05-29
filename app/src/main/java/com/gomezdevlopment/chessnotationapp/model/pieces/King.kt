package com.gomezdevlopment.chessnotationapp.model.pieces

import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic

class King {
    private val gameLogic = GameLogic()

    fun moves(
        piece: ChessPiece,
        hashMap: MutableMap<Square, ChessPiece>,
        squaresToBlock: MutableList<Square>,
        attackedSquares: MutableList<Square>,
        kingCanCastleKingSide: Boolean,
        kingCanCastleQueenSide: Boolean,
        xRayAttacks: MutableList<Square>,
        kingSquare:Square,
        checksOnKing: MutableList<Square>,
        piecesCheckingKing: MutableList<Square>
    ): MutableList<Square> {
        val listOfMoves = mutableListOf<Square>()
        var moveSquare = Square(piece.square.rank + 1, piece.square.file + 1)
        listOfMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank + 1, piece.square.file - 1)
        listOfMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank + 1, piece.square.file)
        listOfMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank - 1, piece.square.file)
        listOfMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank, piece.square.file + 1)
        listOfMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank, piece.square.file - 1)
        listOfMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank - 1, piece.square.file - 1)
        listOfMoves.add(moveSquare)
        moveSquare = Square(piece.square.rank - 1, piece.square.file + 1)
        listOfMoves.add(moveSquare)

        val moves = mutableListOf<Square>()
        for (move in listOfMoves) {
            if (!gameLogic.illegalMove(move, hashMap, piece, squaresToBlock, xRayAttacks, kingSquare, piecesCheckingKing) &&
                !attackedSquares.contains(move)) {
                if(attackedSquares.contains(kingSquare) && checksOnKing.contains(move)) break
                moves.add(move)
            }
        }
        
        //King Side Castling
        moveSquare = Square(piece.square.rank, piece.square.file + 2)
        if(kingCanCastleKingSide){
            if(gameLogic.canKingSideCastle(moveSquare, hashMap, attackedSquares)){
                moves.add(moveSquare)
            }
        }
        //Queen Side Castling
        moveSquare = Square(piece.square.rank, piece.square.file - 2)
        if(kingCanCastleQueenSide){
            if(gameLogic.canQueenSideCastle(moveSquare, hashMap, attackedSquares)){
                moves.add(moveSquare)
            }
        }
        return moves
    }
}
package com.gomezdevlopment.chessnotationapp.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.R


class GameRepository: ViewModel() {
    private val piecesOnBoard: MutableList<ChessPiece> = mutableListOf()
    private val  hashMap : MutableMap<Square, ChessPiece> = HashMap()
    private var previousSquare : MutableState<Square> = mutableStateOf(Square(10,10))

    init {
        piecesOnBoard.add(ChessPiece("white", "rook", R.drawable.ic_wr_alpha, Square(0,0)))
        piecesOnBoard.add(ChessPiece("white", "rook", R.drawable.ic_wr_alpha, Square(0,7)))
        piecesOnBoard.add(ChessPiece("white", "knight", R.drawable.ic_wn_alpha, Square(0,1)))
        piecesOnBoard.add(ChessPiece("white", "knight", R.drawable.ic_wn_alpha, Square(0,6)))
        piecesOnBoard.add(ChessPiece("white", "bishop", R.drawable.ic_wb_alpha, Square(0,2)))
        piecesOnBoard.add(ChessPiece("white", "bishop", R.drawable.ic_wb_alpha, Square(0,5)))
        piecesOnBoard.add(ChessPiece("white", "queen", R.drawable.ic_wq_alpha, Square(0,3)))
        piecesOnBoard.add(ChessPiece("white", "king", R.drawable.ic_wk, Square(0,4)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1,0)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1,1)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1,2)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1,3)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1,4)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1,5)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1,6)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1,7)))

        piecesOnBoard.add(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(7,0)))
        piecesOnBoard.add(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(7,7)))
        piecesOnBoard.add(ChessPiece("black", "knight", R.drawable.ic_bn_alpha, Square(7,1)))
        piecesOnBoard.add(ChessPiece("black", "knight", R.drawable.ic_bn_alpha, Square(7,6)))
        piecesOnBoard.add(ChessPiece("black", "bishop", R.drawable.ic_bb_alpha, Square(7,2)))
        piecesOnBoard.add(ChessPiece("black", "bishop", R.drawable.ic_bb_alpha, Square(7,5)))
        piecesOnBoard.add(ChessPiece("black", "queen", R.drawable.ic_bq_alpha, Square(7,4)))
        piecesOnBoard.add(ChessPiece("black", "king", R.drawable.ic_bk_alpha, Square(7,3)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6,0)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6,1)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6,2)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6,3)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6,4)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6,5)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6,6)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6,7)))

        for(piece in piecesOnBoard){
            hashMap[piece.square] = piece
        }

    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece){
        val previousSquare = piece.square
        hashMap.remove(piece.square)
        piece.square = newSquare
        hashMap[newSquare] = piece
        setPreviousSquare(previousSquare)
    }
    fun getPiecesOnBoard(): MutableList<ChessPiece> {
        return piecesOnBoard
    }

    fun getHashMap(): MutableMap<Square, ChessPiece> {
        return hashMap
    }

    fun getPreviousSquare() : MutableState<Square> {
        return previousSquare
    }

    private fun setPreviousSquare(square: Square){
        previousSquare.value = square
    }
}